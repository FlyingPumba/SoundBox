package com.arcusapp.arcusmusic;

import java.util.ArrayList;
import java.util.List;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

public class PlayListsActivity extends ListActivity implements
		View.OnClickListener {

	private Button btnLogo6;
	private ListView listView;
	private SongsHandler sh;
	private Intent PlayActivityIntent;
	private List<SongEntry> playLists;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_play_lists);

		sh = new SongsHandler(this);

		// inicializo los controles
		btnLogo6 = (Button) findViewById(R.id.btnLogo6);
		btnLogo6.setOnClickListener(this);

		listView = (ListView) findViewById(android.R.id.list);

		listView.setLongClickable(true);
		listView.setOnItemLongClickListener(new OnItemLongClickListener() {

			@Override
			public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
					int pos, long id) {
				// we directly play all the playlist

				// Creamos el Intent
				PlayActivityIntent = new Intent();
				PlayActivityIntent
						.setAction("com.arcusapp.arcusmusic.PLAY_ACTIVITY");

				// Creamos la informacion a pasar entre actividades
				Bundle b = new Bundle();
				// -1 porque no hay una cancion actual:
				b.putString("id", "-1");
				// pasamos todas las canciones del playlist clickeado:
				String playlistID = playLists.get(pos).getKey();
				b.putStringArrayList(
						"songs",
						new ArrayList<String>(sh
								.getSongsFromPlayList(playlistID)));

				// Anadimos la informacion al intent
				PlayActivityIntent.putExtras(b);

				// Iniciamos la nueva actividad
				startActivity(PlayActivityIntent);

				return true;
			}
		});

		playLists = sh.getAllPlayLists();

		setListAdapter(new ArrayAdapter<String>(this,
				android.R.layout.simple_list_item_1,
				SongEntry.getValuesList(playLists)));

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.play_lists, menu);
		return true;
	}

	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		super.onListItemClick(l, v, position, id);

		// mostramos las canciones de esa lista de reproduccion llamando al
		// SongsListActivity

		// Creamos el Intent
		PlayActivityIntent = new Intent();
		PlayActivityIntent
				.setAction("com.arcusapp.arcusmusic.SONGSLIST_ACTIVITY");

		// Creamos la informacion a pasar entre actividades
		Bundle b = new Bundle();

		String playlistID = playLists.get(position).getKey();
		b.putStringArrayList("songs",
				new ArrayList<String>(sh.getSongsFromPlayList(playlistID)));

		// Anadimos la informacion al intent
		PlayActivityIntent.putExtras(b);

		// Iniciamos la nueva actividad
		startActivity(PlayActivityIntent);
	}

	@Override
	public void onClick(View v) {
		if (v.getId() == R.id.btnLogo6) {
			Intent activityIntent = new Intent(this, MainActivity.class);
			startActivity(activityIntent);
		}
	}
}
