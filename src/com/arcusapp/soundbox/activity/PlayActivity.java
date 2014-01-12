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

package com.arcusapp.soundbox.activity;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

import com.arcusapp.soundbox.R;
import com.arcusapp.soundbox.SoundBoxApplication;
import com.arcusapp.soundbox.model.BundleExtra;
import com.arcusapp.soundbox.model.MediaPlayerServiceListener;
import com.arcusapp.soundbox.model.RandomState;
import com.arcusapp.soundbox.model.RepeatState;
import com.arcusapp.soundbox.model.Song;
import com.arcusapp.soundbox.player.MediaPlayerService;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class PlayActivity extends Activity implements OnClickListener {

    private TextView txtTitle, txtArtist, txtAlbum, txtTimeCurrent, txtTimeTotal;
    private ImageButton btnSwitchRandom, btnSwitchRepeat, btnPlayAndPause;
    private SeekBar seekBar;

    private MediaPlayerService mediaService;
    private ServiceConnection myServiceConnection;
    private MediaPlayerServiceListener serviceListener;

    private Song currentSong;
    private Handler myHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play);

        serviceListener = new MediaPlayerServiceListener() {
            @Override
            public void onMediaPlayerStateChanged() {
                updateUI();
            }

            @Override
            public void onExceptionRaised(Exception ex) {
                finish();
            }
        };

        initUI();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.play, menu);
        return true;
    }

    @Override
    protected void onStart() {
        super.onStart();
        SoundBoxApplication.notifyForegroundStateChanged(true);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mediaService == null) {
            bindMediaPlayerService();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mediaService != null) {
            mediaService.unRegisterListener(serviceListener);
            unbindService(myServiceConnection);
            mediaService = null;
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        SoundBoxApplication.notifyForegroundStateChanged(false);
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent();
        intent.setAction(SoundBoxApplication.ACTION_MAIN_ACTIVITY);
        intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        startActivity(intent);

        super.onBackPressed();
    }

    private void bindMediaPlayerService() {
        myServiceConnection = new ServiceConnection() {
            public void onServiceConnected(ComponentName className, IBinder binder) {
                mediaService = ((MediaPlayerService.MyBinder) binder).getService();

                mediaService.registerListener(serviceListener);
                updateUI();
                initRunnableSeekBar();
            }

            public void onServiceDisconnected(ComponentName className) {
                mediaService = null;
            }
        };

        Intent intent = new Intent();
        intent.setAction(SoundBoxApplication.ACTION_MEDIA_PLAYER_SERVICE);
        bindService(intent, myServiceConnection, Context.BIND_AUTO_CREATE);
    }

    private void initUI() {
        txtTitle = (TextView) findViewById(R.id.txtSongTitle);
        txtTitle.setTypeface(null, Typeface.BOLD);
        txtArtist = (TextView) findViewById(R.id.txtSongArtist);
        txtAlbum = (TextView) findViewById(R.id.txtSongAlbum);
        txtTimeCurrent = (TextView) findViewById(R.id.txtTimeCurrent);
        txtTimeTotal = (TextView) findViewById(R.id.txtTimeTotal);

        btnSwitchRandom = (ImageButton) findViewById(R.id.btnSwitchRandom);
        btnSwitchRandom.setOnClickListener(this);

        btnSwitchRepeat = (ImageButton) findViewById(R.id.btnSwitchRepeat);
        btnSwitchRepeat.setOnClickListener(this);

        btnPlayAndPause = (ImageButton) findViewById(R.id.btnPlayPause);

        // init the seekbar
        seekBar = (SeekBar) findViewById(R.id.seekBar);
        seekBar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) { }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) { }

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    if(mediaService != null) {
                        mediaService.seekTo(progress);
                        seekBar.setProgress(progress);
                    }
                }
            }
        });
    }

    @Override
    public void onClick(View v) {
        if(currentSong == null || mediaService == null) {
            finish();
            return;
        }

        if (v.getId() == R.id.btnPlayPause) {
            mediaService.playAndPause();
        }
        else if (v.getId() == R.id.btnPrevSong) {
            mediaService.playPreviousSong();

        }
        else if (v.getId() == R.id.btnNextSong) {
            mediaService.playNextSong();
        }
        else if (v.getId() == R.id.btnSwitchRandom) {
            mediaService.changeRandomState();
            //Toast.makeText(this, randomStateToText(mediaService.getRandomState()), Toast.LENGTH_SHORT).show();
        }
        else if (v.getId() == R.id.btnSwitchRepeat) {
            mediaService.changeRepeatState();
            //Toast.makeText(this, repeatStateToText(mediaService.getRepeatState()), Toast.LENGTH_SHORT).show();
        }
        else if (v.getId() == R.id.btnCurrentPlayList) {
            Intent intent = new Intent();
            intent.setAction(SoundBoxApplication.ACTION_SONGSLIST_ACTIVITY);

            Bundle mExtras = new Bundle();
            List<String> songsID = mediaService.getSongsIDList();
            mExtras.putStringArrayList(BundleExtra.SONGS_ID_LIST, (ArrayList<String>)songsID);
            String currentSongID = mediaService.getCurrentSong().getID();
            mExtras.putString(BundleExtra.CURRENT_ID, currentSongID);

            intent.putExtras(mExtras);
            startActivity(intent);
        }
    }

    public void updateUI() {
        try {
            currentSong = mediaService.getCurrentSong();
            if(currentSong == null) {
                finish();
            }

            txtTitle.setText(currentSong.getTitle());
            txtArtist.setText(currentSong.getArtist());
            txtAlbum.setText(currentSong.getAlbum());
    
            btnSwitchRandom.setImageResource(randomStateIcon(mediaService.getRandomState()));
            btnSwitchRepeat.setImageResource(repeatStateIcon(mediaService.getRepeatState()));
    
            if (mediaService.isPlaying()) {
                btnPlayAndPause.setImageResource(R.drawable.icon_pause);
            } else {
                btnPlayAndPause.setImageResource(R.drawable.icon_play);
            }
            int duration = mediaService.getDuration();
            txtTimeTotal.setText(formatDuration(duration));
            seekBar.setMax(duration);

            // Update the seek bar position
            int position = mediaService.getCurrentPosition();
            txtTimeCurrent.setText(formatDuration(position));
            seekBar.setProgress(position);
        }
        catch (Exception ex) {
            //Toast.makeText(this.getApplicationContext(), "Application unable to update the UI. "+ex.getMessage(), Toast.LENGTH_LONG).show();
            this.finish();
        }
    }

    private void initRunnableSeekBar() {
        myHandler = new Handler();
        myHandler.removeCallbacks(moveSeekBarThread);
        myHandler.postDelayed(moveSeekBarThread, 100);
    }

    private int repeatStateIcon(RepeatState state) {
        if (state == RepeatState.Off) {
            return R.drawable.icon_repeat_off;
        } else if (state == RepeatState.All) {
            return R.drawable.icon_repeat_all;
        } else {
            return R.drawable.icon_repeat_one;
        }
    }

    private int randomStateIcon(RandomState state) {
        if (state == RandomState.Off) {
            return R.drawable.icon_random_off;
        } else if (state == RandomState.Shuffled) {
            return R.drawable.icon_random_shuffled;
        } else {
            return R.drawable.icon_random_random;
        }
    }

    private String repeatStateToText(RepeatState state) {
        if (state == RepeatState.Off) {
            return "Repeat mode is Off";
        } else if (state == RepeatState.All) {
            return "Repeat mode is All";
        } else {
            return "Repeat mode is One";
        }
    }

    private String randomStateToText(RandomState state) {
        if (state == RandomState.Off) {
            return "Random mode is Off";
        } else if (state == RandomState.Shuffled) {
            return "Random mode is Shuffled";
        } else {
            return "Random mode is TrueRandom";
        }
    }

    private Runnable moveSeekBarThread = new Runnable() {
        public void run() {
            try {
                int position = mediaService.getCurrentPosition();
                txtTimeCurrent.setText(formatDuration(position));
                seekBar.setProgress(position);
                myHandler.postDelayed(this, 100);
            } catch (Exception ex) { }
        }
    };

    /**
     * Returns a string with the current duration on the mm:ss format (minutes:seconds)
     * 
     * @param duration in miliseconds
     * @return
     */
    private String formatDuration(int duration) {
        int seconds = (duration / 1000);
        int minutes = duration / (1000*60);
        int remainder = seconds - minutes*60;

        return String.format(Locale.getDefault(), "%02d:%02d", minutes, remainder);
    }
}