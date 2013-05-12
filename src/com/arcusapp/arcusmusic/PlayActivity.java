package com.arcusapp.arcusmusic;

import java.io.File;
import java.util.List;
import java.util.Random;

import android.app.Activity;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

public class PlayActivity extends Activity implements OnClickListener, OnCompletionListener{

	TextView txtTitle, txtFile, txtArtist, txtAlbum;
	Button btnPlayPause, btnPrev, btnNext, btnLogo4, btnSwitchRandom, btnSwitchRepeat, btnList;
	MediaPlayer mediaPlayer;
	String[] defaultProjection;
	
	private SongsHandler sh;
	String actualID;
	List<String> temp_songs;
	List<String> repeatList;
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
		
		btnSwitchRandom = (Button)findViewById(R.id.btnSwitchRandom);
		btnSwitchRandom.setOnClickListener(this);
		btnSwitchRandom.setText("Random Off");
		random = false;
		
		btnSwitchRepeat = (Button)findViewById(R.id.btnSwitchRepeat);
		btnSwitchRepeat.setOnClickListener(this);
		btnSwitchRepeat.setText("Repeat Off");
		repeat = "off";
		
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
	        
	        repeatList = bundle.getStringArrayList("songs");
	        
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
		        mediaPlayer.setOnCompletionListener(this);
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
			prevSong();
			
		}
		else if(v.getId() == R.id.btnNextSong){
			nextSong();
		}
		else if(v.getId() == R.id.btnSwitchRandom){
			random = !random;
			if(random)
				this.btnSwitchRandom.setText("Random On");
			else
				this.btnSwitchRandom.setText("Random Off");
		}
		else if(v.getId() == R.id.btnSwitchRepeat){
			if(repeat == "off"){
				this.btnSwitchRepeat.setText("Repeat All");
				repeat = "all";
				repeatList = temp_songs;
			}
			else if(repeat == "all"){
				this.btnSwitchRepeat.setText("Repeat One");
				repeat = "one";
			}
			else if(repeat == "one"){
				this.btnSwitchRepeat.setText("Repeat Off");
				repeat = "off";
				repeatList = temp_songs;
			}
				
			
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
	
	private void nextSong(){
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

	private void prevSong(){
	
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

	@Override
	public void onCompletion(MediaPlayer arg0) {
		//pasar a la siguiente cancion, esto depende del estado del repeat y del random
		
		if(repeat == "one")
		{
			mediaPlayer.stop();
			mediaPlayer.start();
		}
		else
		{
			
			//tomo la posicion en la lista de la id actual:
			int index = repeatList.indexOf(actualID);
			
			//me fijo que no haya llegado al tope.
			if(index != repeatList.size())
			{
				//elijo un nuevo elemento, random o no.
				if(random){
					Random rnd = new Random();
					int newindex = index;
					while(newindex == index)
						newindex = rnd.nextInt(repeatList.size());
					
					actualID = repeatList.get(newindex);
					repeatList.remove(index);
				}
				else{
					actualID = repeatList.get(index+1);
					repeatList.remove(index);
				}
				
			}
			else{
				//vuelvo a repetir todo o paro el reproductor
				if(repeat == "all")
				{
					repeatList = temp_songs;
					if(random){
						Random rnd = new Random();
						int newindex = rnd.nextInt(repeatList.size());
						actualID = repeatList.get(newindex);
					}
					else
						actualID = repeatList.get(0);
				}
				else //repeat es off y llegamos al final de la lista
					mediaPlayer.stop();
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
}
