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

package com.arcusapp.soundbox.player;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.view.View;
import android.widget.RemoteViews;

import com.arcusapp.soundbox.R;
import com.arcusapp.soundbox.SoundBoxApplication;
import com.arcusapp.soundbox.activity.PlayActivity;

public class MediaPlayerNotification {
    private NotificationCompat.Builder mNotificationBuilder;
    private NotificationManager mNotificationManager;
    public static final int MEDIA_PLAYER_NOTIFICATION_ID = 1337;

    private RemoteViews mExpandedView;
    private RemoteViews mBaseView;

    public MediaPlayerNotification() {
        mNotificationManager = (NotificationManager)SoundBoxApplication.getContext().getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationBuilder = new NotificationCompat.Builder(SoundBoxApplication.getContext());
        mNotificationBuilder.setSmallIcon(R.drawable.icon_soundbox);

        //mNotificationBuilder.setContentTitle("SoundBox is awesome.");
        //mNotificationBuilder.setContentText("You are awesome too !");

        mBaseView = new RemoteViews(SoundBoxApplication.getContext().getPackageName(), R.layout.notification_media_player_base);
        mExpandedView =  new RemoteViews(SoundBoxApplication.getContext().getPackageName(), R.layout.notification_media_player_expanded);

        setUpMediaPlayerActions();
    }

    public void updateNotification(String artistName, String albumName,
                                   String songName, boolean isPlaying) {

        updateRemoteViews(artistName, albumName, songName, isPlaying);

        // Compat builder ignores: mNotificationBuilder.setContent(mBaseView);
        // Workaround to possible support library bug:
        // http://stackoverflow.com/questions/12574386/custom-notification-layout-dont-work-on-android-2-3-or-lower
        Notification noti = mNotificationBuilder.build();
        noti.contentView = mBaseView;

        mNotificationManager.notify(MEDIA_PLAYER_NOTIFICATION_ID, noti);
    }

    public Notification getNotification(String artistName, String albumName,
                                        String songName, boolean isPlaying) {

        updateRemoteViews(artistName, albumName, songName, isPlaying);
        //mNotificationBuilder.setContent(mBaseView);
        Notification noti = mNotificationBuilder.build();
        noti.contentView = mBaseView;

        return noti;
    }

    private void updateRemoteViews(String artistName, String albumName,
                                   String songName, boolean isPlaying) {
        mBaseView.setImageViewResource(R.id.notificationBaseIcon, R.drawable.icon_soundbox);

        mBaseView.setTextViewText(R.id.notificationBaseSongName, songName);
        mBaseView.setTextViewText(R.id.notificationBaseArtistName, artistName);

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            mBaseView.setImageViewResource(R.id.notificationBasePlay,
                    isPlaying ? R.drawable.icon_notification_pause : R.drawable.icon_notification_play);
        } else {
            mBaseView.setViewVisibility(R.id.notificationBasePlay, View.GONE);
            mBaseView.setViewVisibility(R.id.notificationBaseNext, View.GONE);
            mBaseView.setViewVisibility(R.id.notificationBaseCollapse, View.GONE);
        }

    }

    private void setUpMediaPlayerActions() {
        //open the PlayActivity when the user clicks the notification
        Intent notificationIntent = new Intent(SoundBoxApplication.getContext(), PlayActivity.class);
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent clickPendingIntent = PendingIntent.getActivity(SoundBoxApplication.getContext(), 1, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        mNotificationBuilder.setContentIntent(clickPendingIntent);

        Intent togglePlayPauseIntent = new Intent(MediaPlayerService.TOGGLEPLAYPAUSE_ACTION, null, SoundBoxApplication.getContext(), MediaPlayerService.class);
        PendingIntent togglePlayPausePendingIntent = PendingIntent.getService(SoundBoxApplication.getContext(), 2, togglePlayPauseIntent, 0);
        mBaseView.setOnClickPendingIntent(R.id.notificationBasePlay, togglePlayPausePendingIntent);

        Intent nextIntent = new Intent(MediaPlayerService.NEXT_ACTION, null, SoundBoxApplication.getContext(), MediaPlayerService.class);
        PendingIntent nextPendingIntent = PendingIntent.getService(SoundBoxApplication.getContext(), 3, nextIntent, 0);
        mBaseView.setOnClickPendingIntent(R.id.notificationBaseNext, nextPendingIntent);

        Intent collapseIntent = new Intent(MediaPlayerService.STOP_ACTION, null, SoundBoxApplication.getContext(), MediaPlayerService.class);
        PendingIntent collapsePendingIntent = PendingIntent.getService(SoundBoxApplication.getContext(), 4, collapseIntent, 0);
        mBaseView.setOnClickPendingIntent(R.id.notificationBaseCollapse, collapsePendingIntent);
    }

}