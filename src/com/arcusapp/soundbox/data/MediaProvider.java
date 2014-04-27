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

import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v4.content.CursorLoader;

import com.arcusapp.soundbox.SoundBoxApplication;
import com.arcusapp.soundbox.model.MediaEntry;
import com.arcusapp.soundbox.model.MediaType;
import com.arcusapp.soundbox.model.Song;

import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class MediaProvider {

    private OnlyDirsFilter myFilter;
    private File defaultDirectory;
    private Uri defaultDirectoryUri;

    private Cursor myCursor;

    public MediaProvider() {
        myFilter = new OnlyDirsFilter();

        defaultDirectory = SoundBoxApplication.getSDCards().get(0);
        defaultDirectoryUri = MediaStore.Audio.Media.getContentUriForPath(defaultDirectory.getPath());
    }

    /**
     * Returns the id of all the Songs in the MediaStore.
     *
     * @return a list of MediaEntries. The value associated is the name of the Song.
     */
    public List<MediaEntry> getAllSongs() {
        List<MediaEntry> songs = new ArrayList<MediaEntry>();

        String[] cursorProjection = new String[] { MediaStore.Audio.Media._ID, MediaStore.Audio.Media.TITLE };
        String selection = MediaStore.Audio.Media.IS_MUSIC + " != 0 ";
        String sortOrder = MediaStore.Audio.Media.TITLE;

        CursorLoader cl = new CursorLoader(SoundBoxApplication.getContext(), defaultDirectoryUri, cursorProjection, selection, null, sortOrder);
        myCursor = cl.loadInBackground();

        while (myCursor.moveToNext()) {
            songs.add(new MediaEntry(myCursor.getString(0), MediaType.Song, myCursor.getString(1)));
        }

        myCursor.close();
        return songs;
    }

    /**
     * Returns the name of all the Artists in the MediaStore.
     *
     * @return a list of MediaEntries. The value associated is the name of the Artist.
     */
    public List<MediaEntry> getAllArtists() {
        List<MediaEntry> artists = new ArrayList<MediaEntry>();

        Uri uri = MediaStore.Audio.Artists.EXTERNAL_CONTENT_URI;
        String[] projection = { MediaStore.Audio.Artists._ID, MediaStore.Audio.Artists.ARTIST, MediaStore.Audio.Artists.NUMBER_OF_ALBUMS };
        String selection = MediaStore.Audio.Artists.ARTIST + " not null";
        String sortOrder = MediaStore.Audio.Artists.ARTIST;

        CursorLoader cl = new CursorLoader(SoundBoxApplication.getContext(), uri, projection, selection, null, sortOrder);
        myCursor = cl.loadInBackground();

        while (myCursor.moveToNext()) {
            String detail = myCursor.getString(2) + " albums";
            artists.add(new MediaEntry(myCursor.getString(0), MediaType.Artist, myCursor.getString(1), detail));
        }

        myCursor.close();
        return artists;
    }

    /**
     * Returns the name of all the Albums of the specified Artist in the MediaStore.
     * 
     * @param artistName the ID of the Artist
     * @return a list of MediaEntries. The value associated is the name of the Album.
     */
    public List<MediaEntry> getAlbumsFromArtist(String artistName) {
        List<MediaEntry> albums = new ArrayList<MediaEntry>();

        Uri uri = MediaStore.Audio.Albums.EXTERNAL_CONTENT_URI;
        String[] projection = { MediaStore.Audio.Albums._ID, MediaStore.Audio.Albums.ALBUM, MediaStore.Audio.Albums.NUMBER_OF_SONGS };
        String selection = MediaStore.Audio.Albums.ARTIST + " = ?";
        String sortOrder = MediaStore.Audio.Artists.Albums.ALBUM;
        String[] selectionArgs = new String[] { artistName };

        CursorLoader cl = new CursorLoader(SoundBoxApplication.getContext(), uri, projection, selection, selectionArgs, sortOrder);
        myCursor = cl.loadInBackground();

        while (myCursor.moveToNext()) {
            String detail = myCursor.getString(2) + " songs";
            albums.add(new MediaEntry(myCursor.getString(0), MediaType.Album, myCursor.getString(1), detail));
        }

        myCursor.close();
        return albums;
    }

    /**
     * Returns the id of all the Songs of the specified Artist in the MediaStore.
     * 
     * @param artistName the name of the Artist
     * @return a list of MediaEntries. The value associated is the name of the Song.
     */
    public List<MediaEntry> getSongsFromArtist(String artistName) {
        List<MediaEntry> songs = new ArrayList<MediaEntry>();

        String[] projection = { MediaStore.Audio.Media._ID, MediaStore.Audio.Media.TITLE };
        String selection = MediaStore.Audio.Media.IS_MUSIC + " != 0 AND " + MediaStore.Audio.Artists.ARTIST + " = ?";
        String sortOrder = MediaStore.Audio.Media.TITLE;
        String[] selectionArgs = new String[] { artistName };

        CursorLoader cl = new CursorLoader(SoundBoxApplication.getContext(), defaultDirectoryUri, projection, selection, selectionArgs, sortOrder);
        myCursor = cl.loadInBackground();

        while (myCursor.moveToNext()) {
            songs.add(new MediaEntry(myCursor.getString(0), MediaType.Song, myCursor.getString(1)));
        }

        myCursor.close();
        return songs;
    }

    /**
     * Returns the id of all the Songs of the specified Album in the MediaStore.
     * 
     * @param albumName the ID of the Album
     * @return a list of MediaEntries. The value associated is the name of the Song.
     */
    public List<MediaEntry> getSongsFromAlbum(String albumName) {
        List<MediaEntry> songs = new ArrayList<MediaEntry>();

        String[] projection = { MediaStore.Audio.Media._ID, MediaStore.Audio.Media.TITLE };
        String selection = MediaStore.Audio.Media.IS_MUSIC + " != 0 AND " + MediaStore.Audio.Albums.ALBUM + " = ?";
        String sortOrder = MediaStore.Audio.Media.TITLE;
        String[] selectionArgs = new String[] { albumName };

        CursorLoader cl = new CursorLoader(SoundBoxApplication.getContext(), defaultDirectoryUri, projection, selection, selectionArgs, sortOrder);
        myCursor = cl.loadInBackground();

        while (myCursor.moveToNext()) {
            songs.add(new MediaEntry(myCursor.getString(0), MediaType.Song, myCursor.getString(1)));
        }

        myCursor.close();
        return songs;
    }

    /**
     * Returns a list of PlaylistEntries for all the Playlists in the MediaStore.
     * 
     * @return a list of MediaEntries. The value associated is the name of the Playlist.
     */
    public List<MediaEntry> getAllPlayLists() {
        List<MediaEntry> playLists = new ArrayList<MediaEntry>();

        String[] projection = new String[] { MediaStore.Audio.Playlists._ID, MediaStore.Audio.Playlists.NAME };

        CursorLoader cl = new CursorLoader(SoundBoxApplication.getContext(), MediaStore.Audio.Playlists.EXTERNAL_CONTENT_URI, projection, null, null, null);
        myCursor = cl.loadInBackground();

        while (myCursor.moveToNext()) {
            playLists.add(new MediaEntry(myCursor.getString(0), MediaType.Playlist, myCursor.getString(1)));
        }

        Collections.sort(playLists);

        myCursor.close();
        return playLists;
    }

    /**
     * Returns the id  of all the Songs of the specified Playlist in the MediaStore.
     * 
     * @param playListID the ID of the Playlist
     * @return a list of MediaEntries. The value associated is the name of the Song.
     */
    public List<MediaEntry> getSongsFromPlaylist(String playListID) {
        List<MediaEntry> songs = new ArrayList<MediaEntry>();

        Uri specialUri = MediaStore.Audio.Playlists.Members.getContentUri("external", Integer.parseInt(playListID));
        String[] projection = { MediaStore.Audio.Playlists.Members.AUDIO_ID, MediaStore.Audio.Playlists.Members.TITLE };
        String sortOrder = MediaStore.Audio.Playlists.Members.TITLE;

        CursorLoader cl = new CursorLoader(SoundBoxApplication.getContext(), specialUri, projection, null, null, sortOrder);
        myCursor = cl.loadInBackground();

        while (myCursor.moveToNext()) {
            songs.add(new MediaEntry(myCursor.getString(0), MediaType.Song, myCursor.getString(1)));
        }

        myCursor.close();
        return songs;
    }

    public Song getSongFromID(String songID) {
        Song song;
        List<String> values = new ArrayList<String>();

        String[] projection = { MediaStore.Audio.Media._ID, MediaStore.Audio.Media.TITLE,
                MediaStore.Audio.Media.ARTIST, MediaStore.Audio.Media.ALBUM, MediaStore.Audio.Media.DATA };

        String selection = MediaStore.Audio.Media.IS_MUSIC + " != 0 AND " + MediaStore.Audio.Media._ID + " = ?";
        String[] selectionArgs = new String[] { songID };

        CursorLoader cl = new CursorLoader(SoundBoxApplication.getContext(), defaultDirectoryUri, projection, selection, selectionArgs, null);
        myCursor = cl.loadInBackground();

        try {
            myCursor.moveToNext();
            for (int i = 0; i < myCursor.getColumnCount(); i++)
                values.add(myCursor.getString(i));
            if (values.size() > 0) {
                song = new Song(values.get(0), values.get(1), values.get(2), values.get(3), values.get(4));
            } else {
                song = new Song();
            }
        } catch (Exception ignored) {
            song = new Song();
        }

        myCursor.close();
        return song;
    }

    /**
     * Returns a list of SongEntries for the specified Songs.
     * 
     * @param songsID list of ids
     * @param projection one key from {@linkplain MediaStore.Audio.Media} to associate on the SongEntry's value
     * @return a list of SongEntries
     */
    public List<MediaEntry> getValueFromSongs(List<String> songsID, String projection) {
        List<MediaEntry> songs = new ArrayList<MediaEntry>();

        String[] ids = new String[songsID.size()];
        ids = songsID.toArray(ids);

        String[] cursorProjection = new String[] { MediaStore.Audio.Media._ID, projection, MediaStore.Audio.Media.DATA };

        String selection = MediaStore.Audio.Media.IS_MUSIC + " != 0 AND " + MediaStore.Audio.Media._ID + " IN (";
        for (int i = 0; i < songsID.size() - 1; i++)
        {
            selection += "?, ";
        }
        selection += "?)";

        CursorLoader cl = new CursorLoader(SoundBoxApplication.getContext(), defaultDirectoryUri, cursorProjection, selection, ids, null);
        myCursor = cl.loadInBackground();

        while (myCursor.moveToNext()) {
            songs.add(new MediaEntry(myCursor.getString(0), MediaType.Song, myCursor.getString(1)));
        }

        Collections.sort(songs);

        myCursor.close();
        return songs;
    }

    /**
     * Returns the sub directories of a given directory
     * 
     * @param directory the parent folder ({@linkplain File})
     * @return list with the subdirs
     */
    public List<File> getSubDirsInAFolder(File directory) {
        List<File> dirs = new ArrayList<File>();

        // list the directories inside the folder
        File list[] = directory.listFiles(myFilter);

        if (list != null) {

            Collections.addAll(dirs, list);

            // sort the directories alphabetically
            Collections.sort(dirs, new SortFileName());
        }
        return dirs;
    }

    /**
     * Returns a list of SongEntries for the Songs in the specified directory.
     * 
     * @param directory the directory in which to search
     * @param projection one key from {@linkplain MediaStore.Audio.Media} to associate on the SongEntry's value
     * @return a list of SongEntries
     */
    public List<MediaEntry> getSongsInAFolder(File directory, String projection) {
        List<MediaEntry> songs = new ArrayList<MediaEntry>();
        String folder = directory.getPath();

        String[] cursorProjection = new String[] { MediaStore.Audio.Media._ID, projection };
        String selection = MediaStore.Audio.Media.IS_MUSIC + " != 0 AND "
                + "SUBSTR(" + MediaStore.Audio.Media.DATA + ",0 , LENGTH('" + folder + "')+1) = '" + folder + "' AND "
                + "SUBSTR(" + MediaStore.Audio.Media.DATA + ",LENGTH('" + folder + "')+1, 200) LIKE '/%.mp3' AND "
                + "SUBSTR(" + MediaStore.Audio.Media.DATA + ",LENGTH('" + folder + "')+1, 200) NOT LIKE '/%/%.mp3'";

        String sortOrder = MediaStore.Audio.Media.TITLE;

        CursorLoader cl = new CursorLoader(SoundBoxApplication.getContext(), MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, cursorProjection, selection, null, sortOrder);
        myCursor = cl.loadInBackground();

        while (myCursor.moveToNext()) {
            songs.add(new MediaEntry(myCursor.getString(0), MediaType.Song, myCursor.getString(1)));
        }

        CursorLoader cl2 = new CursorLoader(SoundBoxApplication.getContext(), MediaStore.Audio.Media.INTERNAL_CONTENT_URI, cursorProjection, selection, null, sortOrder);
        myCursor = cl2.loadInBackground();

        while (myCursor.moveToNext()) {
            songs.add(new MediaEntry(myCursor.getString(0), MediaType.Song, myCursor.getString(1)));
        }

        myCursor.close();
        return songs;
    }

    /**
     * Class to sort the files based on its names (alphabetically)
     */
    private class SortFileName implements Comparator<File> {
        @Override
        public int compare(File f1, File f2) {
            return f1.getName().compareTo(f2.getName());
        }
    }

    /**
     * Class to filter the Files that are not directories.
     */
    private class OnlyDirsFilter implements FileFilter {

        @Override
        public boolean accept(File pathname) {
            return pathname.isDirectory() && !isProtected(pathname);
        }

        private boolean isProtected(File path) {
            return (!path.canRead() && !path.canWrite());
        }

    }
}