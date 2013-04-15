package com.arcusapp.arcusmusic;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.Button;

public class ArtistsActivity extends Activity implements View.OnClickListener {

	private Button btnLogo3;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_artists);
		
		btnLogo3 = (Button)findViewById(R.id.btnLogo3);
		btnLogo3.setOnClickListener(this);
		
		
	}


	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.artists, menu);
		return true;
	}

	@Override
	public void onClick(View v) {
		if(v.getId() == R.id.btnLogo3)
		{
			finish();
		}
		
	}

}
