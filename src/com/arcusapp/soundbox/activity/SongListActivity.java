package com.arcusapp.soundbox.activity;

import java.util.ArrayList;
import java.util.List;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.Menu;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.arcusapp.soundbox.R;
import com.arcusapp.soundbox.SoundBoxApplication;
import com.arcusapp.soundbox.data.MediaProvider;
import com.arcusapp.soundbox.model.BundleExtra;
import com.arcusapp.soundbox.model.SongEntry;
import com.arcusapp.soundbox.util.MediaEntryHelper;

public class SongListActivity extends ListActivity implements View.OnClickListener {

	String focusedElementID;
	List<SongEntry> songs;
	MediaProvider mediaProvider;
	MediaEntryHelper<SongEntry> mediaEntryHelper;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_songs_list);

		mediaProvider = new MediaProvider();
		mediaEntryHelper = new MediaEntryHelper<SongEntry>();

		try {
			Bundle bundle = this.getIntent().getExtras();

			// TODO: use the focusedElementID
			focusedElementID = bundle.getString(BundleExtra.CURRENT_ID, BundleExtra.DefaultValues.DEFAULT_ID);
			List<String> songsIDs = bundle.getStringArrayList(BundleExtra.SONGS_ID_LIST);

			// XXX: SongslistActivity shoulndt know the projection
			String projection = MediaStore.Audio.Media.TITLE;
			songs = mediaProvider.getValueFromSongs(songsIDs, projection);

			// XXX: make a proper adapter
			setListAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, mediaEntryHelper.getValues(songs)));
		}
		catch (Exception e) {
			Toast.makeText(SoundBoxApplication.getApplicationContext(), "Error while trying to show the songs", Toast.LENGTH_LONG).show();
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
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

		Intent playActivityIntent = new Intent();
		playActivityIntent.setAction("com.arcusapp.soundbox.PLAY_ACTIVITY");

		Bundle b = new Bundle();
		b.putString(BundleExtra.CURRENT_ID, songs.get(position).getID().toString());
		b.putStringArrayList(BundleExtra.SONGS_ID_LIST, new ArrayList<String>(mediaEntryHelper.getIDs(songs)));

		playActivityIntent.putExtras(b);
		startActivity(playActivityIntent);
	}
}
