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
import android.widget.ListView;

import com.arcusapp.soundbox.R;
import com.arcusapp.soundbox.SoundBoxApplication;
import com.arcusapp.soundbox.data.MediaProvider;
import com.arcusapp.soundbox.model.BundleExtra;
import com.arcusapp.soundbox.model.PlaylistEntry;
import com.arcusapp.soundbox.util.MediaEntryHelper;

public class PlaylistsActivity extends ListActivity implements View.OnClickListener {

	private ListView listView;
	private MediaProvider mediaProvider;
	private MediaEntryHelper<PlaylistEntry> mediaEntryHelper;
	private Intent playActivityIntent;
	private List<PlaylistEntry> playLists;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_play_lists);

		mediaProvider = new MediaProvider();
		mediaEntryHelper = new MediaEntryHelper<PlaylistEntry>();

		listView = (ListView) findViewById(android.R.id.list);

		listView.setLongClickable(true);
		listView.setOnItemLongClickListener(new OnItemLongClickListener() {

			@Override
			public boolean onItemLongClick(AdapterView<?> arg0, View arg1, int pos, long id) {

				playActivityIntent = new Intent();
				playActivityIntent.setAction("com.arcusapp.soundbox.PLAY_ACTIVITY");

				Bundle b = new Bundle();

				// we play directly the playlist so we dont have a specific first song
				b.putString(BundleExtra.CURRENT_ID, BundleExtra.DefaultValues.DEFAULT_ID);

				String playlistID = playLists.get(pos).getID();
				b.putStringArrayList(BundleExtra.SONGS_ID_LIST, new ArrayList<String>(mediaProvider.getSongsFromPlaylist(playlistID)));

				playActivityIntent.putExtras(b);
				startActivity(playActivityIntent);

				return true;
			}
		});

		playLists = mediaProvider.getAllPlayLists();

		setListAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, mediaEntryHelper.getValues(playLists)));
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.play_lists, menu);
		return true;
	}

	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		super.onListItemClick(l, v, position, id);

		// show the songs from that specific playlists on the SongsListActivity
		playActivityIntent = new Intent();
		playActivityIntent.setAction(SoundBoxApplication.ACTION_SONGLIST_ACTIVITY);

		Bundle b = new Bundle();
		String playlistID = playLists.get(position).getID();
		b.putStringArrayList(BundleExtra.SONGS_ID_LIST, new ArrayList<String>(mediaProvider.getSongsFromPlaylist(playlistID)));
		playActivityIntent.putExtras(b);

		startActivity(playActivityIntent);
	}

	@Override
	public void onClick(View v) {
		if (v.getId() == R.id.btnHomePlaylistsActivity) {
			Intent activityIntent = new Intent(this, MainActivity.class);
			startActivity(activityIntent);
		}
	}
}
