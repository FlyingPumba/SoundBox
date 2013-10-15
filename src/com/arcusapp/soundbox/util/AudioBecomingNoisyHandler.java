package com.arcusapp.soundbox.util;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.telephony.TelephonyManager;

import com.arcusapp.soundbox.SoundBoxApplication;
import com.arcusapp.soundbox.model.MediaPlayerServiceListener;
import com.arcusapp.soundbox.player.MediaPlayerService;

public class AudioBecomingNoisyHandler extends android.content.BroadcastReceiver implements MediaPlayerServiceListener {
    
    private MediaPlayerService mediaService;
    private ServiceConnection myServiceConnection;
    
    @Override
    public void onReceive(Context ctx, Intent intent) {
       String state = intent.getStringExtra(TelephonyManager.EXTRA_STATE);
       
       if (state.equals(TelephonyManager.EXTRA_STATE_RINGING)) {
           // Phone is ringing
           initServiceconnectionAndPause();
       } else if (state.equals(TelephonyManager.EXTRA_STATE_OFFHOOK)) {
           // Call received
       } else if (state.equals(TelephonyManager.EXTRA_STATE_IDLE)) {
           // Call Dropped or rejected
       }
    }

    private void pausePlayer() {
        if(mediaService.isPlaying()) {
            mediaService.playAndPause();
        }
    }

    private void initServiceconnectionAndPause() {
        myServiceConnection = new ServiceConnection() {
            public void onServiceConnected(ComponentName className, IBinder binder) {
                mediaService = ((MediaPlayerService.MyBinder) binder).getService();
                registerToMediaService();
                pausePlayer();
            }

            public void onServiceDisconnected(ComponentName className) {
                mediaService = null;
            }
        };

        Intent intent = new Intent();
        intent.setAction(SoundBoxApplication.ACTION_MEDIA_PLAYER_SERVICE);
        
        SoundBoxApplication.getApplicationContext().startService(intent);
        SoundBoxApplication.getApplicationContext().bindService(intent, myServiceConnection, Context.BIND_AUTO_CREATE);
    }
    
    private void registerToMediaService() {
        mediaService.registerListener(this);
    }

    @Override
    public void onMediaPlayerStateChanged() { }

    @Override
    public void onExceptionRaised(Exception ex) { }

}
