package com.arcusapp.arcusmusic;

import java.io.File;
import java.util.List;

import android.app.Activity;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.Menu;
import android.widget.TextView;

public class PlayActivity extends Activity {

	TextView txtTitle, txtFile, txtArtist, txtAlbum;
	private SongsHandler sh;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_play);
		
		sh = new SongsHandler(this);
		
		txtTitle = (TextView)findViewById(R.id.txtActualSongTitle);
		txtFile = (TextView)findViewById(R.id.txtActualSongFile);
		txtArtist = (TextView)findViewById(R.id.txtActualSongArtist);
		txtAlbum = (TextView)findViewById(R.id.txtActualSongAlbum);
		
		 //Recuperamos la información pasada en el intent
        Bundle bundle = this.getIntent().getExtras();
        String actualID = bundle.getString("id");
        
        String[] projection = new String[]{MediaStore.Audio.Media.TITLE, MediaStore.Audio.Media.DATA, MediaStore.Audio.Media.ARTIST, MediaStore.Audio.Media.ALBUM};
        
        List<String> infoSong = sh.getInformationFromSong(actualID, projection);
        //Construimos el mensaje a mostrar
        File file = new File(infoSong.get(1));
        txtTitle.setText(infoSong.get(0));
        txtFile.setText("Filename: "+file.getName());
        txtArtist.setText("Artist: "+infoSong.get(2));
        txtAlbum.setText("Album: "+infoSong.get(3));

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.play, menu);
		return true;
	}

}
