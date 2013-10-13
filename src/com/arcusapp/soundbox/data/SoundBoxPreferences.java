package com.arcusapp.soundbox.data;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.json.JSONArray;

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
            
            List<String> songs = new ArrayList<String>();
            try {
                JSONArray jsonArray = new JSONArray(preferences.getString(LAST_SONGS, null));
                for (int i = 0; i <= jsonArray.length(); i++) {
                    songs.add(jsonArray.getString(i));
                }

            } catch (Exception e) { }
            
            return songs;
        }
    
        public static void setLastSongs(List<String> songsID) {
            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(SoundBoxApplication.getApplicationContext());
            SharedPreferences.Editor editor = preferences.edit();
    
            String serializedString = new JSONArray(songsID).toString();
            editor.putString(LAST_SONGS, serializedString);
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