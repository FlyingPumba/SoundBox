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
import android.widget.Button;
import android.widget.TextView;

import com.arcusapp.soundbox.R;
import com.arcusapp.soundbox.SoundBoxApplication;
import com.arcusapp.soundbox.model.MediaPlayerServiceListener;
import com.arcusapp.soundbox.model.Song;
import com.arcusapp.soundbox.player.MediaPlayerService;

public class PlayFragment extends Fragment implements MediaPlayerServiceListener {
    TextView txtSongTitle;
    Button btnPlayPause;
    MediaPlayerService mediaService;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_play, container, false);
        rootView.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
        initUI(rootView);
        initServiceConnection(savedInstanceState);
        return rootView;
    }

    @Override
    public void onSongCompletion() {
        updateUI();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mediaService != null) {
            updateUI();
        }
    }

    private void initServiceConnection(Bundle savedInstanceState) {
        ServiceConnection myServiceConnection = new ServiceConnection() {
            public void onServiceConnected(ComponentName className, IBinder binder) {
                mediaService = ((MediaPlayerService.MyBinder) binder).getService();
                registerToMediaService();
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

    private void updateUI() {
        Song currentSong = mediaService.getCurrentSong();

        if (currentSong != null) {
            txtSongTitle.setText(currentSong.getTitle());

            if (mediaService.isPlaying()) {
                btnPlayPause.setBackgroundResource(R.drawable.icon_pause);
            } else {
                btnPlayPause.setBackgroundResource(R.drawable.icon_play);
            }
            btnPlayPause.setClickable(true);
        } else {
            txtSongTitle.setText("---");
            btnPlayPause.setBackgroundResource(R.drawable.icon_play);
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
                Intent playActivityIntent = new Intent();
                playActivityIntent.setAction(SoundBoxApplication.ACTION_PLAY_ACTIVITY);
                getActivity().startActivity(playActivityIntent);
            }
        });

        txtSongTitle = (TextView) rootView.findViewById(R.id.fargmentPlay_txtSongTitle);
        txtSongTitle.setTypeface(null, Typeface.BOLD);
        txtSongTitle.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent playActivityIntent = new Intent();
                playActivityIntent.setAction(SoundBoxApplication.ACTION_PLAY_ACTIVITY);
                getActivity().startActivity(playActivityIntent);
            }
        });
        btnPlayPause = (Button) rootView.findViewById(R.id.fragmentPlay_btnPlayPause);
        btnPlayPause.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                mediaService.playAndPause();
                if (mediaService.isPlaying()) {
                    btnPlayPause.setBackgroundResource(R.drawable.icon_pause);
                } else {
                    btnPlayPause.setBackgroundResource(R.drawable.icon_play);
                }
            }
        });
    }
}
