package com.arcusapp.soundbox;

import android.content.Context;

/**
 * Stores general information for the application that can be accessed within any other class
 */
public abstract class SoundBoxApplication {
	private static Context appContext;

	public static final String ACTION_MAIN_ACTIVITY = "com.arcusapp.soundbox.action.MAIN_ACTIVITY";
	public static final String ACTION_FOLDERS_ACTIVITY = "com.arcusapp.soundbox.action.FOLDERS_ACTIVITY";
	public static final String ACTION_ARTISTS_ACTIVITY = "com.arcusapp.soundbox.action.ARTISTS_ACTIVITY";
	public static final String ACTION_PLAY_ACTIVITY = "com.arcusapp.soundbox.action.PLAY_ACTIVITY";
	public static final String ACTION_SONGLIST_ACTIVITY = "com.arcusapp.soundbox.action.SONGLIST_ACTIVITY";
	public static final String ACTION_PLAYLISTS_ACTIVITY = "com.arcusapp.soundbox.action.PLAYLISTS_ACTIVITY";
	public static final String ACTION_MEDIA_PLAYER_SERVICE = "com.arcusapp.soundbox.action.MEDIA_PLAYER_SERVICE";

	public static void setInitialContext(Context context) {
		appContext = context;
	}

	public static Context getApplicationContext() {
		return appContext;
	}
}
