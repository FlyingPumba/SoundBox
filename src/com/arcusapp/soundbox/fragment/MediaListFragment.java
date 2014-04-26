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

package com.arcusapp.soundbox.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;

import com.arcusapp.soundbox.R;
import com.arcusapp.soundbox.SoundBoxApplication;
import com.arcusapp.soundbox.adapter.MediaListAdapter;
import com.arcusapp.soundbox.model.BundleExtra;
import com.arcusapp.soundbox.model.MediaEntry;
import com.arcusapp.soundbox.player.MediaPlayerService;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MediaListFragment extends Fragment {

    public static final String ADD_PLAYALLRANDOM_BUTTON = "addRandomButton";

    ListView mListView;
    private MediaListAdapter mAdapter;
    private String focusedElementID = BundleExtra.DefaultValues.DEFAULT_ID;
    private boolean addRandomButton = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // try to get the bundle from the arguments
        Bundle bundleArg = getArguments();
        Bundle bundleInt = getActivity().getIntent().getExtras();

        List<MediaEntry> mMediaContent;

        if (bundleInt != null) {
            focusedElementID = BundleExtra.getBundleString(bundleInt, BundleExtra.CURRENT_ID, BundleExtra.DefaultValues.DEFAULT_ID);
            mMediaContent = bundleInt.getParcelableArrayList(BundleExtra.MEDIA_ENTRY_LIST);
            addRandomButton = bundleInt.getBoolean(ADD_PLAYALLRANDOM_BUTTON, false);
        } else if(bundleArg != null) {
            focusedElementID = BundleExtra.getBundleString(bundleArg, BundleExtra.CURRENT_ID, BundleExtra.DefaultValues.DEFAULT_ID);
            mMediaContent = bundleArg.getParcelableArrayList(BundleExtra.MEDIA_ENTRY_LIST);
            addRandomButton = bundleArg.getBoolean(ADD_PLAYALLRANDOM_BUTTON, false);
        } else {
            Log.d(MediaListFragment.class.getName(), "MediaListFragment created without media content");
            mMediaContent = new ArrayList<MediaEntry>();
        }

        mAdapter = new MediaListAdapter(this.getActivity(), focusedElementID, mMediaContent, addRandomButton);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_songs_list, container, false);

        mListView = (ListView) rootView.findViewById(R.id.songslistActivityList);

        // NOTE: Call this before calling setAdapter. This is so ListView can wrap the supplied cursor with one that will also account for header and footer views.
        if (addRandomButton) {
            addRandomButton();
        }

        mListView.setAdapter(mAdapter);
        mListView.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View view, int position, long arg3) {
                mAdapter.onItemClick(position);
            }
        });

        mListView.setSelection(mAdapter.getFocusedPosition());

        return rootView;
    }

    private void addRandomButton() {
        // create the button
        Button myButton = new Button(getActivity());
        myButton.setId(19);
        myButton.setText(this.getString(R.string.LabelPlaySongsRandom));
        myButton.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20);
        myButton.setCompoundDrawablesWithIntrinsicBounds(null, null, getResources().getDrawable(R.drawable.ic_random_shuffled), null);
        myButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                mAdapter.onPlayAllClick();
            }
        });

        // add the button to the header of the list
        mListView.addHeaderView(myButton);
    }
}