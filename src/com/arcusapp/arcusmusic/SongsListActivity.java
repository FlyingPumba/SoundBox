package com.arcusapp.arcusmusic;

import java.util.ArrayList;
import java.util.List;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.Menu;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

public class SongsListActivity extends ListActivity implements
		View.OnClickListener {

	private Button btnLogo5;

	String actualID;
	List<String> songsIDs;
	List<SongEntry> songs;
	SongsHandler sh;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_songs_list);

		// inicializo los controles
		btnLogo5 = (Button) findViewById(R.id.btnLogo5);
		btnLogo5.setOnClickListener(this);

		sh = new SongsHandler(this);
		// Recuperamos la informacion pasada en el intent
		Bundle bundle = this.getIntent().getExtras();
		// ojo con este actualID, puede ser un id cualquiera o -1 (Un playlist
		// mostrando las canciones), en caso de que no fuera -1 tendria que
		// resaltarlo.
		actualID = bundle.getString("id");
		songsIDs = bundle.getStringArrayList("songs");

		String projection = MediaStore.Audio.Media.TITLE;
		songs = sh.getDisplayForSongs(songsIDs, projection);

		setListAdapter(new ArrayAdapter<String>(this,
				android.R.layout.simple_list_item_1,
				SongEntry.getValuesList(songs)));

		// coloreo de amarillo la cancion actual
		/*
		 * if(actualID != "-1"){ ListView lv =
		 * (ListView)findViewById(android.R.id.list); TextView v =
		 * (TextView)lv.getChildAt(Integer.parseInt(actualID));
		 * v.setBackgroundColor(Color.YELLOW); }
		 */
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.songs_list, menu);
		return true;
	}

	@Override
	public void onClick(View v) {
		if (v.getId() == R.id.btnLogo5) {
			Intent activityIntent = new Intent(this, MainActivity.class);
			startActivity(activityIntent);
		}

	}

	@Override
	public void onBackPressed() {
		finish();
	}

	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		super.onListItemClick(l, v, position, id);

		// Creamos el Intent
		Intent PlayActivityIntent = new Intent();
		PlayActivityIntent.setAction("com.arcusapp.arcusmusic.PLAY_ACTIVITY");

		// Creamos la informacion a pasar entre actividades
		Bundle b = new Bundle();
		// cancion actual:
		b.putString("id", songs.get(position).getKey().toString());
		// todas las demas canciones:
		b.putStringArrayList("songs", new ArrayList<String>(songsIDs));

		// Anadimos la informacion al intent
		PlayActivityIntent.putExtras(b);

		// Iniciamos la nueva actividad
		startActivity(PlayActivityIntent);
	}

}
