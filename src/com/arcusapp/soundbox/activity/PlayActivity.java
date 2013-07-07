package com.arcusapp.soundbox.activity;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.arcusapp.soundbox.R;
import com.arcusapp.soundbox.data.MediaProvider;
import com.arcusapp.soundbox.model.BundleExtra;
import com.arcusapp.soundbox.model.MediaPlayerServiceListener;
import com.arcusapp.soundbox.model.RepeatState;
import com.arcusapp.soundbox.player.MediaPlayerService;

public class PlayActivity extends Activity implements OnClickListener, MediaPlayerServiceListener {

	TextView txtTitle, txtFile, txtArtist, txtAlbum;
	Button btnSwitchRandom, btnSwitchRepeat;

	private MediaProvider mediaProvider;
	private MediaPlayerService mediaService;

	String currentID;
	List<String> temp_songs;

	// FIXME: check and rewrite if necessary, this block may contain bugs
	private ServiceConnection mConnection = new ServiceConnection() {

		public void onServiceConnected(ComponentName className, IBinder binder) {
			mediaService = ((MediaPlayerService.MyBinder) binder).getService();
			Toast.makeText(PlayActivity.this, "Service Conectado",
					Toast.LENGTH_SHORT).show();

			// el service estaba apagado
			if (mediaService.getActualSongID() == null) {
				if (currentID == null) {
					// aca tendria que estar lo del XML, para cuando entras al
					// play list desde el menu princpial y el servicio estaba
					// apagado
					mediaService.SetUp(null, null, mediaProvider, PlayActivity.this);
				}
				else {
					// asignamos la nueva información, si actualID es -1
					// significa que no hay una cancion actual reproduciendose,
					// así que le mandamos la primera de la lista
					if (currentID.equals("-1")) {
						mediaService.SetUp(temp_songs.get(0), temp_songs, mediaProvider,
								PlayActivity.this);
					}
					else {
						mediaService.SetUp(currentID, temp_songs, mediaProvider, PlayActivity.this);
					}
					mediaService.TurnOnMediaPlayer();
				}
			}
			else {
				// el service estaba prendido, y le asginamos la nueva
				// informacion
				if (currentID == null) {
					// play list desde el menu princpial y el servicio estaba
					// prendido
					getInformationFromMediaService();
				}
				else if (currentID.equals("-1")) {
					mediaService.SetUp(temp_songs.get(0), temp_songs, mediaProvider,
							PlayActivity.this);
					mediaService.TurnOnMediaPlayer();
				}
				else {
					mediaService.SetUp(currentID, temp_songs, mediaProvider, PlayActivity.this);
					mediaService.TurnOnMediaPlayer();
				}
			}
		}

		public void onServiceDisconnected(ComponentName className) {
			mediaService = null;
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_play);

		mediaProvider = new MediaProvider();

		txtTitle = (TextView) findViewById(R.id.txtActualSongTitle);
		txtFile = (TextView) findViewById(R.id.txtActualSongFile);
		txtArtist = (TextView) findViewById(R.id.txtActualSongArtist);
		txtAlbum = (TextView) findViewById(R.id.txtActualSongAlbum);

		btnSwitchRandom = (Button) findViewById(R.id.btnSwitchRandom);
		btnSwitchRandom.setOnClickListener(this);
		btnSwitchRandom.setText("Random Off");

		btnSwitchRepeat = (Button) findViewById(R.id.btnSwitchRepeat);
		btnSwitchRepeat.setOnClickListener(this);
		btnSwitchRepeat.setText("Repeat Off");

		// FIXME: this shouldnt be necessary, rewrite !
		/*
		 * este try-catch es porque podemos entrar directamente al PlayActivity
		 * desde el MainActivity, y en ese caso el bundle.getString tira una
		 * excepcion, porque no encuentra el extra "id". En el futuro, siempre
		 * tiene que haber una cancion en el reproductor.
		 */
		try {
			// Recuperamos la informacion pasada en el intent
			Bundle bundle = this.getIntent().getExtras();
			currentID = bundle.getString("id");
			temp_songs = bundle.getStringArrayList("songs");
		} catch (Exception ex) {
		}

		Intent intent = new Intent(this, MediaPlayerService.class);
		this.startService(intent);
		this.bindService(intent, mConnection, Context.BIND_AUTO_CREATE);

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.play, menu);
		return true;
	}

	// TODO: split the large onClick method on differents onClick methods for each button
	@Override
	public void onClick(View v) {
		if (v.getId() == R.id.btnPlayPause) {
			mediaService.PlayAndPause();
		}
		else if (v.getId() == R.id.btnPrevSong) {
			mediaService.PreviousSong();

		}
		else if (v.getId() == R.id.btnNextSong) {
			mediaService.NextSong();
		}
		else if (v.getId() == R.id.btnSwitchRandom) {
			if (mediaService.ChangeRandom())
				this.btnSwitchRandom.setText("Random On");
			else
				this.btnSwitchRandom.setText("Random Off");
		}
		else if (v.getId() == R.id.btnSwitchRepeat) {
			RepeatState rs = mediaService.ChangeRepeat();
			if (rs == RepeatState.Off) {
				this.btnSwitchRepeat.setText("Repeat Off");
			}
			else if (rs == RepeatState.All) {
				this.btnSwitchRepeat.setText("Repeat All");
			}
			else if (rs == RepeatState.One) {
				this.btnSwitchRepeat.setText("Repeat One");
			}

		}
		else if (v.getId() == R.id.btnActualPlayList) {
			Intent intent = new Intent();
			intent.setAction("com.arcusapp.soundbox.SONGSLIST_ACTIVITY");

			Bundle b = new Bundle();
			b.putString(BundleExtra.CURRENT_ID, mediaService.getActualSongID());
			b.putStringArrayList(BundleExtra.SONGS_ID_LIST, new ArrayList<String>(mediaService.getSongsList()));

			intent.putExtras(b);
			startActivity(intent);
		}
		else if (v.getId() == R.id.btnLogo4) {
			Intent activityIntent = new Intent(this, MainActivity.class);
			startActivity(activityIntent);
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		if (mediaService != null)
			unbindService(mConnection);
	}

	@Override
	public void onSongChanged() {
		getInformationFromMediaService();
	}

	// FIXME: This method may contain bugs, check if the approach is correct
	private void getInformationFromMediaService() {
		txtTitle.setText(mediaService.getActualTitle());
		txtFile.setText(mediaService.getActualFileName());
		txtArtist.setText(mediaService.getActualArtist());
		txtAlbum.setText(mediaService.getActualAlbum());

		RepeatState rs = mediaService.getRepeatState();
		if (rs == RepeatState.Off) {
			this.btnSwitchRepeat.setText("Repeat Off");
		}
		else if (rs == RepeatState.All) {
			this.btnSwitchRepeat.setText("Repeat All");
		}
		else if (rs == RepeatState.One) {
			this.btnSwitchRepeat.setText("Repeat One");
		}

		if (mediaService.getRandomState())
			this.btnSwitchRandom.setText("Random On");
		else
			this.btnSwitchRandom.setText("Random Off");
	}
}
