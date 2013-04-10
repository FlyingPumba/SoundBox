package com.arcusapp.arcusmusic;

import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import android.os.Environment;

public class SongsHandler {
	
	private List<String> myList;
    private GenericFilter myFilter;
    public String root_sd;
    public File musicDirectory;
    private List<File> Mp3Files;
    
    public SongsHandler()
    {
    	//constructor
		myFilter = new GenericFilter(); // filtro para solo carpetas y *.mp3

		root_sd = Environment.getExternalStorageDirectory().toString(); //raiz del sd
		musicDirectory = new File( root_sd + "/Musica" ) ;
		
		checkIfMusicDirectoryExist();
    }
    
    public List<String> getSongsInAFolder(File dir, boolean withDirs)
    {
    	File list[] = dir.listFiles(myFilter);
    	myList = new ArrayList<String>(); 
    	if(list != null)
	    {
	    	//inicializo las variables
		    Mp3Files = new ArrayList<File>();
		    
		    // cargo los archivos en MP3Files
		    for(int i = 0; i < list.length; i++)
			{
				if(list[i].isFile())
				{
					Mp3Files.add(list[i]);
				}
				else
				{
					if(withDirs)
						Mp3Files.add(list[i]);
				}
			}
		    
		    
		    //ordeno los archivos por nombre
		    Collections.sort(Mp3Files, new SortFileName());
		    if(withDirs)
		    	Collections.sort(Mp3Files, new SortFolder());
		    
		    //paso todos los archivos a la lista final
		    int cant = Mp3Files.size();
		    for( int i=0; i< cant; i++)
		    {
		        myList.add(Mp3Files.get(i).getName() );
		    }
		    
	    }
	    return myList;
    }
    
    public List<String> getAllSongs()
    {
    	//obtengo los archivos
	    File list[] = musicDirectory.listFiles(myFilter);
	    
	    myList = new ArrayList<String>(); 
	    if(list != null)
	    {
	    	//inicializo las variables
		    Mp3Files = new ArrayList<File>();
		    
		    // cargo los archivos en MP3Files
		    SurfFolders(list);
		    
		    
		    //ordeno los archivos por nombre
		    Collections.sort(Mp3Files, new SortFileName());
		    
		    //paso todos los archivos a la lista final
		    int cant = Mp3Files.size();
		    for( int i=0; i< cant; i++)
		    {
		        myList.add(Mp3Files.get(i).getName() );
		    }
		    
	    }
	    return myList;
    }
    
    private void checkIfMusicDirectoryExist() 
    {
        if(!musicDirectory.exists())
        {
        	musicDirectory.mkdirs();
        }    
    }
    
    private void SurfFolders(File list[])
	{
		if(list!=null)
		{
			for(int i = 0; i < list.length; i++)
			{
				if(list[i].isFile())
				{
					Mp3Files.add(list[i]);
				}
				else
				{
					File newList[] = list[i].listFiles(myFilter);
					SurfFolders(newList);
				}
			}
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
	private class GenericFilter implements FileFilter {

		@Override
		public boolean accept(File pathname) {
			if(pathname.isDirectory())
             {
                return true;
             }
             else
             {
                 return pathname.getName().endsWith(".mp3");
             }
		}

	}
}
