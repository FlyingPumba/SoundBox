package com.arcusapp.arcusmusic;

import java.io.File;
import java.util.List;
import java.util.Random;

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
	Button btnPlayPause, btnPrev, btnNext, btnLogo4, btnSwichRandom, btnSwichRepeat, btnList;
	MediaPlayer mediaPlayer;
	String[] defaultProjection;
	
	private SongsHandler sh;
	String actualID;
	List<String> temp_songs;
	boolean random;
	String repeat;
	
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
		btnPrev = (Button)findViewById(R.id.btnPrevSong);
		btnPrev.setOnClickListener(this);
		btnNext = (Button)findViewById(R.id.btnNextSong);
		btnNext.setOnClickListener(this);
		btnSwichRandom = (Button)findViewById(R.id.btnSwichRandom);
		btnSwichRandom.setOnClickListener(this);
		btnSwichRepeat = (Button)findViewById(R.id.btnSwichRepeat);
		btnSwichRepeat.setOnClickListener(this);
		btnList = (Button)findViewById(R.id.btnActualPlayList);
		btnList.setOnClickListener(this);
		btnLogo4 = (Button)findViewById(R.id.btnLogo4);
		btnLogo4.setOnClickListener(this);
		
		 /*este try-catch es porque podemos entrar directamente al PlayActivity desde el MainActivity, y en ese caso el bundle.getString tira una excepcion, porque
		  * no encuentra el extra "id". En el futuro, siempre tiene que haber una cancion en el reproductor.
		  */
		try
		{
			//Recuperamos la informaci�n pasada en el intent
	        Bundle bundle = this.getIntent().getExtras();
	        actualID = bundle.getString("id");
	        temp_songs = bundle.getStringArrayList("songs");
	        
	        if(actualID != null && actualID != "")
	        {
		        defaultProjection = new String[]{MediaStore.Audio.Media.TITLE, MediaStore.Audio.Media.DATA, MediaStore.Audio.Media.ARTIST, MediaStore.Audio.Media.ALBUM};
		        
		        List<String> infoSong = sh.getInformationFromSong(actualID, defaultProjection);
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
		}
		catch(Exception ex)
		{
			
		}

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
		else if(v.getId() == R.id.btnPrevSong){
			if(temp_songs.size() > 1)
			{
				//tomo la posicion en la lista de la id actual:
				int index = temp_songs.indexOf(actualID);
				//apago el mediaPlayer
				mediaPlayer.stop();
				
				if(random){
					Random rnd = new Random();
					int newindex = index;
					while(newindex == index)
						newindex = rnd.nextInt(temp_songs.size());
					
					actualID = temp_songs.get(newindex);
				}
				else{
					//le resto uno;
					if(index != 0){
						actualID = temp_songs.get(index-1);
					}
					else{
						actualID = temp_songs.get(temp_songs.size());
					}
				}
				
				//obtengo la nueva informacion
				List<String> infoSong = sh.getInformationFromSong(actualID, defaultProjection);
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
			
		}
		else if(v.getId() == R.id.btnNextSong){
			if(temp_songs.size() > 1)
			{
				//tomo la posicion en la lista de la id actual:
				int index = temp_songs.indexOf(actualID);
				//apago el mediaPlayer
				mediaPlayer.stop();
				
				if(random){
					Random rnd = new Random();
					int newindex = index;
					while(newindex == index)
						newindex = rnd.nextInt(temp_songs.size());
					
					actualID = temp_songs.get(newindex);
				}
				else{
					//le sumo uno;
					if(index != temp_songs.size()){
						actualID = temp_songs.get(index+1);
					}
					else{
						actualID = temp_songs.get(0);
					}
				}
				
				//obtengo la nueva informacion
				List<String> infoSong = sh.getInformationFromSong(actualID, defaultProjection);
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
		}
		else if(v.getId() == R.id.btnSwichRandom){
			random = !random;
		}
		else if(v.getId() == R.id.btnSwichRepeat){
			
		}
		else if(v.getId() == R.id.btnActualPlayList){
			
		}
		else if(v.getId() == R.id.btnLogo4){
			
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
