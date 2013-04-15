package com.arcusapp.arcusmusic;

import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.content.CursorLoader;

public class SongsHandler {
	
    private OnlyDirsFilter myFilter;
    public String root_sd;
    public File musicDirectory;
    
    
	private Cursor musiccursor;
	private List<String> songs;
    private Context _context;
    
    public SongsHandler(Context cont)
    {
    	//constructor
		myFilter = new OnlyDirsFilter(); // filtro para solo carpetas

		root_sd = Environment.getExternalStorageDirectory().toString(); //raiz del sd
		musicDirectory = new File( root_sd + "/Musica" ) ;
		_context = cont;
		
		checkIfMusicDirectoryExist();
    }
    
    public List<String> getSongsInAFolder(File dir, boolean withDirs, boolean mp3Format)
    {
    	songs = new ArrayList<String>();
    	String folder = dir.getPath();
    	if(withDirs)
    	{
    		File list[] = dir.listFiles(myFilter);
        	if(list != null)
    	    {
    	    	//inicializo las variables
        		List<File> Dirs = new ArrayList<File>();
    		    
    		    // cargo los archivos en MP3Files
    		    for(int i = 0; i < list.length; i++)
    			{
    		    	Dirs.add(list[i]);
    			}
    		    
    		    //ordeno los archivos por nombre
    		    Collections.sort(Dirs, new SortFileName());
    		    
    		    //paso todos los archivos a la lista final
    		    int cant = Dirs.size();
    		    for( int i=0; i< cant; i++)
    		    {
    		    	songs.add(Dirs.get(i).getName() );
    		    }
    		    
    	    }
    	}
    	
    	CursorLoader cl;
    	String selection = MediaStore.Audio.Media.IS_MUSIC + " != 0 AND " +
    					"SUBSTR("+MediaStore.Audio.Media.DATA+",0 , LENGTH('"+folder+"')+1) = '"+folder+"' AND " +
    			 		"SUBSTR("+MediaStore.Audio.Media.DATA+",LENGTH('"+folder+"')+1, 200) LIKE '/%.mp3' AND " +
    			 		"SUBSTR("+MediaStore.Audio.Media.DATA+",LENGTH('"+folder+"')+1, 200) NOT LIKE '/%/%.mp3'";
    	
    	if(!mp3Format)
    	{           
    		String[] projection = { MediaStore.Audio.Media.TITLE};
    		cl = new CursorLoader(_context, MediaStore.Audio.Media.getContentUriForPath(folder), projection, selection, null, MediaStore.Audio.Media.TITLE);
   	     	musiccursor = cl.loadInBackground();
    	}
    	else
    	{
    		String[] projection = { MediaStore.Audio.Media.DISPLAY_NAME, MediaStore.Audio.Media.DATA};
    		cl = new CursorLoader(_context, MediaStore.Audio.Media.getContentUriForPath(folder), projection, selection, null, MediaStore.Audio.Media.DISPLAY_NAME);
    		musiccursor = cl.loadInBackground();
    	}
    	String data;
	     while(musiccursor.moveToNext()){
	    	 data =  musiccursor.getString(1);

	    	songs.add(musiccursor.getString(0));
	     }
	     return songs;
    }
    
    public List<String> getAllSongs(boolean mp3Format)
    {
    	/*String[] projection = { MediaStore.Audio.Media._ID,
  	             MediaStore.Audio.Media.ARTIST, MediaStore.Audio.Media.TITLE,
  	             MediaStore.Audio.Media.DATA, MediaStore.Audio.Media.DISPLAY_NAME,
  	             MediaStore.Audio.Media.DURATION};*/
    	CursorLoader cl;
    	String selection = MediaStore.Audio.Media.IS_MUSIC + " != 0 ";
    	if(!mp3Format)
    	{
    		String[] projection = { MediaStore.Audio.Media.TITLE};
    		cl = new CursorLoader(_context, MediaStore.Audio.Media.getContentUriForPath(musicDirectory.getPath()), projection, selection, null, MediaStore.Audio.Media.TITLE);
   	     	musiccursor = cl.loadInBackground();
    	}
    	else
    	{
    		String[] projection = { MediaStore.Audio.Media.DISPLAY_NAME};
    		cl = new CursorLoader(_context, MediaStore.Audio.Media.getContentUriForPath(musicDirectory.getPath()), projection, selection, null, MediaStore.Audio.Media.DISPLAY_NAME);
    		musiccursor = cl.loadInBackground();
    	}

	     songs = new ArrayList<String>();

	     while(musiccursor.moveToNext()){
	         songs.add(musiccursor.getString(0));
	     }
	     
	     return songs;
    }
    
    private void checkIfMusicDirectoryExist() 
    {
        if(!musicDirectory.exists())
        {
        	musicDirectory.mkdirs();
        }    
    }
    
    //sorts based on a file or folder. folders will be listed first
  	private class SortFolder implements Comparator<File> {
  	    @Override
  	    public int compare(File f1, File f2) {
  	         if (f1.isDirectory() == f2.isDirectory())
  	            return 0;
  	         else if (f1.isDirectory() && !f2.isDirectory())
  	            return -1;
  	         else
  	            return 1;
  	          }
  	}
    
    //sorts based on the files name
	private class SortFileName implements Comparator<File> {
	    @Override
	    public int compare(File f1, File f2) {
	          return f1.getName().compareTo(f2.getName());
	    }
	}
	
	// inner class, generic extension filter
		private class OnlyDirsFilter implements FileFilter {

			@Override
			public boolean accept(File pathname) {				
				return pathname.isDirectory();
			}

		}
}
