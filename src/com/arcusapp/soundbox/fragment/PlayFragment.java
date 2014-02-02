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

package com.arcusapp.soundbox.fragment;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageButton;
import android.widget.TextView;

import com.arcusapp.soundbox.R;
import com.arcusapp.soundbox.SoundBoxApplication;
import com.arcusapp.soundbox.model.MediaPlayerServiceListener;
import com.arcusapp.soundbox.model.Song;
import com.arcusapp.soundbox.player.MediaPlayerService;

import java.util.List;

public class PlayFragment extends Fragment {

    private TextView txtSongTitle;
    private ImageButton btnPlayPause;

    private MediaPlayerService mediaService;
    private ServiceConnection myServiceConnection;
    private MediaPlayerServiceListener serviceListener;
    private boolean isCurrentSongNull = false;
    
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
    
    private void bindMediaPlayerService() {
        myServiceConnection = new ServiceConnection() {
            public void onServiceConnected(ComponentName className, IBinder binder) {
                mediaService = ((MediaPlayerService.MyBinder) binder).getService();
                mediaService.registerListener(serviceListener);
                updateUI();
            }

            public void onServiceDisconnected(ComponentName className) {
                mediaService = null;
            }
        };

        Intent intent = new Intent();
        intent.setAction(SoundBoxApplication.ACTION_MEDIA_PLAYER_SERVICE);
        getActivity().bindService(intent, myServiceConnection, Context.BIND_AUTO_CREATE);
    }

    private void initUI(View rootView) {
        rootView.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                if (!isCurrentSongNull) {
                    Intent playActivityIntent = new Intent();
                    playActivityIntent.setAction(SoundBoxApplication.ACTION_PLAY_ACTIVITY);
                    getActivity().startActivity(playActivityIntent);
                }
            }
        });

        txtSongTitle = (TextView) rootView.findViewById(R.id.fargmentPlay_txtSongTitle);
        txtSongTitle.setSelected(true);
        txtSongTitle.setTypeface(null, Typeface.BOLD);
        txtSongTitle.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                if (!isCurrentSongNull) {
                    Intent playActivityIntent = new Intent();
                    playActivityIntent.setAction(SoundBoxApplication.ACTION_PLAY_ACTIVITY);
                    getActivity().startActivity(playActivityIntent);
                }
            }
        });
        btnPlayPause = (ImageButton) rootView.findViewById(R.id.fragmentPlay_btnPlayPause);
        btnPlayPause.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                mediaService.playAndPause();
            }
        });
    }

    private void updateUI() {
        Song currentSong = mediaService.getCurrentSong();

        if (currentSong != null) {
            isCurrentSongNull = false;

            // Check if the song changed before setting the new text
            // This way we don't interrupt the marquee.
            String newTitle = currentSong.getTitle();
            String oldTitle = txtSongTitle.getText().toString();
            if(!oldTitle.equals(newTitle)) {
                txtSongTitle.setText(currentSong.getTitle());
            }

            if (mediaService.isPlaying()) {
                btnPlayPause.setImageResource(R.drawable.icon_pause);
            } else {
                btnPlayPause.setImageResource(R.drawable.icon_play);
            }
            btnPlayPause.setClickable(true);
        } else {
            isCurrentSongNull = true;
            txtSongTitle.setText("---");
            btnPlayPause.setImageResource(R.drawable.icon_play);
            btnPlayPause.setClickable(false);
        }
    }
}
