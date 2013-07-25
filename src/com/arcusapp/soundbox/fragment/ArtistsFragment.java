package com.arcusapp.soundbox.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnChildClickListener;

import com.arcusapp.soundbox.R;
import com.arcusapp.soundbox.adapter.ArtistsActivityAdapter;

public class ArtistsFragment extends Fragment {

    ArtistsActivityAdapter myAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_artists, container, false);

        myAdapter = new ArtistsActivityAdapter(this.getActivity());

        ExpandableListView myExpandableList = (ExpandableListView) rootView.findViewById(R.id.expandableListArtists);
        myExpandableList.setGroupIndicator(null);

        myExpandableList.setOnItemLongClickListener(new OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                if (ExpandableListView.getPackedPositionType(id) == ExpandableListView.PACKED_POSITION_TYPE_GROUP) {
                    myAdapter.onArtistLongClick(position);
                    return true;
                } else if (ExpandableListView.getPackedPositionType(id) == ExpandableListView.PACKED_POSITION_TYPE_CHILD) {
                    int groupPosition = ExpandableListView.getPackedPositionGroup(id);
                    int childPosition = ExpandableListView.getPackedPositionChild(id);
                    myAdapter.onAlbumLongClick(groupPosition, childPosition);
                    return true;
                }
                return false;
            }
        });

        myExpandableList.setOnChildClickListener(new OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
                myAdapter.onAlbumClick(groupPosition, childPosition);
                return false;
            }
        });

        myExpandableList.setAdapter(myAdapter);
        return rootView;
    }
}