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
	private SongsHandler sh;
	  
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_songs);
		
		//inicializo los controles
		btnLogo2 = (Button)findViewById(R.id.btnLogo2);
		btnLogo2.setOnClickListener(this);

		sh = new SongsHandler();
	    
	    setListAdapter(new ArrayAdapter<String>(this,
	            android.R.layout.simple_list_item_1, sh.getAllSongs() ));
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

	     //reproducir cancion elegida y poner todas las canciones en una lista de reproduccion temporal
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
}
