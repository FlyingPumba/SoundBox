package com.arcusapp.soundbox.activity;

import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.Toast;

import com.arcusapp.soundbox.R;
import com.arcusapp.soundbox.SoundBoxApplication;
import com.arcusapp.soundbox.adapter.SonglistAcitivityAdapter;
import com.arcusapp.soundbox.model.BundleExtra;

public class SongListActivity extends Activity implements View.OnClickListener {

	ListView myListView;
	SonglistAcitivityAdapter myAdapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_songs_list);

		myListView = (ListView) findViewById(R.id.songslistActivityList);

		try {
			Bundle bundle = this.getIntent().getExtras();

			String focusedElementID = bundle.getString(BundleExtra.CURRENT_ID, BundleExtra.DefaultValues.DEFAULT_ID);
			List<String> songsIDs = bundle.getStringArrayList(BundleExtra.SONGS_ID_LIST);

			myAdapter = new SonglistAcitivityAdapter(this, focusedElementID, songsIDs);
			myListView.setAdapter(myAdapter);
			myListView.setOnItemClickListener(new OnItemClickListener() {

				@Override
				public void onItemClick(AdapterView<?> arg0, View view, int position, long arg3) {
					myAdapter.onSongClick(position);
				}
			});

			myListView.setSelection(myAdapter.getFocusedIDPosition());

		} catch (Exception e) {
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
			Intent activityIntent = new Intent();
			activityIntent.setAction(SoundBoxApplication.ACTION_MAIN_ACTIVITY);
			startActivity(activityIntent);
		}
	}

	@Override
	public void onBackPressed() {
		finish();
	}
}
