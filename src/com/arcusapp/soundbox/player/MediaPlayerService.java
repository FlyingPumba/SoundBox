/*
 * SoundBox - Android Music Player
 * Copyright (C) 2013 Iván Arcuschin Moreno
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

import android.app.AlarmManager;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.os.Binder;
import android.os.Bundle;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.SystemClock;
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
    public static final String INCOMMING_CALL = "com.arcusapp.soundbox.action.MEDIA_PLAYER_SERVICE.INCOMMING_CALL";
    public static final String PLAY_NEW_SONGS = "com.arcusapp.soundbox.action.MEDIA_PLAYER_SERVICE.PLAY_NEW_SONGS";
    public static final String CHANGE_FOREGROUND_STATE = "com.arcusapp.soundbox.player.MEDIA_PLAYER_SERVICE.CHANGE_FOREGROUND_STATE";
    public static final String NOW_IN_FOREGROUND = "now_in_foreground";

    /**
     * Possible actions that can be called from the MediaPlayerNotification
     */
    public static final String TOGGLEPLAYPAUSE_ACTION = "com.arcusapp.soundbox.action.MEDIA_PLAYER_SERVICE.TOGGLEPLAYPAUSE";
    public static final String PREVIOUS_ACTION = "com.arcusapp.soundbox.player.MEDIA_PLAYER_SERVICE.PRVIOUS";
    public static final String NEXT_ACTION = "com.arcusapp.soundbox.player.MEDIA_PLAYER_SERVICE.NEXT";
    public static final String STOP_ACTION = "com.arcusapp.soundbox.player.MEDIA_PLAYER_SERVICE.STOP";

    /**
     * Idle time before stopping the foreground notfication (1 minute)
     */
    private static final int IDLE_DELAY = 60000;

    private BroadcastReceiver headsetReceiver;

    // private int currentSongPosition;
    private SongStack currentSongStack;
    private List<String> songsIDList;

    private RepeatState repeatState = RepeatState.Off;
    private RandomState randomState = RandomState.Off;

    private MediaPlayer mediaPlayer;
    private List<MediaPlayerServiceListener> currentListeners;
    private final IBinder mBinder = new MyBinder();

    private boolean isOnForeground = false;
    MediaPlayerNotification mNotification;

    /**
     * Alarm intent for removing the notification when nothing is playing
     * for some time
     */
    private AlarmManager mAlarmManager;
    private PendingIntent mShutdownIntent;
    private boolean mShutdownScheduled;

    /**
     * Used to know when the service is active
     */
    private boolean mServiceInUse = false;

    /**
     * Used to know if something should be playing or not
     */
    private boolean mIsSupposedToBePlaying = false;

    private int mServiceStartId = -1;

    // Called every time a client starts the service using startService
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        mServiceStartId = startId;

        if(intent != null) {
            handleCommandIntent(intent);
        }

        // Make sure the service will shut down on its own if it was
        // just started but not bound to and nothing is playing
        if(!isPlaying()) {
            Log.d(this.getClass().getName(), "scheduleDelayedShutdown from onStartCommand");
            scheduleDelayedShutdown();
        }

        // We want this service to continue running until it is explicitly stopped, so return sticky.
        return Service.START_STICKY;
    }

    private void handleCommandIntent(Intent intent) {
        String action = intent.getAction();

        // Check if this is an intent from the AudioBecomingNoisyHandler
        if(INCOMMING_CALL.equals(action)) {
            if (mediaPlayer != null) {
                if(mediaPlayer.isPlaying()) {
                    mediaPlayer.pause();
                    mIsSupposedToBePlaying = false;
                    Log.d(this.getClass().getName(), "scheduleDelayedShutdown from INCOMMING_CALL");
                    scheduleDelayedShutdown();
                    fireListenersOnMediaPlayerStateChanged();
                }
            }
        } // Check if we have a request to play new songs
        else if(PLAY_NEW_SONGS.equals(action)) {
            Bundle bundle = intent.getExtras();
            String currentID = BundleExtra.getBundleString(bundle, BundleExtra.CURRENT_ID, BundleExtra.DefaultValues.DEFAULT_ID);
            List<String> songsID = bundle.getStringArrayList(BundleExtra.SONGS_ID_LIST);

            loadSongs(songsID, currentID);
            mediaPlayer.start();
            mIsSupposedToBePlaying = true;
            Log.d(this.getClass().getName(), "cancelShutdown from PLAY_NEW_SONGS");
            cancelShutdown();
        } // Check if the foreground state needs to be changed
        else if (CHANGE_FOREGROUND_STATE.equals(action)) {
            boolean nowInForeground = intent.getBooleanExtra(NOW_IN_FOREGROUND, false);
            if(nowInForeground) {
                // check if we are playing. In case we are not, stop the service
                if(isPlaying()) {
                    Song currentSong = getCurrentSong();
                    Notification notification = mNotification.getNotification(currentSong.getArtist(), currentSong.getAlbum(), currentSong.getTitle(), mIsSupposedToBePlaying);
                    startForeground(MediaPlayerNotification.MEDIA_PLAYER_NOTIFICATION_ID, notification);
                    isOnForeground = true;
                } else {
                    Log.d(this.getClass().getName(), "scheduleDelayedShutdown from CHANGE_FOREGROUND_STATE");
                    scheduleDelayedShutdown();
                }
            } else {
                stopForeground(true);
                isOnForeground = false;
            }
        }

        //Check if there is an action from the MediaPlayerNotification
        if(isOnForeground) {
            if(TOGGLEPLAYPAUSE_ACTION.equals(action)) {
                playAndPause();
            } else if(NEXT_ACTION.equals(action)) {
                playNextSong();
            } else if(PREVIOUS_ACTION.equals(action)) {
                playPreviousSong();
            } else if(STOP_ACTION.equals(action)) {
                stopForeground(true);
                if (!mServiceInUse) {
                    stopSelf(mServiceStartId);
                }
            }
        }
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
        if(mNotification == null){
            mNotification = new MediaPlayerNotification();
        }

        // Start up the thread running the service. Note that we create a
        // separate thread because the service normally runs in the process's
        // main thread, which we don't want to block. We also make it
        // background priority so CPU-intensive work will not disrupt the UI.
        final HandlerThread thread = new HandlerThread("MusicPlayerHandler",
                android.os.Process.THREAD_PRIORITY_BACKGROUND);
        thread.start();

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
                                mIsSupposedToBePlaying = false;
                                fireListenersOnMediaPlayerStateChanged();
                            }
                        }
                    }
                }
            };
            registerReceiver(headsetReceiver, new IntentFilter(Intent.ACTION_HEADSET_PLUG));
        }

        // Initialize the delayed shutdown intent
        final Intent shutdownIntent = new Intent(this, MediaPlayerService.class);
        shutdownIntent.setAction(STOP_ACTION);

        mAlarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        mShutdownIntent = PendingIntent.getService(this, 0, shutdownIntent, 0);

        // Listen for the idle state
        Log.d(this.getClass().getName(), "scheduleDelayedShutdown from onCreate");
        scheduleDelayedShutdown();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        // remove any pending alarms
        mAlarmManager.cancel(mShutdownIntent);

        // Release the player
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mIsSupposedToBePlaying = false;
            mediaPlayer.release();
            mediaPlayer = null;
        }

        //Unregister headset listener
        if(headsetReceiver != null) {
            unregisterReceiver(headsetReceiver);
        }
    }

    @Override
    public IBinder onBind(Intent arg0) {
        Log.d(this.getClass().getName(), "cancelShutdown from onBind");
        cancelShutdown();
        mServiceInUse = true;
        return mBinder;
    }

    @Override
    public boolean onUnbind(Intent arg0) {
        mServiceInUse = false;

        if (mIsSupposedToBePlaying ) {
            // Something is currently playing, or will be playing once
            // an in-progress action requesting audio focus ends, so don't stop
            // the service now.
            return true;

            // If there is a playlist but playback is paused, then wait a while
            // before stopping the service.
        } else if (currentSongStack!= null && currentSongStack.getCurrentSongsIDList().size() > 0) {
            Log.d(this.getClass().getName(), "scheduleDelayedShutdown from onUnbid");
            scheduleDelayedShutdown();
            return true;
        }
        stopSelf(mServiceStartId);
        return true;
    }

    @Override
    public void onRebind(final Intent intent) {
        Log.d(this.getClass().getName(), "cancelShutdown from onRebind");
        cancelShutdown();
        mServiceInUse = true;
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
        return mIsSupposedToBePlaying;
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
            mIsSupposedToBePlaying = true;
            Log.d(this.getClass().getName(), "cancelShutdown from playAndPause");
            cancelShutdown();
        }
        else {
            mediaPlayer.pause();
            mIsSupposedToBePlaying = false;
            Log.d(this.getClass().getName(), "scheduleDelayedShutdown from playAndPause");
            scheduleDelayedShutdown();
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
                    Song currentSong = currentSongStack.getCurrentSong();
                    mediaPlayer.reset();
                    mediaPlayer.setDataSource(currentSong.getFile().getPath());
                    mediaPlayer.prepare();
                    Log.d(this.getClass().getName(), "scheduleDelayedShutdown from playNextSong");
                    scheduleDelayedShutdown();
                    mIsSupposedToBePlaying = false;
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
        mIsSupposedToBePlaying = true;
        Log.d(this.getClass().getName(), "cancelShutdown from playCurrentSong");
        cancelShutdown();
        fireListenersOnMediaPlayerStateChanged();
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
        if(currentListeners != null){
            for (MediaPlayerServiceListener listener : currentListeners) {
                listener.onMediaPlayerStateChanged();
            }
        }
        // if we are playing on foreground, update the notification
        if(isOnForeground) {
            Song currentSong = getCurrentSong();
            mNotification.updateNotification(currentSong.getArtist(), currentSong.getAlbum(), currentSong.getTitle(), mIsSupposedToBePlaying);
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
        if(isOnForeground) {
            stopForeground(true);
        }
    }

    private void scheduleDelayedShutdown() {
        mAlarmManager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP,
                SystemClock.elapsedRealtime() + IDLE_DELAY, mShutdownIntent);
        mShutdownScheduled = true;
    }

    private void cancelShutdown() {
        if (mShutdownScheduled) {
            mAlarmManager.cancel(mShutdownIntent);
            mShutdownScheduled = false;
        }
    }
}