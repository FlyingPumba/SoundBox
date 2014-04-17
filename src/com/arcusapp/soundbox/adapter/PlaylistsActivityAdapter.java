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
import android.widget.ImageView;
import android.widget.TextView;

import com.arcusapp.soundbox.R;
import com.arcusapp.soundbox.SoundBoxApplication;
import com.arcusapp.soundbox.data.MediaProvider;
import com.arcusapp.soundbox.model.BundleExtra;
import com.arcusapp.soundbox.model.PlaylistEntry;
import com.arcusapp.soundbox.player.MediaPlayerService;

import java.util.ArrayList;
import java.util.List;

public class PlaylistsActivityAdapter extends BaseAdapter {
    private Activity mActivity;

    private List<PlaylistEntry> playlists;
    private MediaProvider mediaProvider;

    public PlaylistsActivityAdapter(Activity activity) {
        mActivity = activity;
        mediaProvider = new MediaProvider();

        playlists = mediaProvider.getAllPlayLists();
    }

    public void onPlaylistClick(int position) {
        // show the songs from that specific playlists
        Intent intent = new Intent();
        intent.setAction(SoundBoxApplication.ACTION_SONGSLIST_ACTIVITY);

        Bundle b = new Bundle();
        String playlistID = playlists.get(position).getID();
        b.putStringArrayList(BundleExtra.SONGS_ID_LIST, new ArrayList<String>(mediaProvider.getSongsFromPlaylist(playlistID)));
        intent.putExtras(b);

        mActivity.startActivity(intent);
    }

    public void onPlaylistLongClick(int position) {
        //call the service to play new songs
        Intent serviceIntent = new Intent(MediaPlayerService.PLAY_NEW_SONGS, null, SoundBoxApplication.getContext(), MediaPlayerService.class);

        Bundle b = new Bundle();
        // we play directly the playlist so we dont have a specific first song
        b.putString(BundleExtra.CURRENT_ID, BundleExtra.DefaultValues.DEFAULT_ID);

        String playlistID = playlists.get(position).getID();
        b.putStringArrayList(BundleExtra.SONGS_ID_LIST, new ArrayList<String>(mediaProvider.getSongsFromPlaylist(playlistID)));

        serviceIntent.putExtras(b);
        mActivity.startService(serviceIntent);
    }

    @Override
    public int getCount() {
        return playlists.size();
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

            holder.icon = (ImageView) item.findViewById(R.id.itemIcon);
            holder.text = (TextView) item.findViewById(R.id.itemText);

            holder.icon.setBackgroundResource(R.drawable.ic_song);

            item.setTag(holder);
        }
        else
        {
            holder = (ViewHolder) item.getTag();
        }

        holder.text.setText(playlists.get(position).getValue());

        return (item);
    }
}