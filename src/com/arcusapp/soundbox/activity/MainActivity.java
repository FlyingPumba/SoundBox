package com.arcusapp.soundbox.activity;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;

import com.arcusapp.soundbox.R;
import com.arcusapp.soundbox.SoundBoxApplication;
import com.arcusapp.soundbox.data.MediaProvider;
import com.arcusapp.soundbox.model.BundleExtra;

public class MainActivity extends Activity implements View.OnClickListener {

    Intent activityIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SoundBoxApplication.setInitialContext(getApplicationContext());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btnPlaying) {
            activityIntent = new Intent();
            activityIntent.setAction(SoundBoxApplication.ACTION_PLAY_ACTIVITY);
            startActivity(activityIntent);
        }
        else if (v.getId() == R.id.btnSeeFolders) {
            activityIntent = new Intent();
            activityIntent.setAction(SoundBoxApplication.ACTION_FOLDERS_ACTIVITY);
            startActivity(activityIntent);
        }
        else if (v.getId() == R.id.btnPlayLists) {
            activityIntent = new Intent();
            activityIntent.setAction(SoundBoxApplication.ACTION_PLAYLISTS_ACTIVITY);
            startActivity(activityIntent);
        }
        else if (v.getId() == R.id.btnSongs) {
            activityIntent = new Intent();
            activityIntent.setAction(SoundBoxApplication.ACTION_SONGLIST_ACTIVITY);

            MediaProvider media = new MediaProvider();
            Bundle bundle = new Bundle();
            bundle.putStringArrayList(BundleExtra.SONGS_ID_LIST, new ArrayList<String>(media.getAllSongs()));
            activityIntent.putExtras(bundle);

            startActivity(activityIntent);
        }
        else if (v.getId() == R.id.btnArtists) {
            activityIntent = new Intent();
            activityIntent.setAction(SoundBoxApplication.ACTION_ARTISTS_ACTIVITY);
            startActivity(activityIntent);
        }
    }
}