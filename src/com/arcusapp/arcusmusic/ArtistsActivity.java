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
	/* Codigo para sacar los metadatos de las canciones:
	 * 
	 String selection = MediaStore.Audio.Media.IS_MUSIC + " != 0";
     String[] projection = { MediaStore.Audio.Media._ID,
             MediaStore.Audio.Media.ARTIST, MediaStore.Audio.Media.TITLE,
             MediaStore.Audio.Media.DATA, MediaStore.Audio.Media.DISPLAY_NAME,
             MediaStore.Audio.Media.DURATION};

     cursor = this.managedQuery(
             MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, projection, selection,null, null);

     private List<String> songs = new ArrayList<String>();

     while(cursor.moveToNext()){
         songs.add(cursor.getString(0) + "||" + cursor.getString(1) + "||" +
                 cursor.getString(2) + "||" + cursor.getString(3) + "||" +
                 cursor.getString(4) + "||" + cursor.getString(5));
     }
	 */


	@Override
	public void onClick(View v) {
		if(v.getId() == R.id.btnLogo3)
		{
			finish();
		}
		
	}

}
