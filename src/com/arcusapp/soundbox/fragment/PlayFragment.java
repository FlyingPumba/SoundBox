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
import com.arcusapp.soundbox.data.SoundBoxPreferences;
import com.arcusapp.soundbox.model.MediaPlayerServiceListener;
import com.arcusapp.soundbox.model.Song;
import com.arcusapp.soundbox.player.MediaPlayerService;

import java.util.List;

public class PlayFragment extends Fragment implements MediaPlayerServiceListener {
    private TextView txtSongTitle;
    private ImageButton btnPlayPause;
    private MediaPlayerService mediaService;
    private boolean isCurrentSongNull = false;
    private ServiceConnection myServiceConnection;
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        // Retain this fragment across configuration changes.
        setRetainInstance(true);
        
        initServiceConnection(savedInstanceState);
    }
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_play, container, false);
        rootView.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
        initUI(rootView);
        return rootView;
    }

    @Override
    public void onMediaPlayerStateChanged() {
        updateUI();
    }

    @Override
    public void onExceptionRaised(Exception ex) {
        //Toast.makeText(getActivity(), "EXCEPTION", Toast.LENGTH_LONG).show();
        initServiceConnection(null);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mediaService != null) {
            updateUI();
        }
    }
    
    @Override
    public void onDestroy() {
        if (mediaService != null) {
            mediaService.unRegisterListener(this);
            getActivity().unbindService(myServiceConnection);
        }
        
        super.onDestroy();
    }
    
    private void initServiceConnection(final Bundle savedInstanceState) {
        myServiceConnection = new ServiceConnection() {
            public void onServiceConnected(ComponentName className, IBinder binder) {
                mediaService = ((MediaPlayerService.MyBinder) binder).getService();
                registerToMediaService();
                FetchLastPlayedSongs();
                updateUI();
            }

            public void onServiceDisconnected(ComponentName className) {
                mediaService = null;
            }
        };

        Intent intent = new Intent();
        intent.setAction(SoundBoxApplication.ACTION_MEDIA_PLAYER_SERVICE);
        getActivity().startService(intent);
        getActivity().bindService(intent, myServiceConnection, Context.BIND_AUTO_CREATE);
    }

    private void FetchLastPlayedSongs() {
        List<String> songsID = SoundBoxPreferences.LastSongs.getLastSongs();
        String lastSong = SoundBoxPreferences.LastPlayedSong.getLastPlayedSong();
        // FIXME: use a loadSongs method instead, this is BAD, so BAD.
        mediaService.playSongs(lastSong, songsID);
        mediaService.playAndPause();
    }
    
    private void updateUI() {
        Song currentSong = mediaService.getCurrentSong();

        if (currentSong != null) {
            isCurrentSongNull = false;
            txtSongTitle.setText(currentSong.getTitle());

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

    private void registerToMediaService() {
        mediaService.registerListener(this);
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
}
