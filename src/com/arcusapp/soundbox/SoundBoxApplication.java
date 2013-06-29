package com.arcusapp.soundbox;

import android.content.Context;

/**
 * Stores general information for the application that can be accessed within any other class
 */
public abstract class SoundBoxApplication {
	private static Context appContext;

	public static void setInitialContext(Context context) {
		appContext = context;
	}

	public static Context getApplicationContext() {
		return appContext;
	}
}
