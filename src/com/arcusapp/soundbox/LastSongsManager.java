package com.arcusapp.soundbox;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class LastSongsManager {

	private static String LastSongsKey = "lastsongs";

	public static List<String> getLastSongs(Context _context) {

		SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(_context);
		// Retrieve the values
		Set<String> set = new HashSet<String>();
		set = preferences.getStringSet(LastSongsKey, null);

		return new ArrayList<String>(set);
	}

	public static void setLastSongs(Context _context, List<String> songsID) {
		SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(_context);
		SharedPreferences.Editor editor = preferences.edit();

		// Set the values
		Set<String> set = new HashSet<String>();
		set.addAll(songsID);

		editor.putStringSet(LastSongsKey, set);
		editor.commit();

	}

	public static void removeLastStongs(Context _context) {

		SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(_context);
		SharedPreferences.Editor editor = preferences.edit();
		editor.remove(LastSongsKey);
		editor.commit();
	}
}
