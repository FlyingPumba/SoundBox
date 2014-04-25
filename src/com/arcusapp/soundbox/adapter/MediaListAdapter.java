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
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.arcusapp.soundbox.R;
import com.arcusapp.soundbox.SoundBoxApplication;
import com.arcusapp.soundbox.data.MediaProvider;
import com.arcusapp.soundbox.model.BundleExtra;
import com.arcusapp.soundbox.model.MediaEntry;
import com.arcusapp.soundbox.player.MediaPlayerService;
import com.arcusapp.soundbox.util.MediaEntryHelper;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MediaListAdapter extends BaseAdapter {
    private Activity mActivity;

    private List<MediaEntry> mMediaContent;
    private MediaProvider mMediaProvider;
    private MediaEntryHelper<MediaEntry> mMediaEntryHelper;

    private String mDetailsProjection = MediaStore.Audio.Media.TITLE;

    private String mFocusedID;
    private boolean mHasHeader;

    public MediaListAdapter(Activity activity, String focusedID, List<MediaEntry> mediaList, boolean hasHeader) {
        mActivity = activity;
        mMediaProvider = new MediaProvider();
        mMediaEntryHelper = new MediaEntryHelper<MediaEntry>();
        mHasHeader = hasHeader;
        mMediaContent = mediaList;
        mFocusedID = focusedID;
    }

    public void onItemClick(int position) {
        // show the main activity
        Intent playActivityIntent = new Intent();
        playActivityIntent.setAction(SoundBoxApplication.ACTION_MAIN_ACTIVITY);
        playActivityIntent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        mActivity.startActivity(playActivityIntent);

        //call the service to play new media content
        Intent serviceIntent = new Intent(MediaPlayerService.PLAY_NEW_SONGS, null, SoundBoxApplication.getContext(), MediaPlayerService.class);

        if (mHasHeader) {
            position = position - 1;
        }

        Bundle b = new Bundle();
        b.putString(BundleExtra.CURRENT_ID, mMediaContent.get(position).getID());
        ArrayList<Parcelable> list = new ArrayList<Parcelable>();
        list.add(mMediaContent.get(position));
        b.putParcelableArrayList(BundleExtra.MEDIA_ENTRY_LIST, list);

        serviceIntent.putExtras(b);
        mActivity.startService(serviceIntent);
    }

    public void onPlayAllClick() {
        //start the playActivity
        Intent playActivityIntent = new Intent();
        playActivityIntent.setAction(SoundBoxApplication.ACTION_MAIN_ACTIVITY);
        playActivityIntent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        mActivity.startActivity(playActivityIntent);

        //call the service to play new songs
        Intent serviceIntent = new Intent(MediaPlayerService.PLAY_NEW_SONGS, null, SoundBoxApplication.getContext(), MediaPlayerService.class);

        Bundle b = new Bundle();
        Collections.shuffle(mMediaContent);
        b.putParcelableArrayList(BundleExtra.MEDIA_ENTRY_LIST, new ArrayList<Parcelable>(mMediaContent));
        b.putString(BundleExtra.CURRENT_ID, BundleExtra.DefaultValues.DEFAULT_ID);

        serviceIntent.putExtras(b);
        mActivity.startService(serviceIntent);
    }

    public int getFocusedPosition() {
        if (mFocusedID != BundleExtra.DefaultValues.DEFAULT_ID) {
            for(MediaEntry m : mMediaContent) {
                if(m.getID().equals(mFocusedID)) {
                    return mMediaContent.indexOf(m);
                }
            }
        }

        return 0;
    }

    @Override
    public int getCount() {
        return mMediaContent.size();
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

        holder.text.setText(mMediaContent.get(position).getValue());
        if (mMediaContent.get(position).getID().equals(mFocusedID)) {
            holder.text.setTypeface(null, Typeface.BOLD);
        } else {
            holder.text.setTypeface(null, Typeface.NORMAL);
        }

        holder.details.setVisibility(View.GONE);

        return (item);
    }
}