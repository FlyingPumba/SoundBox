package com.arcusapp.soundbox.activity;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.arcusapp.soundbox.R;
import com.arcusapp.soundbox.SoundBoxApplication;
import com.arcusapp.soundbox.data.MediaProvider;
import com.arcusapp.soundbox.model.BundleExtra;
import com.arcusapp.soundbox.model.SongEntry;
import com.arcusapp.soundbox.util.DirectoryHelper;
import com.arcusapp.soundbox.util.MediaEntryHelper;

public class FoldersActivity extends Activity implements View.OnClickListener {

	private TextView txtDir;
	private ListView songsListView, dirsListView;

	private MediaProvider mediaProvider;
	MediaEntryHelper<SongEntry> mediaEntryHelper;
	private Intent playActivityIntent;

	private File actualDir;
	private List<File> subDirs;
	private List<SongEntry> songs;
	String projection;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_folders);

		mediaProvider = new MediaProvider();
		actualDir = mediaProvider.getDefaultDirectory();

		// TODO: init the UI on a different method
		txtDir = (TextView) findViewById(R.id.txtDir);
		txtDir.setText("Musica/");
		songsListView = (ListView) findViewById(R.id.songsList);
		songsListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
				// handle the click on a song
				playActivityIntent = new Intent(SoundBoxApplication.getApplicationContext(), PlayActivity.class);

				Bundle b = new Bundle();
				b.putString(BundleExtra.CURRENT_ID, songs.get(position).getID().toString());
				b.putStringArrayList(BundleExtra.SONGS_ID_LIST, new ArrayList<String>(mediaEntryHelper.getIDs(songs)));
				playActivityIntent.putExtras(b);

				startActivity(playActivityIntent);
			}
		});
		dirsListView = (ListView) findViewById(R.id.dirsList);
		dirsListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
				// handle the click on a directory
				actualDir = new File(subDirs.get(position).getPath());
				// txtDir.setText(actualDir.toString().split(media.root_sd)[1]);
				txtDir.setText(actualDir.getName());

				songs = mediaProvider.getSongsInAFolder(actualDir, projection);
				subDirs = mediaProvider.getSubDirsInAFolder(actualDir);

				// TODO: WRITE A PROPER ADAPTER !!
				if (!songs.isEmpty()) {
					songsListView.setAdapter(new ArrayAdapter<String>(SoundBoxApplication.getApplicationContext(), android.R.layout.simple_list_item_1, mediaEntryHelper.getValues(songs)));
				} else {
					ArrayAdapter<String> adapter = (ArrayAdapter<String>) songsListView.getAdapter();
					adapter.clear();

				}
				if (!subDirs.isEmpty()) {
					dirsListView.setAdapter(new ArrayAdapter<String>(SoundBoxApplication.getApplicationContext(), android.R.layout.simple_list_item_1, DirectoryHelper.getNamesFromFiles(subDirs)));
				} else {
					ArrayAdapter<String> adapter = (ArrayAdapter<String>) dirsListView.getAdapter();
					adapter.clear();

				}
			}
		});

		// TODO: FoldersActivitty shouldnt handle the projection
		projection = MediaStore.Audio.Media.TITLE;

		// set the first options for the user
		songs = new ArrayList<SongEntry>();
		subDirs = mediaProvider.getDefaultUserOptions();

		songsListView.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, new ArrayList<String>()));
		dirsListView.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, DirectoryHelper.getNamesFromFiles(subDirs)));
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.folders, menu);
		return true;
	}

	@Override
	public void onBackPressed() {
		// check if the actual dir is not an sd card
		if (!mediaProvider.getSDCards().contains(actualDir)) {
			// list parent folder
			actualDir = actualDir.getParentFile();
			txtDir.setText(actualDir.getName());

			songs = mediaProvider.getSongsInAFolder(actualDir, projection);
			subDirs = mediaProvider.getSubDirsInAFolder(actualDir);

			// TODO: WRITE A PROPER ADAPTER !!
			if (!songs.isEmpty()) {
				songsListView.setAdapter(new ArrayAdapter<String>(SoundBoxApplication.getApplicationContext(), android.R.layout.simple_list_item_1, mediaEntryHelper.getValues(songs)));
			} else {
				ArrayAdapter<String> adapter = (ArrayAdapter<String>) songsListView.getAdapter();
				adapter.clear();

			}
			if (!subDirs.isEmpty()) {
				dirsListView.setAdapter(new ArrayAdapter<String>(SoundBoxApplication.getApplicationContext(), android.R.layout.simple_list_item_1, DirectoryHelper.getNamesFromFiles(subDirs)));
			} else {
				ArrayAdapter<String> adapter = (ArrayAdapter<String>) dirsListView.getAdapter();
				adapter.clear();

			}
		} else {
			finish();
		}
	}

	@Override
	public void onClick(View v) {
		if (v.getId() == R.id.btnLogo) {
			finish();
		} else if (v.getId() == R.id.btnPlayFolder) {
			// TODO: play current folder.
		}
	}
}
