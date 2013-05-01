package com.arcusapp.arcusmusic;

import java.util.List;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.Menu;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

public class SongsActivity extends ListActivity implements View.OnClickListener{

	/** Variables iniciales */
	private Button btnLogo2;
	private SongsHandler sh;
	private Intent PlayActivityIntent;
	private List<SongEntry> Songs;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_songs);
		
		//inicializo los controles
		btnLogo2 = (Button)findViewById(R.id.btnLogo2);
		btnLogo2.setOnClickListener(this);

		sh = new SongsHandler(this);
	    
	    String projection = MediaStore.Audio.Media.TITLE;
	    Songs = sh.getAllSongsWithDisplay(projection);
	    
	    setListAdapter(new ArrayAdapter<String>(this,
	            android.R.layout.simple_list_item_1, SongEntry.getStringList(Songs)));
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
        //Creamos el Intent
        PlayActivityIntent = new Intent(this, PlayActivity.class);

        //Creamos la información a pasar entre actividades
        Bundle b = new Bundle();
        TextView textView = (TextView)v;
        b.putString("id", Songs.get(position).getKey().toString());
         
        //Añadimos la información al intent
        PlayActivityIntent.putExtras(b);

        //Iniciamos la nueva actividad
        startActivity(PlayActivityIntent);
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
