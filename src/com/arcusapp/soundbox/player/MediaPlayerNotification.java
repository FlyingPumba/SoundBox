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

package com.arcusapp.soundbox.player;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;

import com.arcusapp.soundbox.R;
import com.arcusapp.soundbox.SoundBoxApplication;
import com.arcusapp.soundbox.activity.PlayActivity;

public class MediaPlayerNotification {
    NotificationCompat.Builder mNotificationBuilder;
    private static final int notifyID = 1;

    public MediaPlayerNotification() {
        mNotificationBuilder = new NotificationCompat.Builder(SoundBoxApplication.getContext());
        mNotificationBuilder.setSmallIcon(R.drawable.icon_soundbox);

        //open the PlayActivity when the user clicks the notification
        Intent notificationIntent = new Intent(SoundBoxApplication.getContext(), PlayActivity.class);
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent clickPendingIntent = PendingIntent.getActivity(SoundBoxApplication.getContext(), 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        mNotificationBuilder.setContentIntent(clickPendingIntent);

        //Pause the music when the user swipes out the notification
        //PendingIntent deletePendingIntent = PendingIntent.getActivity(SoundBoxApplication.getContext(), 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        //mNotificationBuilder.setDeleteIntent(deletePendingIntent);

        mNotificationBuilder.setContentTitle("SoundBox is awesome.");
        mNotificationBuilder.setContentText("You are awesome too !");
    }

    public Notification getNotification() {
        return mNotificationBuilder.build();
    }
}