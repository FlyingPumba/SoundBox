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

package com.arcusapp.soundbox;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Environment;

import com.arcusapp.soundbox.player.MediaPlayerService;
import com.arcusapp.soundbox.util.DirectoryHelper;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Stores general information for the application that can be accessed within any other class
 */
public class SoundBoxApplication extends Application {
    private static Context appContext;

    public static final String ACTION_MAIN_ACTIVITY = "com.arcusapp.soundbox.action.MAIN_ACTIVITY";
    public static final String ACTION_FOLDERS_ACTIVITY = "com.arcusapp.soundbox.action.FOLDERS_ACTIVITY";
    public static final String ACTION_PLAY_ACTIVITY = "com.arcusapp.soundbox.action.PLAY_ACTIVITY";
    public static final String ACTION_SONGSLIST_ACTIVITY = "com.arcusapp.soundbox.action.SONGSLIST_ACTIVITY";
    public static final String ACTION_MEDIA_PLAYER_SERVICE = "com.arcusapp.soundbox.action.MEDIA_PLAYER_SERVICE";

    private static List<File> sdCards;

    private static int mForegroundActivities = 0;

    @Override
    public void onCreate() {
        super.onCreate();

        this.appContext = getApplicationContext();
        searchForSDCards();
    }

    public static Context getContext() {
        return appContext;
    }

    public static void notifyForegroundStateChanged(boolean inForeground) {
        int old = mForegroundActivities;
        if (inForeground) {
            mForegroundActivities++;
        } else {
            mForegroundActivities--;
        }

        if (old == 0 || mForegroundActivities == 0) {
            Intent intent = new Intent();
            intent.setAction(MediaPlayerService.CHANGE_FOREGROUND_STATE);
            intent.putExtra(MediaPlayerService.NOW_IN_FOREGROUND, mForegroundActivities == 0);
            appContext.startService(intent);
        }

    }

    public static List<File> getSDCards() {
        return sdCards;
    }

    public static List<File> getDefaultUserDirectoriesOptions() {
        List<File> defaultUserOptions = new ArrayList<File>();

        if(Build.VERSION.SDK_INT > Build.VERSION_CODES.ECLAIR_MR1) {
            File musicDirectory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MUSIC);
            defaultUserOptions.add(musicDirectory);
        }
        defaultUserOptions.addAll(sdCards);
        return defaultUserOptions;
    }

    private void searchForSDCards() {
        sdCards = new ArrayList<File>();
        File primarysdCard = Environment.getExternalStorageDirectory();
        String[] sdCardDirectories = DirectoryHelper.getStorageDirectories();
        String[] othersdCardDirectories = DirectoryHelper.getOtherStorageDirectories();

        sdCards.add(primarysdCard);
        for (String s : sdCardDirectories) {
            File directory = new File(s);
            if (!sdCards.contains(directory)) {
                sdCards.add(directory);
            }
        }
        for (String s : othersdCardDirectories) {
            File directory = new File(s);
            if (!sdCards.contains(directory)) {
                sdCards.add(directory);
            }
        }
    }
}