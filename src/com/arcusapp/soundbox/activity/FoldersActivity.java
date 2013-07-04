package com.arcusapp.soundbox.activity;

import java.io.File;
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
import android.widget.TextView;

import com.arcusapp.soundbox.MediaProvider;
import com.arcusapp.soundbox.R;
import com.arcusapp.soundbox.model.BundleExtra;
import com.arcusapp.soundbox.model.SongEntry;
import com.arcusapp.soundbox.util.MediaEntryHelper;

public class FoldersActivity extends ListActivity implements View.OnClickListener {

	// TODO: set the OnClick mehtods on the layout xml
	private TextView txtDir;
	private Button btnLogo, btnPlayFolder;

	private MediaProvider media;
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

		// TODO: set the OnClick mehtods on the layout xml
		txtDir = (TextView) findViewById(R.id.txtDir);
		btnLogo = (Button) findViewById(R.id.btnLogo);
		btnLogo.setOnClickListener(this);
		btnPlayFolder = (Button) findViewById(R.id.btnPlayFolder);
		btnPlayFolder.setOnClickListener(this);

		media = new MediaProvider();
		actualDir = media.musicDirectory;
		txtDir.setText("Musica/");

		// TODO: FoldersActivitty shouldnt handle the projection
		projection = MediaStore.Audio.Media.TITLE;

		songs = media.getSongsInAFolder(actualDir, projection);
		subDirs = media.getSubDirsInAFolder(actualDir);

		// TODO: with a proper adapter in order to show the subdirs too
		setListAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, mediaEntryHelper.getValues(songs)));
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.folders, menu);
		return true;
	}

	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		super.onListItemClick(l, v, position, id);

		if (songs.get(position).getID() == "-1") {
			TextView txt = (TextView) v;
			File temp_file = new File(actualDir, txt.getText().toString());

			if (!temp_file.isFile()) {
				actualDir = temp_file;
				txtDir.setText(actualDir.toString().split(media.root_sd)[1]);

				songs = media.getSongsInAFolder(actualDir, projection);
				subDirs = media.getSubDirsInAFolder(actualDir);

				// //TODO: with a proper adapter in order to show the subdirs too
				setListAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, mediaEntryHelper.getValues(songs)));
			}

		} else {
			playActivityIntent = new Intent(this, PlayActivity.class);

			Bundle b = new Bundle();
			// set the selected song to the CURRENT_ID flag
			b.putString(BundleExtra.CURRENT_ID, songs.get(position).getID().toString());
			// set the songs in the folder to the SONGS_ID_LIST flag
			b.putStringArrayList(BundleExtra.SONGS_ID_LIST, new ArrayList<String>(mediaEntryHelper.getIDs(songs)));

			playActivityIntent.putExtras(b);
			startActivity(playActivityIntent);
		}
	}

	@Override
	public void onBackPressed() {
		if (!actualDir.equals(media.musicDirectory)) {
			// list parent folder
			actualDir = actualDir.getParentFile();
			txtDir.setText(actualDir.toString().split(media.root_sd)[1]);

			songs = media.getSongsInAFolder(actualDir, projection);
			subDirs = media.getSubDirsInAFolder(actualDir);

			// //TODO: with a proper adapter in order to show the subdirs too
			setListAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, mediaEntryHelper.getValues(songs)));
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
