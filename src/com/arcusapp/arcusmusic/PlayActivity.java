package com.arcusapp.arcusmusic;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

public class PlayActivity extends Activity implements OnClickListener, MediaPlayerHandlerListener{

	TextView txtTitle, txtFile, txtArtist, txtAlbum;
	Button btnPlayPause, btnPrev, btnNext, btnLogo4, btnSwitchRandom, btnSwitchRepeat, btnList;
	
	private SongsHandler sh;
	private MediaPlayerHandler mph;
	
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
		
		btnSwitchRepeat = (Button)findViewById(R.id.btnSwitchRepeat);
		btnSwitchRepeat.setOnClickListener(this);
		btnSwitchRepeat.setText("Repeat Off");
		
		btnList = (Button)findViewById(R.id.btnActualPlayList);
		btnList.setOnClickListener(this);
		btnLogo4 = (Button)findViewById(R.id.btnLogo4);
		btnLogo4.setOnClickListener(this);
		
		 /*este try-catch es porque podemos entrar directamente al PlayActivity desde el MainActivity, y en ese caso el bundle.getString tira una excepcion, porque
		  * no encuentra el extra "id". En el futuro, siempre tiene que haber una cancion en el reproductor.
		  */
		try
		{
			//Recuperamos la informacion pasada en el intent
	        Bundle bundle = this.getIntent().getExtras();
	        String actualID = bundle.getString("id");
	        List<String> temp_songs = bundle.getStringArrayList("songs");
	        
	        if(actualID != null && actualID != "")
	        {
	        	if(actualID.equals("-1")){
	        		mph = new MediaPlayerHandler(this,this, temp_songs.get(0), temp_songs, sh);
	        	}
	        	else{
	        		mph = new MediaPlayerHandler(this,this, actualID, temp_songs, sh);
	        	}
	        	mph.TurnOnMediaPlayer();
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
			mph.PlayAndPause();
		}
		else if(v.getId() == R.id.btnPrevSong){
			mph.PreviousSong();
			
		}
		else if(v.getId() == R.id.btnNextSong){
			mph.NextSong();
		}
		else if(v.getId() == R.id.btnSwitchRandom){
			if(mph.ChangeRandom())
				this.btnSwitchRandom.setText("Random On");
			else
				this.btnSwitchRandom.setText("Random Off");
		}
		else if(v.getId() == R.id.btnSwitchRepeat){
			RepeatState rs = mph.ChangeRepeat();
			if(rs == RepeatState.Off){
				this.btnSwitchRepeat.setText("Repeat Off");
			}
			else if(rs == RepeatState.All){
				this.btnSwitchRepeat.setText("Repeat All");
			}
			else if(rs == RepeatState.One){
				this.btnSwitchRepeat.setText("Repeat One");
			}
				
			
		}
		else if(v.getId() == R.id.btnActualPlayList){
			Intent intent = new Intent();
			intent.setAction("com.arcusapp.arcusmusic.SONGSLIST_ACTIVITY");
			//Creamos la informacion a pasar entre actividades
	        Bundle b = new Bundle();
	        //cancion actual:
	        b.putString("id", mph.getActualSongID());
	        //todas las demas canciones:
	        b.putStringArrayList("songs", new ArrayList<String>(mph.getSongsList()));
	        
	        //Anadimos la informacion al intent
	        intent.putExtras(b);
			startActivity(intent);
		}
		else if(v.getId() == R.id.btnLogo4){
			finish();
		}
	}
	
	@Override
    protected void onDestroy() {
		super.onDestroy();
        mph.OnDestroy();
    }

	@Override
	public void onSongChanged() {
		txtTitle.setText(mph.getActualTitle());
		txtFile.setText(mph.getActualFileName());
		txtArtist.setText(mph.getActualArtist());
		txtAlbum.setText(mph.getActualAlbum());
	}
}
