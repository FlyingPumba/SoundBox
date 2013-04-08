package com.arcusapp.arcusmusic;

import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import com.arcusapp.arcusmusic.R;

import android.app.ListActivity;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

public class FoldersActivity extends ListActivity implements View.OnClickListener
{

	/** Variables iniciales */
	private TextView txtDir;
	private Button btnLogo, btnPlayFolder;
	private File file;
	private List<String> myList;
	public boolean musicaFound;
    private GenericFilter myFilter;
    private String root_sd;
	  
	@Override
    public void onCreate(Bundle savedInstanceState) 
    {
        super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_folders);
		
		//inicializo los controles
		txtDir = (TextView)findViewById(R.id.txtDir);
		btnLogo = (Button)findViewById(R.id.btnLogo);
		btnLogo.setOnClickListener(this);
		btnPlayFolder = (Button)findViewById(R.id.btnPlayFolder);
		btnPlayFolder.setOnClickListener(this);

		//inicializo el ListView	 
		myList = new ArrayList<String>();  
		myFilter = new GenericFilter();
		
		//verifico que exista la carpeta Musica, en caso contrario es creada
		this.checkIfDirectoryExist();

		root_sd = Environment.getExternalStorageDirectory().toString();
	    file = new File( root_sd + "/Musica" ) ;
	    
	    //obtengo los archivos y directorios, y los ordeno
	    File list[] = file.listFiles(myFilter);
	    
	    if(list != null)
	    {
		    List<File> directoryListing = new ArrayList<File>();
		    directoryListing.addAll(Arrays.asList(list));
		    Collections.sort(directoryListing, new SortFileName());
		    Collections.sort(directoryListing, new SortFolder());
	    
		    for( int i=0; i< list.length; i++)
		    {
		        myList.add( directoryListing.get(i).getName() );
		    }
		    musicaFound = true;
		    
		  //inicializo el TextView
		    txtDir.setText("Musica/");
		    
	    }
	    else
	    {
	    	myList.add("/Musica not Found !");
	    	musicaFound = false;
	    	
	    	//inicializo el TextView
	    	txtDir.setText("");
	    }
	    

	    setListAdapter(new ArrayAdapter<String>(this,
	            android.R.layout.simple_list_item_1, myList ));
	    
	}
	
    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) 
    {
        super.onListItemClick(l, v, position, id);

        if(musicaFound == true)
        {
	        File temp_file = new File( file, myList.get( position ) );  

	        if( !temp_file.isFile())        
	        {
	            file = new File( file, myList.get( position ));
	            myFilter = new GenericFilter();
	            File list[] = file.listFiles(myFilter);
	            if(list != null)
	            {
		            List<File> directoryListing = new ArrayList<File>();
				    directoryListing.addAll(Arrays.asList(list));
				    Collections.sort(directoryListing, new SortFileName());
				    Collections.sort(directoryListing, new SortFolder());
			    
				    myList.clear();
				    
				    for( int i=0; i< list.length; i++)
				    {
				        myList.add( directoryListing.get(i).getName() );
				    }
	            }

	            //Toast.makeText(getApplicationContext(), file.toString(), Toast.LENGTH_LONG).show(); 
	            txtDir.setText(file.toString().split(root_sd)[1]);
				
	            setListAdapter(new ArrayAdapter<String>(this,
	                    android.R.layout.simple_list_item_1, myList ));

	        }
        }
    }
	    
    @Override
	public void onBackPressed() 
    {
    	if(musicaFound == true)
    	{
    		String actual = file.toString();
    		String top = Environment.getExternalStorageDirectory() + "/Musica";
    		
    		
    		if(!actual.equals(top))
    		{
		        String parent = file.getParent().toString();
		        
		        file = new File( parent ) ;         
		        File list[] = file.listFiles(myFilter);
		
		        List<File> directoryListing = new ArrayList<File>();
			    directoryListing.addAll(Arrays.asList(list));
			    Collections.sort(directoryListing, new SortFileName());
			    Collections.sort(directoryListing, new SortFolder());
		    
			    myList.clear();
			    
			    for( int i=0; i< list.length; i++)
			    {
			        myList.add( directoryListing.get(i).getName() );
			    }
		        
		        //Toast.makeText(getApplicationContext(), parent, Toast.LENGTH_LONG).show(); 
		        txtDir.setText(parent.split(root_sd)[1]);
				
		        setListAdapter(new ArrayAdapter<String>(this,
		                android.R.layout.simple_list_item_1, myList ));
    		}
    		else
    		{
    			finish();
    		}
	    }
    }
	    
	private void checkIfDirectoryExist() {

	        File folder = new File(root_sd + "/Musica");
	        if(!folder.exists())
	        {
	            folder.mkdirs();
	        }    
	}

	@Override
	public void onClick(View v) {
		if(v.getId() == R.id.btnLogo)
		{
			finish();
		}
		else if(v.getId() == R.id.btnPlayFolder)
		{
			//reproducir carpeta actual
		}
		
	}
	
	//sorts based on a file or folder. folders will be listed first
	public class SortFolder implements Comparator<File> {
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

