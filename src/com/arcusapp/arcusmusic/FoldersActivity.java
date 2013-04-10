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
import android.view.Menu;
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
	private SongsHandler sh;
	
	private File actualDir;
	  
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
		
		sh = new SongsHandler();
		actualDir = sh.musicDirectory;
		txtDir.setText("Musica/");
		
		setListAdapter(new ArrayAdapter<String>(this,
	            android.R.layout.simple_list_item_1, sh.getSongsInAFolder(sh.musicDirectory, true) ));
	    
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.folders, menu);
		return true;
	}
	
    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) 
    {
        super.onListItemClick(l, v, position, id);
        TextView txt = (TextView)v;

        File temp_file = new File(actualDir, txt.getText().toString());  

        if( !temp_file.isFile())        
        {
        	actualDir = temp_file;
            txtDir.setText(actualDir.toString().split(sh.root_sd)[1]);
			
            setListAdapter(new ArrayAdapter<String>(this,
                    android.R.layout.simple_list_item_1, sh.getSongsInAFolder(actualDir, true) ));

        }
        else
        {
        	//reproducir cancion y agregar canciones de la carpeta a una lista de reproduccion temporal
        }
    }
	    
    @Override
	public void onBackPressed() 
    {
		if(!actualDir.equals(sh.musicDirectory))
		{
			actualDir = actualDir.getParentFile();
	        txtDir.setText(actualDir.toString().split(sh.root_sd)[1]);
			
	        setListAdapter(new ArrayAdapter<String>(this,
	                android.R.layout.simple_list_item_1, sh.getSongsInAFolder(actualDir, true) ));
		}
		else
		{
			finish();
		}
    }
	    


	@Override
	public void onClick(View v) 
	{
		if(v.getId() == R.id.btnLogo)
		{
			finish();
		}
		else if(v.getId() == R.id.btnPlayFolder)
		{
			//reproducir carpeta actual
		}
	}
}

