package com.arcusapp.soundbox.util;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.arcusapp.soundbox.R;
import com.arcusapp.soundbox.data.MediaProvider;
import com.arcusapp.soundbox.fragment.MediaListFragment;
import com.arcusapp.soundbox.fragment.PlaylistsFragment;
import com.arcusapp.soundbox.model.BundleExtra;
import com.arcusapp.soundbox.model.MediaEntry;

import java.util.ArrayList;

public class MainSectionsAdapter extends FragmentPagerAdapter {

    private final int SONGSLIST_FRAGMENT_POSITION = 0;
    private final int ARTIST_FRAGMENT_POSITION = 1;
    private final int PLAYLISTS_FRAGMENT_POSITION = 2;

    MediaListFragment mArtistsFragment;
    MediaListFragment mSongsListFragment;
    PlaylistsFragment mPlaylistsFragment;

    Context mContext;
    MediaProvider mMediaProvider;

    public MainSectionsAdapter(Context context, FragmentManager fm) {
        super(fm);
        mContext = context;
        mMediaProvider = new MediaProvider();
    }

    @Override
    public Fragment getItem(int position) {

        switch (position) {
            case ARTIST_FRAGMENT_POSITION:
                if(mArtistsFragment == null) {
                    // New MediaListFragment with all the artists
                    Bundle bundle = new Bundle();
                    bundle.putParcelableArrayList(BundleExtra.MEDIA_ENTRY_LIST, new ArrayList<MediaEntry>(mMediaProvider.getAllArtists()));

                    mArtistsFragment = new MediaListFragment();
                    mArtistsFragment.setArguments(bundle);
                }
                return mArtistsFragment;
            case SONGSLIST_FRAGMENT_POSITION:
                if(mSongsListFragment == null) {
                    // New MediaListFragment with all the songs
                    Bundle bundle = new Bundle();
                    bundle.putParcelableArrayList(BundleExtra.MEDIA_ENTRY_LIST, new ArrayList<MediaEntry>(mMediaProvider.getAllSongs()));
                    bundle.putBoolean(MediaListFragment.ADD_PLAYALLRANDOM_BUTTON, true);

                    mSongsListFragment = new MediaListFragment();
                    mSongsListFragment.setArguments(bundle);
                }

                return mSongsListFragment;
            case PLAYLISTS_FRAGMENT_POSITION:
                if(mPlaylistsFragment == null) {
                    mPlaylistsFragment = new PlaylistsFragment();
                }
                return mPlaylistsFragment;
        }
        return null;
    }

    @Override
    public int getCount() {
        // Show 3 total pages.
        return 3;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        switch (position) {
            case ARTIST_FRAGMENT_POSITION:
                return mContext.getString(R.string.TabArtists);
            case SONGSLIST_FRAGMENT_POSITION:
                return mContext.getString(R.string.TabSongs);
            case PLAYLISTS_FRAGMENT_POSITION:
                return mContext.getString(R.string.TabPlaylists);
        }
        return null;
    }
}
