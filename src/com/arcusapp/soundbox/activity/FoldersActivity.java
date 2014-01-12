/*
 * SoundBox - Android Music Player
 * Copyright (C) 2013 Iván Arcuschin Moreno
 *
 * This file is part of SoundBox.
 *
 * SoundBox is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 2 of the License, or
 * (at your option) any later version.
 *
 * SoundBox is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with SoundBox.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.arcusapp.soundbox.activity;

import android.app.Activity;
import android.database.DataSetObserver;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;

import com.arcusapp.soundbox.R;
import com.arcusapp.soundbox.adapter.FoldersActivityAdapter;

public class FoldersActivity extends Activity implements View.OnClickListener {

    private TextView txtDir;
    private ListView myListView;
    private FoldersActivityAdapter myAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_folders);

        txtDir = (TextView) findViewById(R.id.txtDir);
        myAdapter = new FoldersActivityAdapter(this, txtDir);
        myAdapter.registerDataSetObserver(new DataSetObserver() {
            @Override
            public void onChanged() {
                super.onChanged();

                if (myAdapter.getCount() > 0)
                    myListView.setSelection(0);
            }
        });

        myListView = (ListView) findViewById(R.id.foldersActivityList);
        myListView.setAdapter(myAdapter);
        myListView.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View view, int position, long arg3) {
                myAdapter.onItemClick(position);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.folders, menu);
        return true;
    }

    @Override
    public void onBackPressed() {
        myAdapter.backPressed();
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btnHomeFoldersActivity) {
            finish();
        } else if (v.getId() == R.id.btnPlayFolder) {
            myAdapter.playCurrentDirectory();
        }
    }
}