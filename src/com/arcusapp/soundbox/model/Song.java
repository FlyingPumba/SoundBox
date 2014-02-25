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

package com.arcusapp.soundbox.model;

import android.util.Log;

import java.io.File;

public class Song {
    private String id;
    private String title;
    private String artist;
    private String album;
    private File file;

    public Song(String id, String title, String artist, String album, String filePath) {
        Log.d("MONKEY", "Song id: "+ id);
        this.setID(id);
        this.setName(title);
        this.setArtist(artist);
        this.setAlbum(album);
        this.setFile(filePath);
    }

    public Song() {
        this.setID(BundleExtra.DefaultValues.DEFAULT_ID);
        this.setName("");
        this.setArtist("");
        this.setAlbum("");
        this.setFile("");
    }

    public String getID() {
        return id;
    }

    private void setID(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    private void setName(String name) {
        this.title = name;
    }

    public String getArtist() {
        return artist;
    }

    private void setArtist(String artist) {
        this.artist = artist;
    }

    public String getAlbum() {
        return album;
    }

    private void setAlbum(String album) {
        this.album = album;
    }

    public File getFile() {
        return file;
    }

    private void setFile(String filePath) {
        this.file = new File(filePath);
    }
}
