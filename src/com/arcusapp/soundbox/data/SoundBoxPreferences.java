package com.arcusapp.soundbox.data;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.arcusapp.soundbox.SoundBoxApplication;
import com.arcusapp.soundbox.model.BundleExtra;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class SoundBoxPreferences {

    private final static String LAST_SONGS = "lastsongs";
    private final static String LAST_PLAYED_SONG = "lastplayedsong";

    public static class LastSongs {
        public static List<String> getLastSongs() {    
            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(SoundBoxApplication.getApplicationContext());
            // Retrieve the values
            Set<String> set = new HashSet<String>();
            set = preferences.getStringSet(LAST_SONGS, null);
    
            return new ArrayList<String>(set);
        }
    
        public static void setLastSongs(List<String> songsID) {
            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(SoundBoxApplication.getApplicationContext());
            SharedPreferences.Editor editor = preferences.edit();
    
            // Set the values
            Set<String> set = new HashSet<String>();
            set.addAll(songsID);
    
            editor.putStringSet(LAST_SONGS, set);
            editor.commit();
    
        }
    }
    
    public static class LastPlayedSong {
        public static String getLastPlayedSong() {
            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(SoundBoxApplication.getApplicationContext());
            String song = preferences.getString(LAST_PLAYED_SONG, BundleExtra.DefaultValues.DEFAULT_ID);
    
            return song;
        }
    
        public static void setLastPlayedSong(String songID) {
            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(SoundBoxApplication.getApplicationContext());
            SharedPreferences.Editor editor = preferences.edit();
            
            editor.putString(LAST_PLAYED_SONG, songID);
            editor.commit();    
        }
    }
}