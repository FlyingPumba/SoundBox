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

package com.arcusapp.soundbox.adapter;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.arcusapp.soundbox.R;
import com.arcusapp.soundbox.SoundBoxApplication;
import com.arcusapp.soundbox.data.MediaProvider;
import com.arcusapp.soundbox.model.BundleExtra;
import com.arcusapp.soundbox.player.MediaPlayerService;

import java.util.ArrayList;
import java.util.List;

public class ArtistsActivityAdapter extends BaseAdapter {
    private Activity mActivity;

    private List<String> mArtists;
    private List<List<String>> mAlbums;
    private MediaProvider mediaProvider;

    public ArtistsActivityAdapter(Activity activity) {
        mActivity = activity;
        mediaProvider = new MediaProvider();

        // get all the Artists
        mArtists = mediaProvider.getAllArtists();

        // get the Albums foreach Artist
        mAlbums = new ArrayList<List<String>>();
        for (int i = 0; i < mArtists.size(); i++) {
            mAlbums.add(mediaProvider.getAlbumsFromArtist(mArtists.get(i)));
        }
    }

    public void onArtistClick(int position) {
        //call the service to play new songs
        Intent serviceIntent = new Intent(MediaPlayerService.PLAY_NEW_SONGS, null, SoundBoxApplication.getContext(), MediaPlayerService.class);

        Bundle b = new Bundle();
        List<String> ids = mediaProvider.getSongsFromArtist(mArtists.get(position));
        b.putStringArrayList(BundleExtra.SONGS_ID_LIST, new ArrayList<String>(ids));
        b.putString(BundleExtra.CURRENT_ID, BundleExtra.DefaultValues.DEFAULT_ID);

        serviceIntent.putExtras(b);
        mActivity.startService(serviceIntent);
    }

    public void onAlbumLongClick(int groupPosition, int childPosition) {
        //call the service to play new songs
        Intent serviceIntent = new Intent(MediaPlayerService.PLAY_NEW_SONGS, null, SoundBoxApplication.getContext(), MediaPlayerService.class);

        Bundle b = new Bundle();
        List<String> ids = mediaProvider.getSongsFromAlbum(mAlbums.get(groupPosition).get(childPosition));
        b.putStringArrayList(BundleExtra.SONGS_ID_LIST, new ArrayList<String>(ids));
        b.putString(BundleExtra.CURRENT_ID, BundleExtra.DefaultValues.DEFAULT_ID);

        serviceIntent.putExtras(b);
        mActivity.startService(serviceIntent);
    }

    public void onAlbumClick(int groupPosition, int childPosition) {
        Intent intent = new Intent();
        intent.setAction(SoundBoxApplication.ACTION_SONGSLIST_ACTIVITY);

        Bundle b = new Bundle();
        List<String> ids = mediaProvider.getSongsFromAlbum(mAlbums.get(groupPosition).get(childPosition));
        b.putStringArrayList(BundleExtra.SONGS_ID_LIST, new ArrayList<String>(ids));
        b.putString(BundleExtra.CURRENT_ID, BundleExtra.DefaultValues.DEFAULT_ID);
        intent.putExtras(b);

        mActivity.startActivity(intent);
    }

    @Override
    public int getCount() {
        return mArtists.size();
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View item = convertView;
        ViewHolder holder;

        if (item == null)
        {
            LayoutInflater inflater = mActivity.getLayoutInflater();
            item = inflater.inflate(R.layout.default_listitem, null);

            holder = new ViewHolder();

            holder.text = (TextView) item.findViewById(R.id.itemText);
            holder.details = (TextView) item.findViewById(R.id.itemDetail);

            item.setTag(holder);
        }
        else
        {
            holder = (ViewHolder) item.getTag();
        }

        holder.text.setText(mArtists.get(position));
        holder.details.setText(mAlbums.get(position).size() + " albums");

        return (item);
    }
}