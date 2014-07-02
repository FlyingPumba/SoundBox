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

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.Toast;

import com.arcusapp.soundbox.R;
import com.arcusapp.soundbox.adapter.MediaListAdapter;
//import com.arcusapp.soundbox.drag.old.DragSortController;
import com.arcusapp.soundbox.drag.DragSortListView;
import com.arcusapp.soundbox.model.BundleExtra;
import com.arcusapp.soundbox.model.MediaEntry;

import java.util.ArrayList;
import java.util.List;

public class MediaListFragment extends ContentFragment {

    public static final String ADD_PLAYALLRANDOM_BUTTON = "addRandomButton";

    DragSortListView mListView;
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
        View rootView = inflater.inflate(R.layout.fragment_media_list, container, false);

        mListView = (DragSortListView) rootView.findViewById(R.id.mediaList);

        // NOTE: Call this before calling setAdapter.
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

        /*mListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                mAdapter.onItemLongClick(position);
                return true;
            }
        });*/

        mListView.setSelection(mAdapter.getFocusedPosition());

        configureDragSort();

        return rootView;
    }

    private void configureDragSort() {
        /*DragSortListView.DropListener onDrop = new DragSortListView.DropListener() {
            @Override
            public void drop(int from, int to)
            {
                if (from != to)
                {
                    Toast.makeText(MediaListFragment.this.getActivity(), from +" to "+ to, Toast.LENGTH_LONG).show();
                }
            }
        };

        DragSortListView.RemoveListener onRemove = new DragSortListView.RemoveListener()
        {
            @Override
            public void remove(int which)
            {
                Toast.makeText(MediaListFragment.this.getActivity(), "remove " + which, Toast.LENGTH_LONG).show();
            }
        };

        mListView.setDropListener(onDrop);
        mListView.setRemoveListener(onRemove);

        DragSortController controller = new DragSortController(mListView);
        controller.setDragHandleId(R.id.mediaListItem);
        //controller.setClickRemoveId(R.id.mediaListItem);
        controller.setRemoveEnabled(false);
        controller.setSortEnabled(true);
        controller.setDragInitMode(2);
        //controller.setRemoveMode(1);

        mListView.setFloatViewManager(controller);
        mListView.setOnTouchListener(controller);
        mListView.setDragEnabled(true);*/
    }

    private void addRandomButton() {
        LayoutInflater inflater = getActivity().getLayoutInflater();
        ViewGroup header = (ViewGroup) inflater.inflate(R.layout.media_list_header, mListView, false);
        Button button = (Button) header.findViewById(R.id.playAllButton);
        button.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                mAdapter.onPlayAllClick();
            }
        });

        mListView.addHeaderView(header);
    }

    public void setMedia(Bundle bundle){
        if(bundle != null) {
            focusedElementID = BundleExtra.getBundleString(bundle, BundleExtra.CURRENT_ID, BundleExtra.DefaultValues.DEFAULT_ID);
            List<MediaEntry> mMediaContent = bundle.getParcelableArrayList(BundleExtra.MEDIA_ENTRY_LIST);

            mAdapter.setMedia(focusedElementID, mMediaContent);
            mAdapter.notifyDataSetChanged();
            mListView.setSelection(mAdapter.getFocusedPosition());
        }  else {
            Log.d(MediaListFragment.class.getName(), "setMedia called with null bundle");
        }
    }

    @Override
    public void onPanelStateChanged(boolean expanded) {
        mListView.setEnabled(!expanded);
    }
}