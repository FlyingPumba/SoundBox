package com.arcusapp.soundbox.activity;

import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;

import com.arcusapp.soundbox.SoundBoxApplication;
import com.arcusapp.soundbox.model.MediaPlayerServiceListener;
import com.arcusapp.soundbox.player.MediaPlayerService;

public abstract class MediaServiceAwareActivity extends BaseActivity
        implements MediaPlayerServiceListener, ServiceConnection {

    private MediaPlayerService mMediaService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        startMediaPlayerService();
    }

    private void startMediaPlayerService() {
        Intent serviceIntent = new Intent(SoundBoxApplication.ACTION_MEDIA_PLAYER_SERVICE, null, SoundBoxApplication.getContext(), MediaPlayerService.class);
        startService(serviceIntent);
    }

    private void bindMediaPlayerService() {
        Intent serviceIntent = new Intent(SoundBoxApplication.ACTION_MEDIA_PLAYER_SERVICE, null, SoundBoxApplication.getContext(), MediaPlayerService.class);
        bindService(serviceIntent, this, Context.BIND_AUTO_CREATE);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mMediaService == null) {
            bindMediaPlayerService();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mMediaService != null) {
            mMediaService.unRegisterListener(this);
            unbindService(this);
            mMediaService = null;
        }
    }
}