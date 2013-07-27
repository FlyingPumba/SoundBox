package com.arcusapp.soundbox.adapter;

import java.util.ArrayList;
import java.util.List;

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

public class PlaylistsAcitivityAdapter extends BaseAdapter {
    private Activity mActivity;

    private List<PlaylistEntry> playlists;
    private MediaProvider mediaProvider;

    public PlaylistsAcitivityAdapter(Activity activity) {
        mActivity = activity;
        mediaProvider = new MediaProvider();

        playlists = mediaProvider.getAllPlayLists();
    }

    public void onPlaylistClick(int position) {
        // show the songs from that specific playlists
        Intent intent = new Intent();
        intent.setAction(SoundBoxApplication.ACTION_SONGLIST_ACTIVITY);

        Bundle b = new Bundle();
        String playlistID = playlists.get(position).getID();
        b.putStringArrayList(BundleExtra.SONGS_ID_LIST, new ArrayList<String>(mediaProvider.getSongsFromPlaylist(playlistID)));
        intent.putExtras(b);

        mActivity.startActivity(intent);
    }

    public void onPlaylistLongClick(int position) {
        Intent playActivityIntent = new Intent();
        playActivityIntent.setAction("com.arcusapp.soundbox.PLAY_ACTIVITY");

        Bundle b = new Bundle();

        // we play directly the playlist so we dont have a specific first song
        b.putString(BundleExtra.CURRENT_ID, BundleExtra.DefaultValues.DEFAULT_ID);

        String playlistID = playlists.get(position).getID();
        b.putStringArrayList(BundleExtra.SONGS_ID_LIST, new ArrayList<String>(mediaProvider.getSongsFromPlaylist(playlistID)));

        playActivityIntent.putExtras(b);
        mActivity.startActivity(playActivityIntent);
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

            item.setTag(holder);
        }
        else
        {
            holder = (ViewHolder) item.getTag();
        }

        holder.icon.setBackgroundResource(R.drawable.icon_song);
        holder.text.setText(playlists.get(position).getValue());

        return (item);
    }
}