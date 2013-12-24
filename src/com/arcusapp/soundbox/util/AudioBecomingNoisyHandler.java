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

package com.arcusapp.soundbox.util;

import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.telephony.TelephonyManager;

import com.arcusapp.soundbox.SoundBoxApplication;
import com.arcusapp.soundbox.model.MediaPlayerServiceListener;
import com.arcusapp.soundbox.player.MediaPlayerService;

public class AudioBecomingNoisyHandler extends android.content.BroadcastReceiver implements MediaPlayerServiceListener {
    
    private Context context;

    @Override
    public void onReceive(Context ctx, Intent intent) {
       context = ctx;
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
        Intent intent = new Intent();
        intent.setAction(SoundBoxApplication.ACTION_MEDIA_PLAYER_SERVICE);
        intent.putExtra(MediaPlayerService.INCOMMING_CALL, true);
        context.startService(intent);
    }

    @Override
    public void onMediaPlayerStateChanged() { }

    @Override
    public void onExceptionRaised(Exception ex) { }

}
