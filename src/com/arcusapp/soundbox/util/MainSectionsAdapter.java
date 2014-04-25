package com.arcusapp.soundbox.util;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.arcusapp.soundbox.R;
import com.arcusapp.soundbox.data.MediaProvider;
import com.arcusapp.soundbox.fragment.ArtistsFragment;
import com.arcusapp.soundbox.fragment.PlaylistsFragment;
import com.arcusapp.soundbox.fragment.SongsListFragment;
import com.arcusapp.soundbox.model.BundleExtra;

import java.util.ArrayList;

public class MainSectionsAdapter extends FragmentPagerAdapter {

    private final int SONGSLIST_FRAGMENT_POSITION = 0;
    private final int ARTIST_FRAGMENT_POSITION = 1;
    private final int PLAYLISTS_FRAGMENT_POSITION = 2;

    ArtistsFragment mArtistsFragment;
    SongsListFragment mSongsListFragment;
    PlaylistsFragment mPlaylistsFragment;

    Context mContext;

    public MainSectionsAdapter(Context context, FragmentManager fm) {
        super(fm);
        mContext = context;
    }

    @Override
    public Fragment getItem(int position) {

        switch (position) {
            case ARTIST_FRAGMENT_POSITION:
                if(mArtistsFragment == null) {
                    mArtistsFragment = new ArtistsFragment();
                }
                return mArtistsFragment;
            case SONGSLIST_FRAGMENT_POSITION:
                if(mSongsListFragment == null) {
                    MediaProvider media = new MediaProvider();
                    Bundle bundle = new Bundle();
                    bundle.putStringArrayList(BundleExtra.SONGS_ID_LIST, new ArrayList<String>(media.getAllSongs()));
                    bundle.putBoolean(SongsListFragment.ADD_PLAYALLRANDOM_BUTTON, true);

                    mSongsListFragment = new SongsListFragment();
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
