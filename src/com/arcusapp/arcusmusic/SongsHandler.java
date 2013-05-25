package com.arcusapp.arcusmusic;

import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import android.content.Context;
import android.database.Cursor;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.content.CursorLoader;

public class SongsHandler {

	private OnlyDirsFilter myFilter;
	public String root_sd;
	public File musicDirectory;

	private Cursor musiccursor;
	private Context _context;

	public SongsHandler(Context cont) {
		// constructor
		myFilter = new OnlyDirsFilter(); // filtro para solo carpetas

		root_sd = Environment.getExternalStorageDirectory().toString(); // raiz
																		// del
																		// sd
		musicDirectory = new File(root_sd + "/Musica");
		this._context = cont;

		checkIfMusicDirectoryExist();
	}

	public List<String> getAllArtists() {
		List<String> artists = new ArrayList<String>();
		CursorLoader cl;
		String selection = MediaStore.Audio.Media.IS_MUSIC + " != 0 AND "
				+ MediaStore.Audio.Artists.ARTIST + " not null ";
		String[] projection = { "DISTINCT " + MediaStore.Audio.Artists.ARTIST };
		cl = new CursorLoader(_context,
				MediaStore.Audio.Media.getContentUriForPath(musicDirectory
						.getPath()), projection, selection, null,
				MediaStore.Audio.Artists.ARTIST);
		musiccursor = cl.loadInBackground();

		while (musiccursor.moveToNext()) {
			artists.add(musiccursor.getString(0));
		}
		return artists;
	}

	public List<String> getArtistAlbums(String artist) {
		List<String> albums = new ArrayList<String>();
		CursorLoader cl;
		String selection = MediaStore.Audio.Media.IS_MUSIC + " != 0 AND "
				+ MediaStore.Audio.Artists.ARTIST + " = '" + artist + "'";

		String[] projection = { "DISTINCT "
				+ MediaStore.Audio.Artists.Albums.ALBUM };
		cl = new CursorLoader(_context,
				MediaStore.Audio.Media.getContentUriForPath(musicDirectory
						.getPath()), projection, selection, null,
				MediaStore.Audio.Artists.Albums.ALBUM);
		musiccursor = cl.loadInBackground();

		while (musiccursor.moveToNext()) {
			albums.add(musiccursor.getString(0));
		}
		return albums;
	}

	public List<String> getArtistSongs(String artist) {
		List<String> ids = new ArrayList<String>();
		CursorLoader cl;
		String selection = MediaStore.Audio.Media.IS_MUSIC + " != 0 AND "
				+ MediaStore.Audio.Artists.ARTIST + " = '" + artist + "'";

		String[] projection = { MediaStore.Audio.Media._ID };
		cl = new CursorLoader(_context,
				MediaStore.Audio.Media.getContentUriForPath(musicDirectory
						.getPath()), projection, selection, null,
				MediaStore.Audio.Media._ID);
		musiccursor = cl.loadInBackground();

		while (musiccursor.moveToNext()) {
			ids.add(musiccursor.getString(0));
		}
		return ids;
	}

	public List<String> getAlbumSongs(String album) {
		List<String> ids = new ArrayList<String>();
		CursorLoader cl;
		String selection = MediaStore.Audio.Media.IS_MUSIC + " != 0 AND "
				+ MediaStore.Audio.Artists.Albums.ALBUM + " = '" + album + "'";

		String[] projection = { MediaStore.Audio.Media._ID };
		cl = new CursorLoader(_context,
				MediaStore.Audio.Media.getContentUriForPath(musicDirectory
						.getPath()), projection, selection, null,
				MediaStore.Audio.Media._ID);
		musiccursor = cl.loadInBackground();

		while (musiccursor.moveToNext()) {
			ids.add(musiccursor.getString(0));
		}
		return ids;
	}

	public List<String> getInformationFromSong(String songID,
			String[] projection) {
		CursorLoader cl;

		String selection = MediaStore.Audio.Media.IS_MUSIC + " != 0 AND "
				+ MediaStore.Audio.Media._ID + " = ?";

		cl = new CursorLoader(_context,
				MediaStore.Audio.Media.getContentUriForPath(musicDirectory
						.getPath()), projection, selection,
				new String[] { songID }, null);
		musiccursor = cl.loadInBackground();

		List<String> information = new ArrayList<String>();

		while (musiccursor.moveToNext()) {
			for (int i = 0; i < musiccursor.getColumnCount(); i++)
				information.add(musiccursor.getString(i));
		}

		return information;
	}

	public List<SongEntry> getSongsInAFolder(File dir, boolean withDirs,
			String projection) {
		List<SongEntry> songs = new ArrayList<SongEntry>();
		String folder = dir.getPath();
		if (withDirs) {
			File list[] = dir.listFiles(myFilter);
			if (list != null) {
				// inicializo las variables
				List<File> Dirs = new ArrayList<File>();

				// cargo los archivos en MP3Files
				for (int i = 0; i < list.length; i++) {
					Dirs.add(list[i]);
				}

				// ordeno los archivos por nombre
				Collections.sort(Dirs, new SortFileName());

				// paso todos los archivos a la lista final
				int cant = Dirs.size();
				for (int i = 0; i < cant; i++) {
					songs.add(new SongEntry("-1", Dirs.get(i).getName()));
				}

			}
		}

		CursorLoader cl;
		String selection = MediaStore.Audio.Media.IS_MUSIC + " != 0 AND "
				+ "SUBSTR(" + MediaStore.Audio.Media.DATA + ",0 , LENGTH('"
				+ folder + "')+1) = '" + folder + "' AND " + "SUBSTR("
				+ MediaStore.Audio.Media.DATA + ",LENGTH('" + folder
				+ "')+1, 200) LIKE '/%.mp3' AND " + "SUBSTR("
				+ MediaStore.Audio.Media.DATA + ",LENGTH('" + folder
				+ "')+1, 200) NOT LIKE '/%/%.mp3'";

		cl = new CursorLoader(_context,
				MediaStore.Audio.Media.getContentUriForPath(folder),
				new String[] { MediaStore.Audio.Media._ID, projection },
				selection, null, MediaStore.Audio.Media._ID);
		musiccursor = cl.loadInBackground();

		while (musiccursor.moveToNext()) {

			songs.add(new SongEntry(musiccursor.getString(0), musiccursor
					.getString(1)));
		}
		return songs;
	}

	public List<SongEntry> getAllSongsWithDisplay(String projection) {
		List<SongEntry> allSongsDisplay = new ArrayList<SongEntry>();

		CursorLoader cl;
		String selection = MediaStore.Audio.Media.IS_MUSIC + " != 0 ";

		cl = new CursorLoader(_context,
				MediaStore.Audio.Media.getContentUriForPath(musicDirectory
						.getPath()), new String[] { MediaStore.Audio.Media._ID,
						projection }, selection, null, null);
		musiccursor = cl.loadInBackground();

		while (musiccursor.moveToNext()) {
			allSongsDisplay.add(new SongEntry(musiccursor.getString(0),
					musiccursor.getString(1)));
		}

		Collections.sort(allSongsDisplay);

		return allSongsDisplay;
	}

	public List<SongEntry> getDisplayForSongs(List<String> songsID,
			String projection) {
		List<SongEntry> songsDisplay = new ArrayList<SongEntry>();
		CursorLoader cl;

		String[] ids = new String[songsID.size()];
		ids = songsID.toArray(ids);

		String selection = MediaStore.Audio.Media.IS_MUSIC + " != 0 AND "
				+ MediaStore.Audio.Media._ID + " IN (";
		for (int i = 0; i < songsID.size() - 1; i++)
			selection += "?, ";

		selection += "?)";

		cl = new CursorLoader(_context,
				MediaStore.Audio.Media.getContentUriForPath(musicDirectory
						.getPath()), new String[] { MediaStore.Audio.Media._ID,
						projection }, selection, ids,
				MediaStore.Audio.Media._ID);
		musiccursor = cl.loadInBackground();

		while (musiccursor.moveToNext()) {
			songsDisplay.add(new SongEntry(musiccursor.getString(0),
					musiccursor.getString(1)));
		}

		Collections.sort(songsDisplay);

		return songsDisplay;
	}

	public List<SongEntry> getAllPlayLists() {
		List<SongEntry> allPlayLists = new ArrayList<SongEntry>();

		CursorLoader cl;
		String[] projection = new String[] { MediaStore.Audio.Playlists._ID,
				MediaStore.Audio.Playlists.NAME };

		cl = new CursorLoader(_context,
				MediaStore.Audio.Playlists.EXTERNAL_CONTENT_URI, projection,
				null, null, null);
		musiccursor = cl.loadInBackground();

		while (musiccursor.moveToNext()) {
			allPlayLists.add(new SongEntry(musiccursor.getString(0),
					musiccursor.getString(1)));
		}

		Collections.sort(allPlayLists);

		return allPlayLists;
	}

	public List<String> getSongsFromPlayList(String playListID) {
		List<String> ids = new ArrayList<String>();
		CursorLoader cl;
		/*
		 * String[] proj = { MediaStore.Audio.Playlists.Members.AUDIO_ID,
		 * MediaStore.Audio.Playlists.Members.ARTIST,
		 * MediaStore.Audio.Playlists.Members.TITLE,
		 * MediaStore.Audio.Playlists.Members._ID };
		 */

		String[] projection = { MediaStore.Audio.Playlists.Members.AUDIO_ID };

		cl = new CursorLoader(_context,
				MediaStore.Audio.Playlists.Members.getContentUri("external",
						Integer.parseInt(playListID)), projection, null, null,
				MediaStore.Audio.Playlists.Members.AUDIO_ID);
		musiccursor = cl.loadInBackground();

		while (musiccursor.moveToNext()) {
			ids.add(musiccursor.getString(0));
		}
		return ids;
	}

	private void checkIfMusicDirectoryExist() {
		if (!musicDirectory.exists()) {
			musicDirectory.mkdirs();
		}
	}

	// sorts based on the files name
	private class SortFileName implements Comparator<File> {
		@Override
		public int compare(File f1, File f2) {
			return f1.getName().compareTo(f2.getName());
		}
	}

	// inner class, generic extension filter
	private class OnlyDirsFilter implements FileFilter {

		@Override
		public boolean accept(File pathname) {
			return pathname.isDirectory();
		}

	}
}
