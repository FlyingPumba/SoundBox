package com.arcusapp.soundbox;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import com.arcusapp.soundbox.R;

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

public class FoldersActivity extends ListActivity implements
		View.OnClickListener {

	/** Variables iniciales */
	private TextView txtDir;
	private Button btnLogo, btnPlayFolder;
	private SongsHandler sh;
	private Intent PlayActivityIntent;

	private File actualDir;
	private List<SongEntry> Songs;
	String projection;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_folders);

		// inicializo los controles
		txtDir = (TextView) findViewById(R.id.txtDir);
		btnLogo = (Button) findViewById(R.id.btnLogo);
		btnLogo.setOnClickListener(this);
		btnPlayFolder = (Button) findViewById(R.id.btnPlayFolder);
		btnPlayFolder.setOnClickListener(this);

		sh = new SongsHandler(this);
		actualDir = sh.musicDirectory;
		txtDir.setText("Musica/");

		projection = MediaStore.Audio.Media.TITLE;
		Songs = sh.getSongsInAFolder(actualDir, true, projection);

		setListAdapter(new ArrayAdapter<String>(this,
				android.R.layout.simple_list_item_1,
				SongEntry.getValuesList(Songs)));

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.folders, menu);
		return true;
	}

	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		super.onListItemClick(l, v, position, id);

		if (Songs.get(position).getKey() == "-1") {
			TextView txt = (TextView) v;
			File temp_file = new File(actualDir, txt.getText().toString());

			if (!temp_file.isFile()) {
				actualDir = temp_file;
				txtDir.setText(actualDir.toString().split(sh.root_sd)[1]);

				Songs = sh.getSongsInAFolder(actualDir, true, projection);

				setListAdapter(new ArrayAdapter<String>(this,
						android.R.layout.simple_list_item_1,
						SongEntry.getValuesList(Songs)));
			}

		}
		else {
			// reproducir cancion y agregar canciones de la carpeta a una lista
			// de reproduccion temporal
			// Creamos el Intent
			PlayActivityIntent = new Intent(this, PlayActivity.class);

			// Creamos la informaci�n a pasar entre actividades
			Bundle b = new Bundle();
			// cancion actual:
			b.putString("id", Songs.get(position).getKey().toString());
			// demas canciones de ESTA carpeta
			b.putStringArrayList("songs",
					new ArrayList<String>(SongEntry.getKeysList(Songs)));
			// A�adimos la informaci�n al intent
			PlayActivityIntent.putExtras(b);

			// Iniciamos la nueva actividad
			startActivity(PlayActivityIntent);

		}
	}

	@Override
	public void onBackPressed() {
		if (!actualDir.equals(sh.musicDirectory)) {
			actualDir = actualDir.getParentFile();
			txtDir.setText(actualDir.toString().split(sh.root_sd)[1]);

			Songs = sh.getSongsInAFolder(actualDir, true, projection);

			setListAdapter(new ArrayAdapter<String>(this,
					android.R.layout.simple_list_item_1,
					SongEntry.getValuesList(Songs)));
		}
		else {
			finish();
		}
	}

	@Override
	public void onClick(View v) {
		if (v.getId() == R.id.btnLogo) {
			finish();
		}
		else if (v.getId() == R.id.btnPlayFolder) {
			// reproducir carpeta actual
		}
	}
}
