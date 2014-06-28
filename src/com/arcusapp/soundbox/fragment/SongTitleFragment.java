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
import com.arcusapp.soundbox.util.FontUtils;

public class SongTitleFragment extends Fragment implements OnClickListener {

    private TextView txtTitle, txtArtistAlbum;
    private ImageButton btnPanel;

    private Song currentSong;

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
        View rootView = inflater.inflate(R.layout.fragment_song_title, container, false);

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

        if(mIsPanelExpanded) {
            btnPanel.setVisibility(View.INVISIBLE);
        } else {
            btnPanel.setVisibility(View.VISIBLE);
            if (mediaService != null && mediaService.isPlaying()) {
                btnPanel.setImageResource(R.drawable.ic_pause);
            } else {
                btnPanel.setImageResource(R.drawable.ic_play);
            }
        }
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

        btnPanel = (ImageButton) rootView.findViewById(R.id.btnPanel);
        btnPanel.setOnClickListener(this);
    }

    private void updateUI() {
        try {
            currentSong = mediaService.getCurrentSong();
            if(currentSong == null) {
                isCurrentSongNull = true;
                txtTitle.setText("---");
                btnPanel.setClickable(false);
            } else {
                isCurrentSongNull = false;
                txtTitle.setText(currentSong.getTitle());
                txtArtistAlbum.setText(getResources().getString(R.string.LabelArtistAlbum, currentSong.getAlbum(), currentSong.getArtist()));

                btnPanel.setClickable(true);
                if (mediaService.isPlaying()) {
                    if(!mIsPanelExpanded) {
                        btnPanel.setImageResource(R.drawable.ic_pause);
                    }
                } else {
                    if(!mIsPanelExpanded) {
                        btnPanel.setImageResource(R.drawable.ic_play);
                    }
                }
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

        if (v.getId() == R.id.btnPanel) {
            if(!mIsPanelExpanded) {
                mediaService.playAndPause();
            }
        }
    }
}
