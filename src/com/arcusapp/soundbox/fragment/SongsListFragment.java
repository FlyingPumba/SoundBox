/*
 * SoundBox - Android Music Player
 * Copyright (C) 2013  Iván Arcuschin Moreno
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

package com.arcusapp.soundbox.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.arcusapp.soundbox.R;
import com.arcusapp.soundbox.SoundBoxApplication;
import com.arcusapp.soundbox.adapter.SongsListActivityAdapter;
import com.arcusapp.soundbox.model.BundleExtra;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class SongsListFragment extends Fragment {

    public static String ADD_PLAYALLRANDOM_BUTTON = "addRandomButton";

    ListView myListView;
    private SongsListActivityAdapter myAdapter;
    private List<String> songsIDs;
    private String focusedElementID = BundleExtra.DefaultValues.DEFAULT_ID;
    private boolean addRandomButton = false;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_songs_list, container, false);

        myListView = (ListView) rootView.findViewById(R.id.songslistActivityList);

        // try to get the bundle from the arguments
        Bundle bundleArg = getArguments();
        Bundle bundleInt = getActivity().getIntent().getExtras();
        try {
            if (bundleInt != null) {
                focusedElementID = bundleInt.getString(BundleExtra.CURRENT_ID, BundleExtra.DefaultValues.DEFAULT_ID);
                songsIDs = bundleInt.getStringArrayList(BundleExtra.SONGS_ID_LIST);
                addRandomButton = bundleInt.getBoolean(ADD_PLAYALLRANDOM_BUTTON, false);
            } else {
                focusedElementID = bundleArg.getString(BundleExtra.CURRENT_ID, BundleExtra.DefaultValues.DEFAULT_ID);
                songsIDs = bundleArg.getStringArrayList(BundleExtra.SONGS_ID_LIST);
                addRandomButton = bundleArg.getBoolean(ADD_PLAYALLRANDOM_BUTTON, false);
            }

            // NOTE: Call this before calling setAdapter. This is so ListView can wrap the supplied cursor with one that will also account for header and footer views.
            if (addRandomButton) {
                addRandomButton();
            }

            myAdapter = new SongsListActivityAdapter(this.getActivity(), focusedElementID, songsIDs, addRandomButton);
            myListView.setAdapter(myAdapter);
            myListView.setOnItemClickListener(new OnItemClickListener() {

                @Override
                public void onItemClick(AdapterView<?> arg0, View view, int position, long arg3) {
                    myAdapter.onSongClick(position);
                }
            });

            myListView.setSelection(myAdapter.getFocusedIDPosition());
        } catch (Exception e) {
            Toast.makeText(SoundBoxApplication.getApplicationContext(), "Error while trying to show the songs", Toast.LENGTH_LONG).show();
        }

        return rootView;
    }

    private void addRandomButton() {
        // create the button
        Button myButton = new Button(getActivity());
        myButton.setId(19);
        myButton.setText("Play all songs random");
        myButton.setCompoundDrawablesWithIntrinsicBounds(null, null, getResources().getDrawable(R.drawable.icon_random_shuffled), null);
        myButton.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent playActivityIntent = new Intent();
                playActivityIntent.setAction(SoundBoxApplication.ACTION_PLAY_ACTIVITY);

                Collections.shuffle(songsIDs);

                Bundle b = new Bundle();
                b.putStringArrayList(BundleExtra.SONGS_ID_LIST, new ArrayList<String>(songsIDs));

                playActivityIntent.putExtras(b);
                startActivity(playActivityIntent);
            }
        });

        // add the button to the header of the list
        myListView.addHeaderView(myButton);
    }
}