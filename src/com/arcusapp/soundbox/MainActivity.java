package com.arcusapp.soundbox;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.Button;

public class MainActivity extends Activity implements View.OnClickListener {

	Intent activityIntent;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		initializeUIComponents();

		SoundBoxApplication.setInitialContext(getApplicationContext());
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public void onClick(View v) {
		if (v.getId() == R.id.btnPlaying) {
			activityIntent = new Intent();
			activityIntent.setAction("com.arcusapp.soundbox.PLAY_ACTIVITY");
			startActivity(activityIntent);
		}
		else if (v.getId() == R.id.btnSeeFolders) {
			activityIntent = new Intent();
			activityIntent.setAction("com.arcusapp.soundbox.FOLDERS_ACTIVITY");
			startActivity(activityIntent);
		}
		else if (v.getId() == R.id.btnPlayLists) {
			activityIntent = new Intent();
			activityIntent.setAction("com.arcusapp.soundbox.PLAYLISTS_ACTIVITY");
			startActivity(activityIntent);
		}
		else if (v.getId() == R.id.btnSongs) {
			activityIntent = new Intent();
			activityIntent.setAction("com.arcusapp.soundbox.SONGS_ACTIVITY");
			startActivity(activityIntent);
		}
		else if (v.getId() == R.id.btnArtists) {
			activityIntent = new Intent();
			activityIntent.setAction("com.arcusapp.soundbox.ARTISTS_ACTIVITY");
			startActivity(activityIntent);
		}
	}

	private void initializeUIComponents() {
		Button button = (Button) findViewById(R.id.btnSeeFolders);
		button.setOnClickListener(this);
		button = (Button) findViewById(R.id.btnSongs);
		button.setOnClickListener(this);
		button = (Button) findViewById(R.id.btnArtists);
		button.setOnClickListener(this);
		button = (Button) findViewById(R.id.btnPlaying);
		button.setOnClickListener(this);
		button = (Button) findViewById(R.id.btnPlayLists);
		button.setOnClickListener(this);
	}

}
