package com.arcusapp.soundbox.data;

import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.content.CursorLoader;

import com.arcusapp.soundbox.SoundBoxApplication;
import com.arcusapp.soundbox.model.PlaylistEntry;
import com.arcusapp.soundbox.model.Song;
import com.arcusapp.soundbox.model.SongEntry;
import com.arcusapp.soundbox.util.DirectoryHelper;

public class MediaProvider {

	private OnlyDirsFilter myFilter;
	private File defaultDirectory;
	private Uri defaultDirectoryUri;

	List<File> sdCards;

	File musicDirectory;

	private Cursor myCursor;

	public MediaProvider() {
		myFilter = new OnlyDirsFilter();

		musicDirectory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MUSIC);
		searchForSDCards();
		defaultDirectory = sdCards.get(0);
		defaultDirectoryUri = MediaStore.Audio.Media.getContentUriForPath(defaultDirectory.getPath());
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

	public File getDefaultDirectory() {
		return defaultDirectory;
	}

	public List<File> getDefaultUserOptions() {
		List<File> defaultUserOptions = new ArrayList<File>();
		defaultUserOptions.add(musicDirectory);
		defaultUserOptions.addAll(sdCards);
		return defaultUserOptions;
	}

	public List<File> getSDCards() {
		return sdCards;
	}

	/**
	 * Returns the id of all the Songs in the MediaStore.
	 * 
	 * @return list with the ids
	 */
	public List<String> getAllSongs() {
		List<String> songs = new ArrayList<String>();

		String[] cursorProjection = new String[] { MediaStore.Audio.Media._ID };
		String selection = MediaStore.Audio.Media.IS_MUSIC + " != 0 ";

		CursorLoader cl = new CursorLoader(SoundBoxApplication.getApplicationContext(), defaultDirectoryUri, cursorProjection, selection, null, null);
		myCursor = cl.loadInBackground();

		while (myCursor.moveToNext()) {
			songs.add(myCursor.getString(0));
		}

		Collections.sort(songs);

		return songs;
	}

	// XXX: delete this method if we are not going to use it
	/**
	 * Returns a list of SongEntries for all the Songs in the MediaStore.
	 * 
	 * @param projection one key from {@linkplain MediaStore.Audio.Media} to associate on the SongEntry's value
	 * @return a list of SongEntries
	 * @deprecated NOT USED
	 */
	public List<SongEntry> getAllSongsWithValue(String projection) {
		List<SongEntry> allSongsDisplay = new ArrayList<SongEntry>();

		String[] cursorProjection = new String[] { MediaStore.Audio.Media._ID, projection };
		String selection = MediaStore.Audio.Media.IS_MUSIC + " != 0 ";

		CursorLoader cl = new CursorLoader(SoundBoxApplication.getApplicationContext(), defaultDirectoryUri, cursorProjection, selection, null, null);
		myCursor = cl.loadInBackground();

		while (myCursor.moveToNext()) {
			allSongsDisplay.add(new SongEntry(myCursor.getString(0), myCursor.getString(1)));
		}

		Collections.sort(allSongsDisplay);

		return allSongsDisplay;
	}

	/**
	 * Returns the name ({@linkplain MediaStore.Audio.Artists.ARTIST}) of all the Artists in the MediaStore.
	 * 
	 * @return list with the names
	 */
	public List<String> getAllArtists() {
		List<String> artists = new ArrayList<String>();

		String[] projection = { "DISTINCT " + MediaStore.Audio.Artists.ARTIST };
		String selection = MediaStore.Audio.Media.IS_MUSIC + " != 0 AND " + MediaStore.Audio.Artists.ARTIST + " not null ";
		String sortOrder = MediaStore.Audio.Artists.ARTIST;

		CursorLoader cl = new CursorLoader(SoundBoxApplication.getApplicationContext(), defaultDirectoryUri, projection, selection, null, sortOrder);
		myCursor = cl.loadInBackground();

		while (myCursor.moveToNext()) {
			artists.add(myCursor.getString(0));
		}
		return artists;
	}

	/**
	 * Returns the name ({@linkplain MediaStore.Audio.Albums.ALBUM}) of all the Albums of the specified Artist in the MediaStore.
	 * 
	 * @param artist the name of the Artist ({@linkplain MediaStore.Audio.Artists.ARTIST})
	 * @return list with the names
	 */
	public List<String> getAlbumsFromArtist(String artist) {
		List<String> albums = new ArrayList<String>();

		String[] projection = { "DISTINCT " + MediaStore.Audio.Artists.Albums.ALBUM };
		String selection = MediaStore.Audio.Media.IS_MUSIC + " != 0 AND " + MediaStore.Audio.Artists.ARTIST + " = '" + artist + "'";
		String sortOrder = MediaStore.Audio.Artists.Albums.ALBUM;

		CursorLoader cl = new CursorLoader(SoundBoxApplication.getApplicationContext(), defaultDirectoryUri, projection, selection, null, sortOrder);
		myCursor = cl.loadInBackground();

		while (myCursor.moveToNext()) {
			albums.add(myCursor.getString(0));
		}
		return albums;
	}

	/**
	 * Returns the id ({@linkplain MediaStore.Audio.Media._ID}) of all the Songs of the specified Artist in the MediaStore.
	 * 
	 * @param artist the name of the Artist ({@linkplain MediaStore.Audio.Artists.ARTIST})
	 * @return list with the ids
	 */
	public List<String> getSongsFromArtist(String artist) {
		List<String> ids = new ArrayList<String>();

		String[] projection = { MediaStore.Audio.Media._ID };
		String selection = MediaStore.Audio.Media.IS_MUSIC + " != 0 AND " + MediaStore.Audio.Artists.ARTIST + " = '" + artist + "'";
		String sortOrder = MediaStore.Audio.Media.TITLE;

		CursorLoader cl = new CursorLoader(SoundBoxApplication.getApplicationContext(), defaultDirectoryUri, projection, selection, null, sortOrder);
		myCursor = cl.loadInBackground();

		while (myCursor.moveToNext()) {
			ids.add(myCursor.getString(0));
		}
		return ids;
	}

	/**
	 * Returns the id ({@linkplain MediaStore.Audio.Media._ID}) of all the Songs of the specified Album in the MediaStore.
	 * 
	 * @param album the name of the Album ({@linkplain MediaStore.Audio.Albums.ALBUM})
	 * @return list with the ids
	 */
	public List<String> getSongsFromAlbum(String album) {
		List<String> ids = new ArrayList<String>();

		String[] projection = { MediaStore.Audio.Media._ID };
		String selection = MediaStore.Audio.Media.IS_MUSIC + " != 0 AND " + MediaStore.Audio.Artists.Albums.ALBUM + " = '" + album + "'";
		String sortOrder = MediaStore.Audio.Media.TITLE;

		CursorLoader cl = new CursorLoader(SoundBoxApplication.getApplicationContext(), defaultDirectoryUri, projection, selection, null, sortOrder);
		myCursor = cl.loadInBackground();

		while (myCursor.moveToNext()) {
			ids.add(myCursor.getString(0));
		}
		return ids;
	}

	/**
	 * Returns a list of PlaylistEntries for all the Playlists in the MediaStore.
	 * 
	 * @return a list of PlaylistEntries. The value associated is the name of the Playlist ({@linkplain MediaStore.Audio.Playlists.NAME})
	 */
	public List<PlaylistEntry> getAllPlayLists() {
		List<PlaylistEntry> playLists = new ArrayList<PlaylistEntry>();

		String[] projection = new String[] { MediaStore.Audio.Playlists._ID, MediaStore.Audio.Playlists.NAME };

		CursorLoader cl = new CursorLoader(SoundBoxApplication.getApplicationContext(), MediaStore.Audio.Playlists.EXTERNAL_CONTENT_URI, projection, null, null, null);
		myCursor = cl.loadInBackground();

		while (myCursor.moveToNext()) {
			playLists.add(new PlaylistEntry(myCursor.getString(0),
					myCursor.getString(1)));
		}

		Collections.sort(playLists);

		return playLists;
	}

	/**
	 * Returns the id ({@linkplain MediaStore.Audio.Playlists.Members.AUDIO_ID}) of all the Songs of the specified Playlist in the MediaStore.
	 * 
	 * @param playListID ({@linkplain MediaStore.Audio.Playlists._ID})
	 * @return list with the ids
	 */
	public List<String> getSongsFromPlaylist(String playListID) {
		List<String> ids = new ArrayList<String>();

		Uri specialUri = MediaStore.Audio.Playlists.Members.getContentUri("external", Integer.parseInt(playListID));
		String[] projection = { MediaStore.Audio.Playlists.Members.AUDIO_ID };
		String sortOrder = MediaStore.Audio.Playlists.Members.AUDIO_ID;

		CursorLoader cl = new CursorLoader(SoundBoxApplication.getApplicationContext(), specialUri, projection, null, null, sortOrder);
		myCursor = cl.loadInBackground();

		while (myCursor.moveToNext()) {
			ids.add(myCursor.getString(0));
		}
		return ids;
	}

	public Song getSongFromID(String songID) {
		Song song;
		List<String> values = new ArrayList<String>();

		String[] projection = { MediaStore.Audio.Media._ID, MediaStore.Audio.Media.TITLE,
				MediaStore.Audio.Media.ARTIST, MediaStore.Audio.Media.ALBUM, MediaStore.Audio.Media.DATA };

		String selection = MediaStore.Audio.Media.IS_MUSIC + " != 0 AND " + MediaStore.Audio.Media._ID + " = ?";
		String[] selectionArgs = new String[] { songID };

		CursorLoader cl = new CursorLoader(SoundBoxApplication.getApplicationContext(), defaultDirectoryUri, projection, selection, selectionArgs, null);
		myCursor = cl.loadInBackground();

		myCursor.moveToNext();
		for (int i = 0; i < myCursor.getColumnCount(); i++)
			values.add(myCursor.getString(i));
		if (values.size() > 0) {
			song = new Song(values.get(0), values.get(1), values.get(2), values.get(3), values.get(4));
		} else {
			song = new Song();
		}

		return song;
	}

	/**
	 * Returns the values corresponding to the given projection keys for the specified Song.
	 * 
	 * @param songID ({@linkplain MediaStore.Audio.Media._ID})
	 * @param projection array with keys from {@linkplain MediaStore.Audio.Media}
	 * @return list with the values
	 * @deprecated @see {@linkplain #getSongFromID(String)}
	 */
	public List<String> getValuesFromSong(String songID, String[] projection) {
		List<String> information = new ArrayList<String>();

		String selection = MediaStore.Audio.Media.IS_MUSIC + " != 0 AND " + MediaStore.Audio.Media._ID + " = ?";
		String[] selectionArgs = new String[] { songID };

		CursorLoader cl = new CursorLoader(SoundBoxApplication.getApplicationContext(), defaultDirectoryUri, projection, selection, selectionArgs, null);
		myCursor = cl.loadInBackground();

		myCursor.moveToNext();
		for (int i = 0; i < myCursor.getColumnCount(); i++)
			information.add(myCursor.getString(i));

		return information;
	}

	/**
	 * Returns a list of SongEntries for the specified Songs.
	 * 
	 * @param songsID list of ids ({@linkplain MediaStore.Audio.Media._ID})
	 * @param projection one key from {@linkplain MediaStore.Audio.Media} to associate on the SongEntry's value
	 * @return a list of SongEntries
	 */
	public List<SongEntry> getValueFromSongs(List<String> songsID, String projection) {
		List<SongEntry> songs = new ArrayList<SongEntry>();

		String[] ids = new String[songsID.size()];
		ids = songsID.toArray(ids);

		String[] cursorProjection = new String[] { MediaStore.Audio.Media._ID, projection, MediaStore.Audio.Media.DATA };

		String selection = MediaStore.Audio.Media.IS_MUSIC + " != 0 AND " + MediaStore.Audio.Media._ID + " IN (";
		for (int i = 0; i < songsID.size() - 1; i++)
			selection += "?, ";
		selection += "?)";

		String sortOrder = MediaStore.Audio.Media._ID;

		CursorLoader cl = new CursorLoader(SoundBoxApplication.getApplicationContext(), defaultDirectoryUri, cursorProjection, selection, ids, sortOrder);
		myCursor = cl.loadInBackground();

		while (myCursor.moveToNext()) {
			songs.add(new SongEntry(myCursor.getString(0), myCursor.getString(1)));
		}

		Collections.sort(songs);

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

			for (int i = 0; i < list.length; i++) {
				dirs.add(list[i]);
			}

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
	public List<SongEntry> getSongsInAFolder(File directory, String projection) {
		List<SongEntry> songs = new ArrayList<SongEntry>();
		String folder = directory.getPath();

		String[] cursorProjection = new String[] { MediaStore.Audio.Media._ID, projection };
		String selection = MediaStore.Audio.Media.IS_MUSIC + " != 0 AND "
				+ "SUBSTR(" + MediaStore.Audio.Media.DATA + ",0 , LENGTH('" + folder + "')+1) = '" + folder + "' AND "
				+ "SUBSTR(" + MediaStore.Audio.Media.DATA + ",LENGTH('" + folder + "')+1, 200) LIKE '/%.mp3' AND "
				+ "SUBSTR(" + MediaStore.Audio.Media.DATA + ",LENGTH('" + folder + "')+1, 200) NOT LIKE '/%/%.mp3'";

		String sortOrder = MediaStore.Audio.Media._ID;

		CursorLoader cl = new CursorLoader(SoundBoxApplication.getApplicationContext(), MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, cursorProjection, selection, null, sortOrder);
		myCursor = cl.loadInBackground();

		while (myCursor.moveToNext()) {
			songs.add(new SongEntry(myCursor.getString(0), myCursor.getString(1)));
		}

		CursorLoader cl2 = new CursorLoader(SoundBoxApplication.getApplicationContext(), MediaStore.Audio.Media.INTERNAL_CONTENT_URI, cursorProjection, selection, null, sortOrder);
		myCursor = cl2.loadInBackground();

		while (myCursor.moveToNext()) {
			songs.add(new SongEntry(myCursor.getString(0), myCursor.getString(1)));
		}

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
