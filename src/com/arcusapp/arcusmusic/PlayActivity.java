package com.arcusapp.arcusmusic;

import java.io.File;
import java.util.List;

import android.app.Activity;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

public class PlayActivity extends Activity implements OnClickListener{

	TextView txtTitle, txtFile, txtArtist, txtAlbum;
	Button btnPlayPause, btnPrev, btnNext, btnLogo5, btnSwichRandom, btnSwichRepeat, btnList;
	MediaPlayer mediaPlayer;
	
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
		
		btnPlayPause = (Button)findViewById(R.id.btnPlayPause);
		btnPlayPause.setOnClickListener(this);
		
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
        
        Uri uri = Uri.fromFile(file);
        mediaPlayer = MediaPlayer.create(getApplicationContext(), uri);
        mediaPlayer.start();

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.play, menu);
		return true;
	}

	@Override
	public void onClick(View v) {
		if(v.getId() == R.id.btnPlayPause){
			if(mediaPlayer.isPlaying() == false)
				mediaPlayer.start();
			else
				mediaPlayer.pause();
		}
	}
	
	@Override
    protected void onDestroy() {
		super.onDestroy();
        if(mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }
}
