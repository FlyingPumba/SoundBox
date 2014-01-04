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

package com.arcusapp.soundbox;

import android.app.Application;
import android.content.Context;

/**
 * Stores general information for the application that can be accessed within any other class
 */
public class SoundBoxApplication extends Application {
    private static Context appContext;

    public static final String ACTION_MAIN_ACTIVITY = "com.arcusapp.soundbox.action.MAIN_ACTIVITY";
    public static final String ACTION_FOLDERS_ACTIVITY = "com.arcusapp.soundbox.action.FOLDERS_ACTIVITY";
    public static final String ACTION_ARTISTS_ACTIVITY = "com.arcusapp.soundbox.action.ARTISTS_ACTIVITY";
    public static final String ACTION_PLAY_ACTIVITY = "com.arcusapp.soundbox.action.PLAY_ACTIVITY";
    public static final String ACTION_SONGSLIST_ACTIVITY = "com.arcusapp.soundbox.action.SONGSLIST_ACTIVITY";
    public static final String ACTION_PLAYLISTS_ACTIVITY = "com.arcusapp.soundbox.action.PLAYLISTS_ACTIVITY";
    public static final String ACTION_MEDIA_PLAYER_SERVICE = "com.arcusapp.soundbox.action.MEDIA_PLAYER_SERVICE";

    public static final int PICK_SONG_REQUEST = 1;

    @Override
    public void onCreate() {
        super.onCreate();

        this.appContext = getApplicationContext();
    }

    public static Context getContext() {
        return appContext;
    }
}