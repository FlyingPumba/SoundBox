package com.arcusapp.arcusmusic;

import java.io.File;
import java.util.List;
import java.util.Random;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.net.Uri;
import android.os.Binder;
import android.os.IBinder;
import android.provider.MediaStore;
import android.util.Log;

public class MediaPlayerService extends Service implements OnCompletionListener {

	private static final String TAG = "MediaPlayerService";

	private String actualID;
	private List<String> songsList;
	private List<String> repeatList;

	private RepeatState repeatState;
	private boolean randomState;
	Random rnd = new Random();

	private String actualTitle;
	private File actualFile;
	private String actualArtist;
	private String actualAlbum;

	private SongsHandler sh;
	String[] defaultProjection = new String[] { MediaStore.Audio.Media.TITLE,
			MediaStore.Audio.Media.DATA, MediaStore.Audio.Media.ARTIST,
			MediaStore.Audio.Media.ALBUM };

	private MediaPlayer mediaPlayer;
	private MediaPlayerServiceListener mphl;
	private Context context;

	private final IBinder mBinder = new MyBinder();

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		// We want this service to continue running until it is explicitly
		// stopped, so return sticky.

		return Service.START_STICKY;
	}

	@Override
	public IBinder onBind(Intent arg0) {
		return mBinder;
	}

	@Override
	public boolean onUnbind(Intent arg0) {
		mphl = null;
		return true;
	}

	public class MyBinder extends Binder {
		MediaPlayerService getService() {
			return MediaPlayerService.this;
		}
	}

	public boolean SetUp(String actual, List<String> songs,
			SongsHandler songsHandler, MediaPlayerServiceListener listener) {

		if (actual != null) {
			this.actualID = actual;
			this.songsList = songs;
			repeatList = songs;
		}

		if (mphl == null)
			mphl = listener;
		if (mediaPlayer == null)
			mediaPlayer = new MediaPlayer();
		if (sh == null)
			sh = songsHandler;

		// la id que mando el playList Activity es null, significa que no tiene
		// cancion y no va a llamar al TurnOnMediaPlayer
		if (actual == null)
			mphl.onSongChanged();

		return true;
	}

	// Primer metodo para prender el reproductor
	public void TurnOnMediaPlayer() {
		setInfo();
		ChangeSong();
	}

	public String getActualSongID() {
		return actualID;
	}

	public String getActualFileName() {
		return actualFile.getName();
	}

	public String getActualArtist() {
		return actualArtist;
	}

	public String getActualAlbum() {
		return actualAlbum;
	}

	public String getActualTitle() {
		return actualTitle;
	}

	public List<String> getSongsList() {
		return songsList;
	}

	public boolean getRandomState() {
		return randomState;
	}

	public RepeatState getRepeatState() {
		return repeatState;
	}

	public boolean ChangeRandom() {
		randomState = !randomState;
		return randomState;
	}

	public RepeatState ChangeRepeat() {
		if (repeatState == RepeatState.Off) {
			repeatState = RepeatState.All;
		}
		else if (repeatState == RepeatState.All) {
			repeatState = RepeatState.One;
		}
		else if (repeatState == RepeatState.One) {
			repeatState = RepeatState.Off;
		}
		return repeatState;
	}

	public void PlayAndPause() {
		// prendemos la musica !
		if (!mediaPlayer.isPlaying()) {
			// mediaPlayer.setOnCompletionListener(this);
			mediaPlayer.start();
		}
		else {
			mediaPlayer.pause();
		}
	}

	public void NextSong() {
		if (songsList.size() > 1) {
			// tomo la posicion en la lista de la id actual:
			int index = songsList.indexOf(actualID);
			// apago el mediaPlayer
			mediaPlayer.stop();

			if (randomState) {
				int newindex = index;
				while (newindex == index)
					newindex = rnd.nextInt(songsList.size());

				actualID = songsList.get(newindex);
			}
			else {
				// le sumo uno;
				if (index + 1 != songsList.size()) {
					actualID = songsList.get(index + 1);
				}
				else {
					actualID = songsList.get(0);
				}
			}

			setInfo();
			ChangeSong();
		}
	}

	public void PreviousSong() {

		// tomo la posicion en la lista de la id actual:
		int index = songsList.indexOf(actualID);
		// apago el mediaPlayer
		mediaPlayer.stop();

		if (randomState) {
			Random rnd = new Random();
			int newindex = index;
			while (newindex == index)
				newindex = rnd.nextInt(songsList.size());

			actualID = songsList.get(newindex);
		}
		else {
			// le resto uno;
			if (index != 0) {
				actualID = songsList.get(index - 1);
			}
			else {
				actualID = songsList.get(songsList.size() - 1);
			}
		}

		setInfo();
		ChangeSong();
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		if (mediaPlayer != null) {
			mediaPlayer.stop();
			mediaPlayer.release();
			mediaPlayer = null;
		}
	}

	@Override
	public void onCreate() {
		super.onDestroy();
		repeatState = RepeatState.Off;
	}

	private void setInfo() {
		// Recuperamos cierta informacion para mostrar en el reproductor
		List<String> infoSong = sh.getInformationFromSong(actualID,
				defaultProjection);
		// Construimos el mensaje a mostrar
		actualFile = new File(infoSong.get(1));
		actualTitle = infoSong.get(0);
		actualArtist = infoSong.get(2);
		actualAlbum = infoSong.get(3);
	}

	private void ChangeSong() {
		Uri uri = Uri.fromFile(actualFile);
		if (mediaPlayer != null)
			if (mediaPlayer.isPlaying())
				mediaPlayer.stop();
		mediaPlayer = MediaPlayer.create(context, uri);
		Log.d(TAG, "Creating media player, file is: " + actualFile.getName());
		mediaPlayer.setOnCompletionListener(this);
		mediaPlayer.start();

		mphl.onSongChanged();
	}

	@Override
	public void onCompletion(MediaPlayer arg0) {
		// pasar a la siguiente cancion, esto depende del estado del repeat y
		// del random
		if (repeatState == RepeatState.One) {
			mediaPlayer.stop();
			mediaPlayer.start();
		}
		else {

			// tomo la posicion en la lista de la id actual:
			int index = repeatList.indexOf(actualID);

			// me fijo que no haya llegado al tope.
			if (index + 1 != repeatList.size()) {
				// elijo un nuevo elemento, random o no.
				if (randomState) {
					rnd = new Random();
					int newindex = index;
					while (newindex == index)
						newindex = rnd.nextInt(repeatList.size());

					actualID = repeatList.get(newindex);
					repeatList.remove(index);
				}
				else {
					actualID = repeatList.get(index + 1);
					repeatList.remove(index);
				}

				setInfo();
				ChangeSong();
			}
			else {
				// vuelvo a repetir todo o paro el reproductor
				if (repeatState == RepeatState.All) {
					repeatList = songsList;
					if (randomState) {
						rnd = new Random();
						int newindex = rnd.nextInt(repeatList.size());
						actualID = repeatList.get(newindex);
					}
					else
						actualID = repeatList.get(0);

					setInfo();
					ChangeSong();
				}
				else
					// repeat es off y llegamos al final de la lista
					mediaPlayer.stop();
			}
		}
	}
}
