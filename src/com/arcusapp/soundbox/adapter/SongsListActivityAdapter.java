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

package com.arcusapp.soundbox.adapter;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.provider.MediaStore;
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
import com.arcusapp.soundbox.model.SongEntry;
import com.arcusapp.soundbox.player.MediaPlayerService;
import com.arcusapp.soundbox.util.MediaEntryHelper;

import java.util.ArrayList;
import java.util.List;

public class SongsListActivityAdapter extends BaseAdapter {
    private Activity mActivity;

    private List<SongEntry> songs;
    private MediaProvider mediaProvider;
    private MediaEntryHelper<SongEntry> mediaEntryHelper;

    private String projection = MediaStore.Audio.Media.TITLE;

    private String focusedID;
    private boolean hasHeader;

    public SongsListActivityAdapter(Activity activity, String focusedID, List<String> songsID, boolean hasHeader) {
        mActivity = activity;
        mediaProvider = new MediaProvider();
        mediaEntryHelper = new MediaEntryHelper<SongEntry>();

        this.hasHeader = hasHeader;

        List<SongEntry> temp_songs = mediaProvider.getValueFromSongs(songsID, projection);
        songs = new ArrayList<SongEntry>();
        // order them by the order given on the songsID
        for (int i = 0; i < songsID.size(); i++) {
            for (int j = 0; j < temp_songs.size(); j++) {
                if (temp_songs.get(j).getID().equals(songsID.get(i))) {
                    songs.add(temp_songs.get(j));
                    temp_songs.remove(j);
                    break;
                }
            }
        }

        this.focusedID = focusedID;
    }

    public void onSongClick(int position) {
        //call the service to play new songs
        Intent serviceIntent = new Intent();
        serviceIntent.setAction(SoundBoxApplication.ACTION_MEDIA_PLAYER_SERVICE);

        int finalpos = position;
        if (hasHeader) {
            finalpos--;
        }

        Bundle b = new Bundle();
        b.putString(BundleExtra.CURRENT_ID, songs.get(finalpos).getID().toString());
        b.putStringArrayList(BundleExtra.SONGS_ID_LIST, new ArrayList<String>(mediaEntryHelper.getIDs(songs)));
        b.putBoolean(MediaPlayerService.PLAY_NEW_SONGS, true);

        serviceIntent.putExtras(b);
        mActivity.startService(serviceIntent);

        //start the playActivity
        Intent playActivityIntent = new Intent();
        playActivityIntent.setAction(SoundBoxApplication.ACTION_PLAY_ACTIVITY);
        playActivityIntent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        mActivity.startActivity(playActivityIntent);
    }

    public int getFocusedIDPosition() {
        if (focusedID != BundleExtra.DefaultValues.DEFAULT_ID) {
            for (int i = 0; i < songs.size(); i++) {
                if (songs.get(i).getID().equals(focusedID)) {
                    return i;
                }
            }
        }

        return 0;
    }

    @Override
    public int getCount() {
        return songs.size();
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

            holder.icon.setBackgroundResource(R.drawable.icon_song);

            item.setTag(holder);
        }
        else
        {
            holder = (ViewHolder) item.getTag();
        }

        holder.text.setText(songs.get(position).getValue());

        if (songs.get(position).getID().equals(focusedID)) {
            holder.text.setTypeface(null, Typeface.BOLD);
        } else {
            holder.text.setTypeface(null, Typeface.NORMAL);
        }

        return (item);
    }
}