package com.arcusapp.arcusmusic;

import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import android.app.ListActivity;
import android.os.Bundle;
import android.os.Environment;
import android.view.Menu;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

public class SongsActivity extends ListActivity implements View.OnClickListener{

	/** Variables iniciales */
	private Button btnLogo2;
	private File file;
	private List<String> myList;
	public boolean musicaFound;
    private GenericFilter myFilter;
    private String root_sd;
    List<File> allMp3Files;
	  
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_songs);
		
		//inicializo los controles
		btnLogo2 = (Button)findViewById(R.id.btnLogo2);
		btnLogo2.setOnClickListener(this);

		//inicializo el ListView	 
		myList = new ArrayList<String>();  
		myFilter = new GenericFilter();

		root_sd = Environment.getExternalStorageDirectory().toString();
	    file = new File( root_sd + "/Musica" ) ;
	    
	    //obtengo los archivos y directorios, y los ordeno
	    File list[] = file.listFiles(myFilter);
	    
	    if(list != null)
	    {
		    allMp3Files = new ArrayList<File>();
		    //allMp3Files.addAll(Arrays.asList(list));
		    SurfFolders(list);
		    
		    
		    //ordeno la lista por nombre
		    Collections.sort(allMp3Files, new SortFileName());
		    
		    //esto es para pasar todos los archivos a la lista final
		    int cant = allMp3Files.size();
		    for( int i=0; i< cant; i++)
		    {
		        myList.add(allMp3Files.get(i).getName() );
		    }
		    musicaFound = true;
		    
	    }
	    else
	    {
	    	myList.add("/Musica not Found !");
	    	musicaFound = false;
	    	
	    }
	    

	    setListAdapter(new ArrayAdapter<String>(this,
	            android.R.layout.simple_list_item_1, myList ));
	}
	
	private void SurfFolders(File list[])
	{
		if(list!=null)
		{
			for(int i = 0; i < list.length; i++)
			{
				if(list[i].isFile())
				{
					allMp3Files.add(list[i]);
				}
				else
				{
					File newList[] = list[i].listFiles(myFilter);
					SurfFolders(newList);
				}
			}
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.songs, menu);
		return true;
	}
	
	  protected void onListItemClick(ListView l, View v, int position, long id) 
	    {
	        super.onListItemClick(l, v, position, id);

	        if(musicaFound == true)
	        {
		        //reproducir cancion elegida y poner todas las canciones en una lista de reproduccion temporal
	        }
	    }
		    
	    @Override
		public void onBackPressed() 
	    {
	    	finish();
	    }

		@Override
		public void onClick(View v) {
			if(v.getId() == R.id.btnLogo2)
			{
				finish();
			}
		}
		
		//sorts based on the files name
		public class SortFileName implements Comparator<File> {
		    @Override
		    public int compare(File f1, File f2) {
		          return f1.getName().compareTo(f2.getName());
		    }
		}
		
		// inner class, generic extension filter
		public class GenericFilter implements FileFilter {

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
