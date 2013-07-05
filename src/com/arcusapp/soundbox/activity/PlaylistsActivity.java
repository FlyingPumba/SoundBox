package com.arcusapp.soundbox.activity;

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

import com.arcusapp.soundbox.R;
import com.arcusapp.soundbox.data.MediaProvider;
import com.arcusapp.soundbox.model.BundleExtra;
import com.arcusapp.soundbox.model.PlaylistEntry;
import com.arcusapp.soundbox.util.MediaEntryHelper;

public class PlaylistsActivity extends ListActivity implements
		View.OnClickListener {

	private Button btnLogo6;
	private ListView listView;
	private MediaProvider media;
	private MediaEntryHelper<PlaylistEntry> mediaEntryHelper;
	private Intent playActivityIntent;
	private List<PlaylistEntry> playLists;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_play_lists);

		media = new MediaProvider();
		mediaEntryHelper = new MediaEntryHelper<PlaylistEntry>();

		// TODO: set the onClick method on the layout xml
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
				playActivityIntent = new Intent();
				playActivityIntent.setAction("com.arcusapp.soundbox.PLAY_ACTIVITY");

				Bundle b = new Bundle();

				// we play directly the playlist so we dont have a specific first song
				b.putString(BundleExtra.CURRENT_ID, BundleExtra.DefaultValues.DEFAULT_ID);

				String playlistID = playLists.get(pos).getID();
				b.putStringArrayList(BundleExtra.SONGS_ID_LIST, new ArrayList<String>(media.getSongsFromPlaylist(playlistID)));

				playActivityIntent.putExtras(b);
				startActivity(playActivityIntent);

				return true;
			}
		});

		playLists = media.getAllPlayLists();

		setListAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, mediaEntryHelper.getValues(playLists)));

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
		playActivityIntent = new Intent();
		playActivityIntent.setAction("com.arcusapp.soundbox.SONGSLIST_ACTIVITY");

		// Creamos la informacion a pasar entre actividades
		Bundle b = new Bundle();

		String playlistID = playLists.get(position).getID();
		b.putStringArrayList("songs",
				new ArrayList<String>(media.getSongsFromPlaylist(playlistID)));

		// Anadimos la informacion al intent
		playActivityIntent.putExtras(b);

		// Iniciamos la nueva actividad
		startActivity(playActivityIntent);
	}

	@Override
	public void onClick(View v) {
		if (v.getId() == R.id.btnLogo6) {
			Intent activityIntent = new Intent(this, MainActivity.class);
			startActivity(activityIntent);
		}
	}
}
