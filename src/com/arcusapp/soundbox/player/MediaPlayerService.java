package com.arcusapp.soundbox.player;

import java.util.ArrayList;
import java.util.List;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import com.arcusapp.soundbox.data.SoundBoxPreferences;
import com.arcusapp.soundbox.model.BundleExtra;
import com.arcusapp.soundbox.model.MediaPlayerServiceListener;
import com.arcusapp.soundbox.model.RandomState;
import com.arcusapp.soundbox.model.RepeatState;
import com.arcusapp.soundbox.model.Song;

public class MediaPlayerService extends Service implements OnCompletionListener {

    private static final String TAG = "MediaPlayerService";

    // private int currentSongPosition;
    private SongStack currentSongStack;
    private List<String> songsIDList;

    private RepeatState repeatState = RepeatState.Off;
    private RandomState randomState = RandomState.Off;

    private MediaPlayer mediaPlayer;
    private List<MediaPlayerServiceListener> currentListeners;
    private final IBinder mBinder = new MyBinder();

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // Called every time a client starts the service using startService
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
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }

    @Override
    public IBinder onBind(Intent arg0) {
        return mBinder;
    }

    @Override
    public boolean onUnbind(Intent arg0) {
        currentListeners = null;
        return true;
    }

    public class MyBinder extends Binder {
        public MediaPlayerService getService() {
            return MediaPlayerService.this;
        }
    }

    public void registerListener(MediaPlayerServiceListener listener) {
        this.currentListeners.add(listener);
    }

    // FIXME: Split this method in two differents, playSongs and LoadSongs
    public void playSongs(String currentSongID, List<String> songsID) {
        if (songsID.size() == 0) {
            Log.d(TAG, "No songs to play");
            return;
        }

        songsIDList = songsID;

        // store the songs on preferences
        SoundBoxPreferences.LastSongs.setLastSongs(songsIDList);
        
        int currentSongPosition;
        if (currentSongID.equals(BundleExtra.DefaultValues.DEFAULT_ID)) {
            currentSongPosition = 0;
        } else {
            currentSongPosition = songsID.indexOf(currentSongID);
        }

        // create the song stack
        // FIXME: when this method is called from PlayFragment we don't have to alter the list of sonsID (perhaps fixed with LoadSongs method)
        currentSongStack = new SongStack(currentSongPosition, this.songsIDList, randomState);

        playCurrentSong();
    }

    public Song getCurrentSong() {
        if (currentSongStack != null) {
            return currentSongStack.getCurrentSong();
        } else {
            return null;
        }
    }

    public List<String> getSongsIDList() {
        if (currentSongStack.getCurrentRandomState() == RandomState.Random) {
            return songsIDList;
        } else {
            return currentSongStack.getCurrentSongsIDList();
        }

    }

    public boolean isPlaying() {
        return mediaPlayer.isPlaying();
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
        }
        else {
            mediaPlayer.pause();
        }
    }

    public void playNextSong() {
        currentSongStack.moveStackForward();
        // check if we started the playlist again
        if (currentSongStack.getCurrentSong().getID().equals(currentSongStack.getCurrentSongsIDList().get(0))) {
            if (repeatState == RepeatState.Off) {
                // prepare the first song of the list, but do not play it.
                mediaPlayer.stop();
                Song currentSong = currentSongStack.getCurrentSong();
                try {
                    mediaPlayer.reset();
                    mediaPlayer.setDataSource(currentSong.getFile().getPath());
                    mediaPlayer.prepare();
                } catch (Exception e) {
                    Log.d(TAG, "Wrong file path on the first song");
                }
                fireListenersOnSongCompletion();

            } else {
                playCurrentSong();
            }
        } else {
            playCurrentSong();
        }
    }

    public void playPreviousSong() {
        currentSongStack.moveStackBackward();
        playCurrentSong();
    }

    @Override
    public void onCompletion(MediaPlayer arg0) {
        try {
            if (repeatState == RepeatState.One) {
                playCurrentSong();
            } else {
                playNextSong();
                fireListenersOnSongCompletion();
            }
        }
        catch (Exception ex) {
            fireListenersOnErrorRaised(ex);
        }
    }

    private void playCurrentSong() {
        if (mediaPlayer.isPlaying()) {
            mediaPlayer.stop();
        }

        // play the song
        Song currentSong = currentSongStack.getCurrentSong();
        SoundBoxPreferences.LastPlayedSong.setLastPlayedSong(currentSong.getID());
        try {
            mediaPlayer.reset();
            mediaPlayer.setDataSource(currentSong.getFile().getPath());
            mediaPlayer.prepare();
        } catch (Exception e) {
            Log.d(TAG, "Wrong file path on the first song");
        }

        mediaPlayer.start();
        fireListenersOnSongCompletion();
    }
    
    private void fireListenersOnSongCompletion() {
        for (MediaPlayerServiceListener listener : currentListeners) {
            listener.onSongCompletion();            
        }
    }
    
    private void fireListenersOnErrorRaised(Exception ex) {
        for (MediaPlayerServiceListener listener : currentListeners) {
            listener.onExceptionRaised(ex);            
        }
    }
}