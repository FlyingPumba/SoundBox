package com.arcusapp.soundbox.fragment;

import java.util.List;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.Toast;

import com.arcusapp.soundbox.R;
import com.arcusapp.soundbox.SoundBoxApplication;
import com.arcusapp.soundbox.adapter.SonglistAcitivityAdapter;
import com.arcusapp.soundbox.model.BundleExtra;

public class SongsListFragment extends Fragment {

    SonglistAcitivityAdapter myAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_songs_list, container, false);

        ListView myListView = (ListView) rootView.findViewById(R.id.songslistActivityList);

        try {
            Bundle bundle = getArguments();

            String focusedElementID = bundle.getString(BundleExtra.CURRENT_ID, BundleExtra.DefaultValues.DEFAULT_ID);
            List<String> songsIDs = bundle.getStringArrayList(BundleExtra.SONGS_ID_LIST);

            myAdapter = new SonglistAcitivityAdapter(this.getActivity(), focusedElementID, songsIDs);
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
}