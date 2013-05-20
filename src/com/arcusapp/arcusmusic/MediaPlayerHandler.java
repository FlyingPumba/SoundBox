package com.arcusapp.arcusmusic;

import java.io.File;
import java.util.List;
import java.util.Random;

import android.content.Context;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.net.Uri;
import android.provider.MediaStore;

public class MediaPlayerHandler implements OnCompletionListener {
	private String actualID;
	private List<String> songsList;
	private List<String> repeatList;
	
	private RepeatState repeatState;
	private boolean randomState;
	Random rnd = new Random();
	
	private String actualTitle;
	private File actualFile;
	private String actualArtist;
	private String actualAlbum;
	
	private SongsHandler sh;
	String[] defaultProjection = new String[]{MediaStore.Audio.Media.TITLE, MediaStore.Audio.Media.DATA, MediaStore.Audio.Media.ARTIST, MediaStore.Audio.Media.ALBUM};
    
    private MediaPlayer mediaPlayer;
    private MediaPlayerHandlerListener mphl;
    private Context context;
	
	public MediaPlayerHandler(Context context, MediaPlayerHandlerListener listener, String actualSong, List<String> songs, SongsHandler songsHandler){
		actualID = actualSong;
		songsList = songs;
		repeatList = songs;
		
		//the order here is important, the method setInfo() is getting the File for the song that will use the InitializeMediaPlayer(),
		// and also, the setInfo() method is firing the onSongChanged method from the listener
		this.context = context;
		this.mphl = listener;
		mediaPlayer = new MediaPlayer();
		sh = songsHandler;
	}
	
	// Primer metodo para prender el reproductor
	public void TurnOnMediaPlayer(){
		setInfo();
		InitializeMediaPlayer();
		mediaPlayer.start();
	}
	
	public String getActualSongID(){
		return actualID;
	}
	
	public String getActualFileName(){
		return actualFile.getName();
	}
	
	public String getActualArtist(){
		return actualArtist;
	}
	
	public String getActualAlbum(){
		return actualAlbum;
	}
	
	public String getActualTitle(){
		return actualTitle;
	}
	
	public boolean ChangeRandom(){
		randomState = !randomState;
		return randomState;
	}
	
	public RepeatState ChangeRepeat(){
		if(repeatState == RepeatState.Off){
			repeatState = RepeatState.All;
		}
		else if(repeatState == RepeatState.All){
			repeatState = RepeatState.One;
		}
		else if(repeatState == RepeatState.One){
			repeatState = RepeatState.Off;
		}
		return repeatState;
	}
	
	public void PlayAndPause(){
		//prendemos la musica !
		if(!mediaPlayer.isPlaying()){
		   // mediaPlayer.setOnCompletionListener(this);
		    mediaPlayer.start();
		}
		else{
			mediaPlayer.pause();
		}
	}
	public void NextSong(){
		if(songsList.size() > 1)
		{
			//tomo la posicion en la lista de la id actual:
			int index = songsList.indexOf(actualID);
			//apago el mediaPlayer
			mediaPlayer.stop();
			
			if(randomState){
				int newindex = index;
				while(newindex == index)
					newindex = rnd.nextInt(songsList.size());
				
				actualID = songsList.get(newindex);
			}
			else{
				//le sumo uno;
				if(index+1 != songsList.size()){
					actualID = songsList.get(index+1);
				}
				else{
					actualID = songsList.get(0);
				}
			}
			
			setInfo();
	        InitializeMediaPlayer();
	        mediaPlayer.start();	        
		}	
	}

	public void PreviousSong(){
	
		//tomo la posicion en la lista de la id actual:
		int index = songsList.indexOf(actualID);
		//apago el mediaPlayer
		mediaPlayer.stop();

		if(randomState){
			Random rnd = new Random();
			int newindex = index;
			while(newindex == index)
				newindex = rnd.nextInt(songsList.size());
			
			actualID = songsList.get(newindex);
		}
		else{
			//le resto uno;
			if(index != 0){
				actualID = songsList.get(index-1);
			}
			else{
				actualID = songsList.get(songsList.size()-1);
			}
		}
		
		setInfo();
        InitializeMediaPlayer();
        mediaPlayer.start();
	}
	
	public void OnDestroy(){
		if(mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer = null;
        }
	}
	
	private void setInfo(){
		// Recuperamos cierta informacion para mostrar en el reproductor
	    List<String> infoSong = sh.getInformationFromSong(actualID, defaultProjection);
	    //Construimos el mensaje a mostrar
	    actualFile = new File(infoSong.get(1));
	    actualTitle =infoSong.get(0);
	    actualArtist = infoSong.get(2);
	    actualAlbum = infoSong.get(3);
	    
	    mphl.onSongChanged();
	}

	private void InitializeMediaPlayer(){
		 Uri uri = Uri.fromFile(actualFile);
	     mediaPlayer = MediaPlayer.create(context, uri);
	     mediaPlayer.setOnCompletionListener(this);
	}

	@Override
	public void onCompletion(MediaPlayer arg0) {
		//pasar a la siguiente cancion, esto depende del estado del repeat y del random
		if(repeatState == RepeatState.One)
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
				if(randomState){
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
				if(repeatState == RepeatState.All)
				{
					repeatList = songsList;
					if(randomState){
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
			
			setInfo();
	        InitializeMediaPlayer();
	        mediaPlayer.start();
		}
		
	}
		
}
