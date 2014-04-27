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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.arcusapp.soundbox.R;
import com.arcusapp.soundbox.SoundBoxApplication;
import com.arcusapp.soundbox.data.MediaProvider;
import com.arcusapp.soundbox.fragment.MediaListFragment;
import com.arcusapp.soundbox.model.BundleExtra;
import com.arcusapp.soundbox.model.MediaEntry;
import com.arcusapp.soundbox.model.MediaType;
import com.arcusapp.soundbox.player.MediaPlayerService;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MediaListAdapter extends BaseAdapter {
    private Activity mActivity;

    private List<MediaEntry> mMediaContent;
    private MediaProvider mMediaProvider;

    private String mFocusedID;
    private boolean mHasHeader;

    public MediaListAdapter(Activity activity, String focusedID, List<MediaEntry> mediaList, boolean hasHeader) {
        mActivity = activity;
        mMediaProvider = new MediaProvider();
        mHasHeader = hasHeader;
        mMediaContent = mediaList;
        mFocusedID = focusedID;
    }

    public void onItemClick(int position) {
        if (mHasHeader) {
            position = position - 1;
        }

        MediaEntry media = mMediaContent.get(position);

        Intent intent = new Intent();
        Bundle bundle = new Bundle();

        switch (media.getMediaType()) {
            case Song:
                // show the main activity
                intent.setAction(SoundBoxApplication.ACTION_MAIN_ACTIVITY);
                intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                mActivity.startActivity(intent);

                //call the service to play new media content
                Intent serviceIntent = new Intent(MediaPlayerService.PLAY_NEW_SONGS, null, SoundBoxApplication.getContext(), MediaPlayerService.class);

                bundle.putString(BundleExtra.CURRENT_ID, mMediaContent.get(position).getID());
                ArrayList<Parcelable> list = new ArrayList<Parcelable>();
                list.add(media);
                bundle.putParcelableArrayList(BundleExtra.MEDIA_ENTRY_LIST, list);

                serviceIntent.putExtras(bundle);
                mActivity.startService(serviceIntent);

                break;
            case Artist:
                // show the media list activity
                intent.setAction(SoundBoxApplication.ACTION_MEDIALIST_ACTIVITY);

                bundle.putBoolean(MediaListFragment.ADD_PLAYALLRANDOM_BUTTON, true);
                List<MediaEntry> albums = mMediaProvider.getAlbumsFromArtist(media.getValue());
                bundle.putParcelableArrayList(BundleExtra.MEDIA_ENTRY_LIST, new ArrayList<MediaEntry>(albums));

                intent.putExtras(bundle);
                mActivity.startActivity(intent);
                break;
            case Album:
                // show the media list activity
                intent.setAction(SoundBoxApplication.ACTION_MEDIALIST_ACTIVITY);

                bundle.putBoolean(MediaListFragment.ADD_PLAYALLRANDOM_BUTTON, true);
                List<MediaEntry> songsAlbum = mMediaProvider.getSongsFromAlbum(media.getValue());
                bundle.putParcelableArrayList(BundleExtra.MEDIA_ENTRY_LIST, new ArrayList<MediaEntry>(songsAlbum));

                intent.putExtras(bundle);
                mActivity.startActivity(intent);
                break;
            case Playlist:
                // show the media list activity
                intent.setAction(SoundBoxApplication.ACTION_MEDIALIST_ACTIVITY);

                bundle.putBoolean(MediaListFragment.ADD_PLAYALLRANDOM_BUTTON, true);
                List<MediaEntry> songsPlaylist = mMediaProvider.getSongsFromPlaylist(media.getID());
                bundle.putParcelableArrayList(BundleExtra.MEDIA_ENTRY_LIST, new ArrayList<MediaEntry>(songsPlaylist));

                intent.putExtras(bundle);
                mActivity.startActivity(intent);
                break;
        }

    }


    public void onItemLongClick(int position) {
        if (mHasHeader) {
            position = position - 1;
        }

        MediaEntry media = mMediaContent.get(position);

        // show the main activity
        Intent intent = new Intent();
        intent.setAction(SoundBoxApplication.ACTION_MAIN_ACTIVITY);
        intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        mActivity.startActivity(intent);

        //call the service to play new media content
        Intent serviceIntent = new Intent(MediaPlayerService.PLAY_NEW_SONGS, null, SoundBoxApplication.getContext(), MediaPlayerService.class);
        Bundle bundle = new Bundle();

        switch (media.getMediaType()) {
            case Song:
                bundle.putString(BundleExtra.CURRENT_ID, mMediaContent.get(position).getID());
                ArrayList<Parcelable> list = new ArrayList<Parcelable>();
                list.add(media);
                bundle.putParcelableArrayList(BundleExtra.MEDIA_ENTRY_LIST, list);
                break;
            case Artist:
                List<MediaEntry> songsArtist = mMediaProvider.getSongsFromArtist(media.getValue());
                bundle.putParcelableArrayList(BundleExtra.MEDIA_ENTRY_LIST, new ArrayList<MediaEntry>(songsArtist));
                break;
            case Album:
                List<MediaEntry> songsAlbum = mMediaProvider.getSongsFromAlbum(media.getValue());
                bundle.putParcelableArrayList(BundleExtra.MEDIA_ENTRY_LIST, new ArrayList<MediaEntry>(songsAlbum));
                break;
            case Playlist:
                List<MediaEntry> songsPlaylist = mMediaProvider.getSongsFromPlaylist(media.getID());
                bundle.putParcelableArrayList(BundleExtra.MEDIA_ENTRY_LIST, new ArrayList<MediaEntry>(songsPlaylist));
                break;
        }


        serviceIntent.putExtras(bundle);
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

        MediaEntry media = mMediaContent.get(position);

        // set item main text
        holder.text.setText(media.getValue());
        if (mMediaContent.get(position).getID().equals(mFocusedID)) {
            holder.text.setTypeface(null, Typeface.BOLD);
        } else {
            holder.text.setTypeface(null, Typeface.NORMAL);
        }

        // set item details
        if(media.getMediaType() == MediaType.Song) {
            holder.details.setVisibility(View.GONE);
        } else {
            holder.details.setText(media.getDetail());
        }

        return (item);
    }
}