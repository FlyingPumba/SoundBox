package com.arcusapp.soundbox.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ListView;

import com.arcusapp.soundbox.R;
import com.arcusapp.soundbox.adapter.PlaylistsAcitivityAdapter;

public class PlaylistsFragment extends Fragment {

    PlaylistsAcitivityAdapter myAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_playlists, container, false);

        myAdapter = new PlaylistsAcitivityAdapter(getActivity());
        ListView listView = (ListView) rootView.findViewById(R.id.playlistsActivityList);

        listView.setLongClickable(true);
        listView.setOnItemLongClickListener(new OnItemLongClickListener() {

            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int pos, long id) {
                myAdapter.onPlaylistLongClick(pos);
                return true;
            }
        });

        listView.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int pos, long id) {
                myAdapter.onPlaylistClick(pos);
            }
        });

        listView.setAdapter(myAdapter);

        return rootView;
    }
}