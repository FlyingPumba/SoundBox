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

import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.arcusapp.soundbox.R;
import com.arcusapp.soundbox.SoundBoxApplication;
import com.arcusapp.soundbox.activity.FoldersActivity;
import com.arcusapp.soundbox.data.MediaProvider;
import com.arcusapp.soundbox.model.SongEntry;
import com.arcusapp.soundbox.util.MediaEntryHelper;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class FoldersActivityAdapter extends BaseAdapter {

    private FoldersActivity mActivity;
    private TextView txtCurrentDirectory;

    private List<File> subDirs;
    private List<SongEntry> songs;
    private MediaProvider mediaProvider;
    private MediaEntryHelper<SongEntry> mediaEntryHelper;
    private File currentDir;

    private List<String> displayList;
    private int currentDirCount;
    private int currentSongsCount;

    /**
     * File to indicate that we are showing the first user options
     */
    private File MAIN_USER_OPTIONS = new File("defaultoptions");

    private String projection = MediaStore.Audio.Media.TITLE;

    public FoldersActivityAdapter(FoldersActivity activity, TextView currentDirectoryTextView) {
        mActivity = activity;
        txtCurrentDirectory = currentDirectoryTextView;

        mediaProvider = new MediaProvider();
        mediaEntryHelper = new MediaEntryHelper<SongEntry>();
        currentDir = MAIN_USER_OPTIONS;

        // set the first options for the user
        songs = new ArrayList<SongEntry>();
        subDirs = SoundBoxApplication.getDefaultUserDirectoriesOptions();

        displayList = new ArrayList<String>();
        makeDisplayList();
    }

    public void playCurrentDirectory() {
        /*Intent playActivityIntent = new Intent();
        playActivityIntent.setAction(SoundBoxApplication.ACTION_PLAY_ACTIVITY);

        Bundle b = new Bundle();
        b.putStringArrayList(BundleExtra.SONGS_ID_LIST, new ArrayList<String>(mediaEntryHelper.getIDs(songs)));
        playActivityIntent.putExtras(b);

        mActivity.startActivity(playActivityIntent);*/
    }

    public void onItemClick(int position) {
        if (isDirItem(position)) {
            // handle the click on a directory
            currentDir = new File(subDirs.get(position).getPath());

            songs = mediaProvider.getSongsInAFolder(currentDir, projection);
            subDirs = mediaProvider.getSubDirsInAFolder(currentDir);

            makeDisplayList();
        } else {
            // handle the click on a song
            /*Intent playActivityIntent = new Intent();
            playActivityIntent.setAction(SoundBoxApplication.ACTION_PLAY_ACTIVITY);

            Bundle b = new Bundle();
            b.putString(BundleExtra.CURRENT_ID, songs.get(position - currentDirCount).getID().toString());
            b.putStringArrayList(BundleExtra.SONGS_ID_LIST, new ArrayList<String>(mediaEntryHelper.getIDs(songs)));
            playActivityIntent.putExtras(b);

            mActivity.startActivity(playActivityIntent);*/
        }
    }

    public void backPressed() {
        // check if the current dir is not an sd card and is not the MainUserOptions
        if (!SoundBoxApplication.getSDCards().contains(currentDir) && currentDir != MAIN_USER_OPTIONS) {
            // list parent folder
            currentDir = currentDir.getParentFile();

            songs = mediaProvider.getSongsInAFolder(currentDir, projection);
            subDirs = mediaProvider.getSubDirsInAFolder(currentDir);

            makeDisplayList();
        } else if (currentDir != MAIN_USER_OPTIONS) {
            currentDir = MAIN_USER_OPTIONS;

            songs = new ArrayList<SongEntry>();
            subDirs = SoundBoxApplication.getDefaultUserDirectoriesOptions();

            makeDisplayList();
        } else {
            // the current dir is the MainUserOptions, so finish the FoldersActivity
            mActivity.finish();
        }
    }

    private void makeDisplayList() {
        displayList.clear();
        currentDirCount = subDirs.size();
        currentSongsCount = songs.size();

        for (File f : subDirs) {
            displayList.add(f.getName());
        }
        for (SongEntry s : songs) {
            displayList.add(s.getValue());
        }

        // update the textView on the FoldersActivity to show the name of the current folder
        txtCurrentDirectory.setText(getCurrentlDirName());

        // refresh the list on the FoldersActivity
        this.notifyDataSetChanged();
    }

    private String getCurrentlDirName() {
        if (currentDir.equals(MAIN_USER_OPTIONS))
            return "";
        else
            return currentDir.getName();
    }

    /**
     * Returns true if the specified item is a dir.
     * 
     * @param position the item position
     * @return boolean
     */
    private boolean isDirItem(int position) {
        return position < currentDirCount;
    }

    @Override
    public int getCount() {
        return currentDirCount + currentSongsCount;
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

            //holder.icon = (ImageView) item.findViewById(R.id.itemIcon);
            holder.text = (TextView) item.findViewById(R.id.itemText);

            item.setTag(holder);
        }
        else
        {
            holder = (ViewHolder) item.getTag();
        }

        // check if its a directory or a song
        if (isDirItem(position)) {
            //holder.icon.setBackgroundResource(R.drawable.ic_folder);
            holder.text.setText(subDirs.get(position).getName());
        } else {
            //holder.icon.setBackgroundResource(R.drawable.ic_audiofile);
            holder.text.setText(songs.get(position - currentDirCount).getValue());
        }

        return (item);
    }
}