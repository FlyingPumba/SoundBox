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

package com.arcusapp.soundbox.data;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import com.arcusapp.soundbox.SoundBoxApplication;
import com.arcusapp.soundbox.model.BundleExtra;
import com.arcusapp.soundbox.model.MediaEntry;
import com.arcusapp.soundbox.model.MediaType;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class SoundBoxPreferences {

    private final static String LAST_MEDIA = "lastmedia";
    private final static String LAST_PLAYED_SONG = "lastplayedsong";

    public static class LastMedia {
        public static List<MediaEntry> getLastMedia() {
            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(SoundBoxApplication.getContext());

            List<MediaEntry> media = new ArrayList<MediaEntry>();
            try {
                JSONArray jsonArray = new JSONArray(preferences.getString(LAST_MEDIA, null));
                for (int i = 0; i <= jsonArray.length(); i++) {
                    String serializedJson = jsonArray.getString(i);
                    JSONObject jsonObject = new JSONObject(serializedJson);
                    media.add(new MediaEntry(jsonObject));
                }

            } catch (Exception e) {
                if(e.getMessage() != null && !e.getMessage().equals("")){
                    Log.d(SoundBoxPreferences.class.getName(), e.getMessage());
                }
            }

            return media;
        }

        public static void setLastMedia(List<MediaEntry> media) {
            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(SoundBoxApplication.getContext());
            SharedPreferences.Editor editor = preferences.edit();

            ArrayList<String> serializedList = new ArrayList<String>();
            for(MediaEntry entry : media){
                serializedList.add(entry.toJSON());
            }

            JSONArray jsonArray = new JSONArray(serializedList);
            editor.putString(LAST_MEDIA, jsonArray.toString());
            editor.commit();
        }
    }
    
    public static class LastPlayedSong {
        public static MediaEntry getLastPlayedSong() {
            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(SoundBoxApplication.getContext());

            MediaEntry media = new MediaEntry(BundleExtra.DefaultValues.DEFAULT_ID, MediaType.Song, "");
            try {
                JSONArray jsonArray = new JSONArray(preferences.getString(LAST_PLAYED_SONG, null));
                media = (MediaEntry) jsonArray.get(0);
            } catch (Exception ignored) { }

            return media;
        }
    
        public static void setLastPlayedSong(MediaEntry song) {
            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(SoundBoxApplication.getContext());
            SharedPreferences.Editor editor = preferences.edit();

            ArrayList<MediaEntry> aux = new ArrayList<MediaEntry>();
            aux.add(song);

            String serializedString = new JSONArray(aux).toString();
            editor.putString(LAST_PLAYED_SONG, serializedString);
            editor.commit();
        }
    }
}