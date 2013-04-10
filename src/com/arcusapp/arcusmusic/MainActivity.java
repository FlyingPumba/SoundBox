package com.arcusapp.arcusmusic;

import com.arcusapp.arcusmusic.R;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.Button;

public class MainActivity extends Activity implements View.OnClickListener {

	Intent testActivityIntent;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		//asigno el listener a los botones
		Button button = (Button)findViewById(R.id.btnSeeFolders);
        button.setOnClickListener(this);
        button = (Button)findViewById(R.id.btnSongs);
        button.setOnClickListener(this);
        button = (Button)findViewById(R.id.btnArtists);
        button.setOnClickListener(this);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public void onClick(View v) {
		if(v.getId() == R.id.btnPlaying){
			//En Reproduccion
		}
		else if(v.getId() == R.id.btnSeeFolders)
		{
			testActivityIntent = new Intent();
			testActivityIntent.setAction("com.arcusapp.arcusmusic.FOLDERS_ACTIVITY");
			startActivity(testActivityIntent);
		}
		else if(v.getId() == R.id.btnPlayLists){
			//Listas de Reproduccion
		}
		else if(v.getId() == R.id.btnSongs){
			testActivityIntent = new Intent();
			testActivityIntent.setAction("com.arcusapp.arcusmusic.SONGS_ACTIVITY");
			startActivity(testActivityIntent);
		}
		else if(v.getId() == R.id.btnArtists){
			testActivityIntent = new Intent();
			testActivityIntent.setAction("com.arcusapp.arcusmusic.ARTISTS_ACTIVITY");
			startActivity(testActivityIntent);
			
		}
		
		
	}

}
