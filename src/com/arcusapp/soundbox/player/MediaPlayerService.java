/*
 * SoundBox - Android Music Player
 * Copyright (C) 2013  Iván Arcuschin Moreno
 *
 * This file is part of SoundBox.
 *
 * SoundBox is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 2 of the License, or
 * (at your option) any later version.
 *
 * SoundBox is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with SoundBox.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.arcusapp.soundbox.player;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;

import com.arcusapp.soundbox.data.SoundBoxPreferences;
import com.arcusapp.soundbox.model.BundleExtra;
import com.arcusapp.soundbox.model.MediaPlayerServiceListener;
import com.arcusapp.soundbox.model.RandomState;
import com.arcusapp.soundbox.model.RepeatState;
import com.arcusapp.soundbox.model.Song;

import java.util.ArrayList;
import java.util.List;

public class MediaPlayerService extends Service implements OnCompletionListener {

    private static final String TAG = "MediaPlayerService";
    public static final String INCOMMING_CALL = "incomming_call";
    public static final String PLAY_NEW_SONGS = "play_new_songs";
    public static final String NOW_IN_FOREGROUND = "now_in_foreground";

    private BroadcastReceiver headsetReceiver;

    // private int currentSongPosition;
    private SongStack currentSongStack;
    private List<String> songsIDList;

    private RepeatState repeatState = RepeatState.Off;
    private RandomState randomState = RandomState.Off;

    private boolean isPlaying = false;

    private MediaPlayer mediaPlayer;
    private List<MediaPlayerServiceListener> currentListeners;
    private final IBinder mBinder = new MyBinder();

    private boolean isOnForeground = false;

    // Called every time a client starts the service using startService
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // Check if this is an intent from the AudioBecomingNoisyHandler
        if(intent != null) {
            if(intent.getBooleanExtra(INCOMMING_CALL, false)) {
                if (mediaPlayer != null) {
                    if(mediaPlayer.isPlaying()) {
                        mediaPlayer.pause();
                        isPlaying = false;
                        fireListenersOnMediaPlayerStateChanged();
                    }
                }
            } else if(intent.getBooleanExtra(PLAY_NEW_SONGS, false)) {
                Bundle bundle = intent.getExtras();
                String currentID = BundleExtra.getBundleString(bundle, BundleExtra.CURRENT_ID, BundleExtra.DefaultValues.DEFAULT_ID);
                List<String> songsID = bundle.getStringArrayList(BundleExtra.SONGS_ID_LIST);

                loadSongs(songsID, currentID);
                mediaPlayer.start();
                isPlaying = true;
            }

            boolean nowInForeground = intent.getBooleanExtra(NOW_IN_FOREGROUND, false);

            if(isOnForeground && !nowInForeground) {
                stopForeground(true);
                isOnForeground = false;
            } else if (!isOnForeground && nowInForeground && isPlaying) {
                MediaPlayerNotification notification = new MediaPlayerNotification();
                startForeground(1337, notification.getNotification());
                isOnForeground = true;
            }
        }

        // We want this service to continue running until it is explicitly stopped, so return sticky.
        return Service.START_STICKY;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        // Called when the Service object is instantiated. Theoretically, only once.
        if (mediaPlayer == null) {
            mediaPlayer = new MediaPlayer();
            mediaPlayer.setOnCompletionListener(this);
        }
        if(currentListeners == null) {
            currentListeners = new ArrayList<MediaPlayerServiceListener>();
        }

        FetchLastPlayedSongs();

        if(headsetReceiver == null) {
            headsetReceiver = new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    // state - 0 for unplugged, 1 for plugged.
                    int state = intent.getIntExtra("state", 0);
                    if(state == 0) {
                        if (mediaPlayer != null) {
                            if(mediaPlayer.isPlaying()) {
                                mediaPlayer.pause();
                                isPlaying = false;
                                fireListenersOnMediaPlayerStateChanged();
                            }
                        }
                    }
                }
            };
            registerReceiver(headsetReceiver, new IntentFilter(Intent.ACTION_HEADSET_PLUG));
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            isPlaying = false;
            mediaPlayer.release();
            mediaPlayer = null;
        }
        if(headsetReceiver != null) {
            unregisterReceiver(headsetReceiver);
        }
    }

    @Override
    public IBinder onBind(Intent arg0) {
        return mBinder;
    }

    @Override
    public boolean onUnbind(Intent arg0) {
        return true;
    }

    public class MyBinder extends Binder {
        public MediaPlayerService getService() {
            return MediaPlayerService.this;
        }
    }

    public void registerListener(MediaPlayerServiceListener listener) {
        if(listener == null || currentListeners == null){
            stopSelf();
            return;
        }

        if(!currentListeners.contains(listener) ) {
            currentListeners.add(listener);
        }
    }
    
    public void unRegisterListener(MediaPlayerServiceListener listener) {
        if(listener == null || currentListeners == null){
            stopSelf();
            return;
        }
        currentListeners.remove(listener);
    }

    private void FetchLastPlayedSongs() {
        List<String> songsID = SoundBoxPreferences.LastSongs.getLastSongs();
        String lastSong = SoundBoxPreferences.LastPlayedSong.getLastPlayedSong();

        loadSongs(songsID, lastSong);
    }

    public void loadSongs(List<String> songsID, String currentSongID) {
        if (songsID.isEmpty()) {
            Log.d(TAG, "No songs to play");
            return;
        }
        this.songsIDList = new ArrayList<String>();
        this.songsIDList.addAll(songsID);

        int currentSongPosition;
        if (currentSongID.equals(BundleExtra.DefaultValues.DEFAULT_ID)) {
            currentSongPosition = 0;
        } else {
            currentSongPosition = this.songsIDList.indexOf(currentSongID);
            if(currentSongPosition == -1) {
                Log.d(TAG, "The first song to play is not in the loaded songs");
                return;
            }
        }

        // create the song stack
        currentSongStack = new SongStack(currentSongPosition, this.songsIDList, randomState);

        prepareMediaPlayer();
    }

    public Song getCurrentSong() {
        if (currentSongStack != null) {
            return currentSongStack.getCurrentSong();
        } else {
            return null;
        }
    }

    public boolean isPlaying() {
        return isPlaying;
    }

    public RandomState getRandomState() {
        return currentSongStack.getCurrentRandomState();
    }

    public RepeatState getRepeatState() {
        return repeatState;
    }

    public RandomState changeRandomState() {
        if (randomState == RandomState.Off) {
            randomState = RandomState.Shuffled;
        }
        else if (randomState == RandomState.Shuffled) {
            randomState = RandomState.Random;
        }
        else if (randomState == RandomState.Random) {
            randomState = RandomState.Off;
        }
        currentSongStack.setRandomState(randomState);
        fireListenersOnMediaPlayerStateChanged();
        return randomState;
    }

    public RepeatState changeRepeatState() {
        if (repeatState == RepeatState.Off) {
            repeatState = RepeatState.All;
        }
        else if (repeatState == RepeatState.All) {
            repeatState = RepeatState.One;
        }
        else if (repeatState == RepeatState.One) {
            repeatState = RepeatState.Off;
        }
        fireListenersOnMediaPlayerStateChanged();
        return repeatState;
    }

    public void seekTo(int position) {
        mediaPlayer.seekTo(position);
    }

    public int getCurrentPosition() {
        return mediaPlayer.getCurrentPosition();
    }

    public int getDuration() {
        return mediaPlayer.getDuration();
    }

    public void playAndPause() {
        if (!mediaPlayer.isPlaying()) {
            mediaPlayer.start();
            isPlaying = true;
        }
        else {
            mediaPlayer.pause();
            isPlaying = false;
        }
        fireListenersOnMediaPlayerStateChanged();
    }

    public void playNextSong() {
        try {
            currentSongStack.moveStackForward();
            // check if we started the playlist again
            if (currentSongStack.getCurrentSong().getID().equals(currentSongStack.getCurrentSongsIDList().get(0))) {
                if (repeatState == RepeatState.Off) {
                    // prepare the first song of the list, but do not play it.
                    mediaPlayer.stop();
                    isPlaying = false;
                    Song currentSong = currentSongStack.getCurrentSong();
                    mediaPlayer.reset();
                    mediaPlayer.setDataSource(currentSong.getFile().getPath());
                    mediaPlayer.prepare();
                    fireListenersOnMediaPlayerStateChanged();

                } else {
                    playCurrentSong();
                }
            } else {
                playCurrentSong();
            }
        } catch (Exception e) {
            fireListenersOnErrorRaised(e);
        }
    }

    public void playPreviousSong() {
        try {
            currentSongStack.moveStackBackward();
            playCurrentSong();
        } catch (Exception e) {
            fireListenersOnErrorRaised(e);
        }
    }

    public List<String> getSongsIDList() {
        if (currentSongStack.getCurrentRandomState() == RandomState.Random) {
            return songsIDList;
        } else {
            return currentSongStack.getCurrentSongsIDList();
        }
    }

    @Override
    public void onCompletion(MediaPlayer arg0) {
        try {
            if (repeatState == RepeatState.One) {
                playCurrentSong();
            } else {
                playNextSong();
                fireListenersOnMediaPlayerStateChanged();
            }
        }
        catch (Exception ex) {
            fireListenersOnErrorRaised(ex);
        }
    }

    private void playCurrentSong() {
        prepareMediaPlayer();
        mediaPlayer.start();
        isPlaying = true;
    }

    private void prepareMediaPlayer() {
        try {
            if (mediaPlayer.isPlaying()) {
                mediaPlayer.stop();
            }
            // play the song
            Song currentSong = currentSongStack.getCurrentSong();
            SoundBoxPreferences.LastPlayedSong.setLastPlayedSong(currentSong.getID());

            mediaPlayer.reset();
            mediaPlayer.setDataSource(currentSong.getFile().getPath());
            mediaPlayer.prepare();

            fireListenersOnMediaPlayerStateChanged();
        } catch (Exception e) {
            fireListenersOnErrorRaised(e);
        }
    }
    
    private void fireListenersOnMediaPlayerStateChanged() {
        if(currentListeners == null){
            stopSelf();
            return;
        }
        for (MediaPlayerServiceListener listener : currentListeners) {
            listener.onMediaPlayerStateChanged();            
        }
    }

    private void fireListenersOnErrorRaised(Exception ex) {
        if(currentListeners == null){
            stopSelf();
            return;
        }
        for (MediaPlayerServiceListener listener : currentListeners) {
            listener.onExceptionRaised(ex);            
        }
    }
}