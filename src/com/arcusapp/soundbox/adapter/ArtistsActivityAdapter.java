package com.arcusapp.soundbox.adapter;

import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.arcusapp.soundbox.R;
import com.arcusapp.soundbox.SoundBoxApplication;
import com.arcusapp.soundbox.activity.ArtistsActivity;
import com.arcusapp.soundbox.data.MediaProvider;
import com.arcusapp.soundbox.model.BundleExtra;

public class ArtistsActivityAdapter extends BaseExpandableListAdapter {
	private ArtistsActivity mActivity;

	private List<String> mArtists;
	private List<List<String>> mAlbums;
	private MediaProvider mediaProvider;

	public ArtistsActivityAdapter(ArtistsActivity activity) {
		mActivity = activity;
		mediaProvider = new MediaProvider();

		// get all the Artists
		mArtists = mediaProvider.getAllArtists();

		// get the Albums foreach Artist
		mAlbums = new ArrayList<List<String>>();
		for (int i = 0; i < mArtists.size(); i++) {
			mAlbums.add(mediaProvider.getAlbumsFromArtist(mArtists.get(i).toString()));
		}
	}

	public void onArtistLongClick(int position) {
		Intent playActivityIntent = new Intent();
		playActivityIntent.setAction(SoundBoxApplication.ACTION_PLAY_ACTIVITY);

		Bundle b = new Bundle();
		List<String> ids = mediaProvider.getSongsFromArtist(mArtists.get(position));
		b.putStringArrayList(BundleExtra.SONGS_ID_LIST, new ArrayList<String>(ids));
		b.putString(BundleExtra.CURRENT_ID, BundleExtra.DefaultValues.DEFAULT_ID);
		playActivityIntent.putExtras(b);

		mActivity.startActivity(playActivityIntent);
	}

	public void onAlbumLongClick(int groupPosition, int childPosition) {
		Intent playActivityIntent = new Intent();
		playActivityIntent.setAction(SoundBoxApplication.ACTION_PLAY_ACTIVITY);

		Bundle b = new Bundle();
		List<String> ids = mediaProvider.getSongsFromAlbum(mAlbums.get(groupPosition).get(childPosition));
		b.putStringArrayList(BundleExtra.SONGS_ID_LIST, new ArrayList<String>(ids));
		b.putString(BundleExtra.CURRENT_ID, BundleExtra.DefaultValues.DEFAULT_ID);
		playActivityIntent.putExtras(b);

		mActivity.startActivity(playActivityIntent);
	}

	public void onAlbumClick(int groupPosition, int childPosition) {
		Intent intent = new Intent();
		intent.setAction(SoundBoxApplication.ACTION_SONGLIST_ACTIVITY);

		Bundle b = new Bundle();
		List<String> ids = mediaProvider.getSongsFromAlbum(mAlbums.get(groupPosition).get(childPosition));
		b.putStringArrayList(BundleExtra.SONGS_ID_LIST, new ArrayList<String>(ids));
		b.putString(BundleExtra.CURRENT_ID, BundleExtra.DefaultValues.DEFAULT_ID);
		intent.putExtras(b);

		mActivity.startActivity(intent);
	}

	@Override
	public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
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

		holder.text.setText(mArtists.get(groupPosition));
		if (isExpanded) {
			holder.icon.setImageResource(R.drawable.icon_artist_selected);
			holder.text.setTypeface(null, Typeface.BOLD);
		} else {
			holder.icon.setImageResource(R.drawable.icon_artist);
			holder.text.setTypeface(null, Typeface.NORMAL);
		}

		return (item);
	}

	@Override
	public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
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

		holder.icon.setImageResource(R.drawable.icon_album);
		holder.text.setText(mAlbums.get(groupPosition).get(childPosition));

		return (item);
	}

	@Override
	public Object getGroup(int groupPosition) {
		// gets the title of each parent/group
		return mArtists.get(groupPosition).toString();
	}

	@Override
	public Object getChild(int groupPosition, int childPosition) {
		// gets the name of each item
		return mAlbums.get(groupPosition).get(childPosition).toString();
	}

	@Override
	public int getGroupCount() {
		// counts the number of group/parent items so the list knows how many
		// times calls getGroupView() method
		return mArtists.size();
	}

	@Override
	public int getChildrenCount(int groupPosition) {
		// counts the number of children items so the list knows how many times
		// calls getChildView() method
		return mAlbums.get(groupPosition).size();
	}

	@Override
	public long getGroupId(int groupPosition) {
		return groupPosition;
	}

	@Override
	public long getChildId(int groupPosition, int childPosition) {
		return childPosition;
	}

	@Override
	public boolean hasStableIds() {
		return true;
	}

	@Override
	public boolean isChildSelectable(int groupPosition, int childPosition) {
		return true;
	}
}
