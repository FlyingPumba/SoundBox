package com.arcusapp.soundbox.adapter;

import java.util.ArrayList;
import java.util.List;

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
import com.arcusapp.soundbox.activity.SongListActivity;
import com.arcusapp.soundbox.data.MediaProvider;
import com.arcusapp.soundbox.model.BundleExtra;
import com.arcusapp.soundbox.model.SongEntry;

public class SonglistAcitivityAdapter extends BaseAdapter {
	private SongListActivity mActivity;

	private List<SongEntry> songs;
	private List<String> songsID;
	private MediaProvider mediaProvider;

	private String projection = MediaStore.Audio.Media.TITLE;

	private String focusedID;

	public SonglistAcitivityAdapter(SongListActivity activity, String focusedID, List<String> songsID) {
		mActivity = activity;
		mediaProvider = new MediaProvider();
		this.songsID = songsID;
		songs = mediaProvider.getValueFromSongs(songsID, projection);

		this.focusedID = focusedID;
	}

	public void onSongClick(int position) {
		Intent playActivityIntent = new Intent();
		playActivityIntent.setAction(SoundBoxApplication.ACTION_PLAY_ACTIVITY);

		Bundle b = new Bundle();
		b.putString(BundleExtra.CURRENT_ID, songs.get(position).getID().toString());
		b.putStringArrayList(BundleExtra.SONGS_ID_LIST, new ArrayList<String>(songsID));

		playActivityIntent.putExtras(b);
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

			holder.icon = (ImageView) item.findViewById(R.id.foldersListIcon);
			holder.text = (TextView) item.findViewById(R.id.foldersListText);

			item.setTag(holder);
		}
		else
		{
			holder = (ViewHolder) item.getTag();
		}

		holder.icon.setImageResource(R.drawable.filetype_music);
		holder.text.setText(songs.get(position).getValue());

		if (songs.get(position).getID().equals(focusedID)) {
			holder.text.setTypeface(null, Typeface.BOLD);
		} else {
			holder.text.setTypeface(null, Typeface.NORMAL);
		}

		return (item);
	}
}
