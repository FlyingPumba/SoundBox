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

package com.arcusapp.soundbox.fragment;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;

import com.arcusapp.soundbox.R;
import com.arcusapp.soundbox.SoundBoxApplication;
import com.arcusapp.soundbox.model.BundleExtra;
import com.arcusapp.soundbox.model.MediaPlayerServiceListener;
import com.arcusapp.soundbox.model.RandomState;
import com.arcusapp.soundbox.model.RepeatState;
import com.arcusapp.soundbox.model.Song;
import com.arcusapp.soundbox.player.MediaPlayerService;
import com.arcusapp.soundbox.util.FontUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class PlayFragment extends Fragment implements OnClickListener {

    private TextView txtTitle, txtArtistAlbum, txtTimeCurrent, txtTimeTotal;
    private ImageButton btnSwitchRandom, btnSwitchRepeat, btnPlayPause, btnPanel, btnPrevious, btnNext;
    private SeekBar progressBar;

    private Song currentSong;
    private Handler myHandler;

    private MediaPlayerService mediaService;
    private ServiceConnection myServiceConnection;
    private MediaPlayerServiceListener serviceListener;
    private boolean isCurrentSongNull = true;
    private boolean mIsPanelExpanded = false;
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        // Retain this fragment across configuration changes.
        setRetainInstance(true);

        serviceListener = new MediaPlayerServiceListener() {
            @Override
            public void onMediaPlayerStateChanged() {
                updateUI();
            }

            @Override
            public void onExceptionRaised(Exception ex) {
                bindMediaPlayerService();
            }
        };
    }
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_play, container, false);

        FontUtils.setRobotoFont(getActivity().getApplicationContext(), rootView);

        if(Build.VERSION.SDK_INT > Build.VERSION_CODES.ECLAIR_MR1) {
            rootView.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
        } else {
            rootView.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));
        }

        initUI(rootView);
        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mediaService == null) {
            bindMediaPlayerService();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mediaService != null) {
            mediaService.unRegisterListener(serviceListener);
            getActivity().unbindService(myServiceConnection);
            mediaService = null;
        }
    }

    public void setPanelExpanded(boolean expanded) {
        mIsPanelExpanded = expanded;
        ViewGroup.LayoutParams progressBarParams = progressBar.getLayoutParams();

        if(mIsPanelExpanded) {
            btnPanel.setImageResource(R.drawable.ic_list);
            progressBarParams.height = (int)getResources().getDimension(R.dimen.progress_bar_height_expanded);
        } else {
            if (mediaService != null && mediaService.isPlaying()) {
                btnPanel.setImageResource(R.drawable.ic_pause);
            } else {
                btnPanel.setImageResource(R.drawable.ic_play);
            }
            progressBarParams.height = (int)getResources().getDimension(R.dimen.progress_bar_height_collapsed);
        }
        progressBar.setLayoutParams(progressBarParams);
    }

    public boolean isCurrentSongNull() {
        return isCurrentSongNull;
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


        Intent serviceIntent = new Intent(SoundBoxApplication.ACTION_MEDIA_PLAYER_SERVICE, null, SoundBoxApplication.getContext(), MediaPlayerService.class);
        getActivity().bindService(serviceIntent, myServiceConnection, Context.BIND_AUTO_CREATE);
    }

    private void initUI(View rootView) {
        txtTitle = (TextView) rootView.findViewById(R.id.txtSongTitle);
        txtTitle.setTypeface(null, Typeface.BOLD);
        txtTitle.setSelected(true);
        txtArtistAlbum = (TextView) rootView.findViewById(R.id.txtSongArtistAlbum);
        txtTimeCurrent = (TextView) rootView.findViewById(R.id.txtTimeCurrent);
        txtTimeTotal = (TextView) rootView.findViewById(R.id.txtTimeTotal);

        btnPlayPause = (ImageButton) rootView.findViewById(R.id.btnPlayPause);
        btnPlayPause.setOnClickListener(this);
        btnPrevious = (ImageButton) rootView.findViewById(R.id.btnPrevSong);
        btnPrevious.setOnClickListener(this);
        btnNext = (ImageButton) rootView.findViewById(R.id.btnNextSong);
        btnNext.setOnClickListener(this);
        btnSwitchRandom = (ImageButton) rootView.findViewById(R.id.btnSwitchRandom);
        btnSwitchRandom.setOnClickListener(this);
        btnSwitchRepeat = (ImageButton) rootView.findViewById(R.id.btnSwitchRepeat);
        btnSwitchRepeat.setOnClickListener(this);
        btnPanel = (ImageButton) rootView.findViewById(R.id.btnPanel);
        btnPanel.setOnClickListener(this);

        // init the seekbar
        progressBar = (SeekBar) rootView.findViewById(R.id.progressBar);
        progressBar.setThumb(null);
        progressBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                //progressBar.setThumb(null);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                /*Drawable drawable = getResources().getDrawable(android.R.drawable.gallery_thumb);
                final int quarterHeight = drawable.getIntrinsicHeight()/4;
                final int halfWidht = drawable.getIntrinsicWidth()/2;

                drawable.setBounds(new Rect(-halfWidht,
                        -quarterHeight,
                        halfWidht,
                        3*quarterHeight));
                progressBar.setThumb(drawable);*/
            }

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    if(mediaService != null && mIsPanelExpanded) {
                        mediaService.seekTo(progress);
                        seekBar.setProgress(progress);
                    }
                }
            }
        });

        progressBar.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                // only handle user touch when the panel is Expanded
                return !mIsPanelExpanded;
            }
        });
    }

    private void updateUI() {
        try {
            currentSong = mediaService.getCurrentSong();
            if(currentSong == null) {
                isCurrentSongNull = true;
                txtTitle.setText("---");
                btnPlayPause.setImageResource(R.drawable.ic_play);
                btnPanel.setClickable(false);
            } else {
                isCurrentSongNull = false;
                txtTitle.setText(currentSong.getTitle());
                txtArtistAlbum.setText(getResources().getString(R.string.LabelArtistAlbum, currentSong.getAlbum(), currentSong.getArtist()));

                btnSwitchRandom.setImageResource(randomStateIcon(mediaService.getRandomState()));
                btnSwitchRepeat.setImageResource(repeatStateIcon(mediaService.getRepeatState()));

                btnPanel.setClickable(true);
                if (mediaService.isPlaying()) {
                    btnPlayPause.setImageResource(R.drawable.ic_pause);
                    if(!mIsPanelExpanded) {
                        btnPanel.setImageResource(R.drawable.ic_pause);
                    }
                } else {
                    btnPlayPause.setImageResource(R.drawable.ic_play);
                    if(!mIsPanelExpanded) {
                        btnPanel.setImageResource(R.drawable.ic_play);
                    }
                }

                int duration = mediaService.getDuration();
                txtTimeTotal.setText(formatDuration(duration));
                progressBar.setMax(duration);

                // Update the seek bar position
                int position = mediaService.getCurrentPosition();
                txtTimeCurrent.setText(formatDuration(position));
                progressBar.setProgress(position);
            }
        }
        catch (Exception ex) {
            //Toast.makeText(this.getApplicationContext(), "Application unable to update the UI. "+ex.getMessage(), Toast.LENGTH_LONG).show();
            //this.finish();
        }
    }

    @Override
    public void onClick(View v) {
        if(currentSong == null || mediaService == null) {
            return;
        }

        if (v.getId() == R.id.btnPlayPause) {
            mediaService.playAndPause();
        }
        else if (v.getId() == R.id.btnPanel) {
            if(mIsPanelExpanded) {
                Intent intent = new Intent();
                intent.setAction(SoundBoxApplication.ACTION_SONGSLIST_ACTIVITY);

                Bundle mExtras = new Bundle();
                List<String> songsID = mediaService.getSongsIDList();
                mExtras.putStringArrayList(BundleExtra.SONGS_ID_LIST, (ArrayList<String>)songsID);
                String currentSongID = mediaService.getCurrentSong().getID();
                mExtras.putString(BundleExtra.CURRENT_ID, currentSongID);

                intent.putExtras(mExtras);
                startActivity(intent);
            } else {
                mediaService.playAndPause();
            }
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
    }

    private void initRunnableSeekBar() {
        myHandler = new Handler();
        myHandler.removeCallbacks(moveSeekBarThread);
        myHandler.postDelayed(moveSeekBarThread, 100);
    }

    private int repeatStateIcon(RepeatState state) {
        if (state == RepeatState.Off) {
            return R.drawable.ic_repeat_off;
        } else if (state == RepeatState.All) {
            return R.drawable.ic_repeat_all;
        } else {
            return R.drawable.ic_repeat_one;
        }
    }

    private int randomStateIcon(RandomState state) {
        if (state == RandomState.Off) {
            return R.drawable.ic_random_off;
        } else if (state == RandomState.Shuffled) {
            return R.drawable.ic_random_shuffled;
        } else {
            return R.drawable.ic_random_random;
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
                progressBar.setProgress(position);
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
