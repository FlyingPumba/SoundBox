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

package com.arcusapp.soundbox.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.arcusapp.soundbox.R;
import com.arcusapp.soundbox.SoundBoxApplication;
import com.arcusapp.soundbox.data.MediaProvider;
import com.arcusapp.soundbox.fragment.ArtistsFragment;
import com.arcusapp.soundbox.fragment.PlayFragment;
import com.arcusapp.soundbox.fragment.PlaylistsFragment;
import com.arcusapp.soundbox.fragment.SongsListFragment;
import com.arcusapp.soundbox.model.BundleExtra;
import com.arcusapp.soundbox.player.MediaPlayerService;
import com.arcusapp.soundbox.util.CustomViewPager;
import com.arcusapp.soundbox.util.SlidingUpPanelLayout;

import java.util.ArrayList;

public class MainActivity extends ActionBarActivity implements android.support.v7.app.ActionBar.TabListener {

    private static final String PANEL_SAVED_STATE = "was_panel_expanded_last_time";

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a {@link android.support.v4.app.FragmentPagerAdapter} derivative, which
     * will keep every loaded fragment in memory. If this becomes too memory
     * intensive, it may be best to switch to a {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    SectionsPagerAdapter mSectionsPagerAdapter;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    CustomViewPager mViewPager;
    SlidingUpPanelLayout mSlidingLayout;
    PlayFragment mPlayFragment;
    private boolean mPanelExpanded = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //start the MediaPlayerService
        Intent serviceIntent = new Intent(SoundBoxApplication.ACTION_MEDIA_PLAYER_SERVICE, null, SoundBoxApplication.getContext(), MediaPlayerService.class);
        startService(serviceIntent);

        mPlayFragment = (PlayFragment) getSupportFragmentManager().findFragmentById(R.id.playFragmentContainer);

        //set up the sliding layout
        mSlidingLayout = (SlidingUpPanelLayout) findViewById(R.id.sliding_layout);
        mSlidingLayout.setPanelHeight(66);
        mSlidingLayout.setPlayFragment(mPlayFragment);
        TextView dragerView = (TextView) findViewById(R.id.txtSongTitle);
        mSlidingLayout.setDragView(dragerView);

        mSlidingLayout.setPanelSlideListener(new SlidingUpPanelLayout.PanelSlideListener() {
            @Override
            public void onPanelSlide(View panel, float slideOffset) {

            }

            @Override
            public void onPanelCollapsed(View panel) {
                //enable ViewPager Fragments
                mViewPager.setContentEnabled(true);
                //inform the fragment that panel is collapsed
                mPlayFragment.setPanelExpanded(false);
                mPanelExpanded = false;
            }

            @Override
            public void onPanelExpanded(View panel) {
                //disable ViewPager Fragments
                mViewPager.setContentEnabled(false);
                //inform the fragment that panel is expanded
                mPlayFragment.setPanelExpanded(true);
                mPanelExpanded = true;
            }

            @Override
            public void onPanelAnchored(View panel) {

            }
        });
        // Set up the action bar.
        final android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

        // Create the adapter that will return a fragment for each of the three
        // primary sections of the app.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (CustomViewPager) findViewById(R.id.pager);
        mViewPager.setAdapter(mSectionsPagerAdapter);
        mViewPager.setContentEnabled(true);

        // When swiping between different sections, select the corresponding
        // tab. We can also use ActionBar.Tab#select() to do this if we have
        // a reference to the Tab.
        mViewPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                actionBar.setSelectedNavigationItem(position);
            }
        });

        // For each of the sections in the app, add a tab to the action bar.
        for (int i = 0; i < mSectionsPagerAdapter.getCount(); i++) {
            // Create a tab with text corresponding to the page title defined by
            // the adapter. Also specify this Activity object, which implements
            // the TabListener interface, as the callback (listener) for when
            // this tab is selected.
            actionBar.addTab(actionBar.newTab()
                            .setText(mSectionsPagerAdapter.getPageTitle(i))
                            .setTabListener(this));
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        SoundBoxApplication.notifyForegroundStateChanged(true);
    }

    @Override
    protected void onStop() {
        super.onStop();
        SoundBoxApplication.notifyForegroundStateChanged(false);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_about:
                Intent intent = new Intent();
                intent.setAction(SoundBoxApplication.ACTION_ABOUT_ACTIVITY);
                startActivity(intent);
                return true;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        // Save UI state changes to the savedInstanceState.
        // This bundle will be passed to onCreate if the process is
        // killed and restarted.
        savedInstanceState.putBoolean(PANEL_SAVED_STATE, mSlidingLayout.isExpanded());
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        // Restore UI state from the savedInstanceState.
        // This bundle has also been passed to onCreate.
        mPanelExpanded = savedInstanceState.getBoolean(PANEL_SAVED_STATE);

        //If this onCraete is called after a configuration change,
        // we need to reconfigure PlayFragment and ViewPagew
        if(mPanelExpanded) {
            //disable ViewPager Fragments
            mViewPager.setContentEnabled(false);
            //inform the fragment that panel is expanded
            mPlayFragment.setPanelExpanded(true);
        } else {
            //enable ViewPager Fragments
            mViewPager.setContentEnabled(true);
            //inform the fragment that panel is collapsed
            mPlayFragment.setPanelExpanded(false);
        }
    }

    @Override
    public void onTabSelected(android.support.v7.app.ActionBar.Tab tab, android.support.v4.app.FragmentTransaction fragmentTransaction) {
        // When the given tab is selected, switch to the corresponding page in
        // the ViewPager.
        mViewPager.setCurrentItem(tab.getPosition());
    }

    @Override
    public void onTabUnselected(android.support.v7.app.ActionBar.Tab tab, android.support.v4.app.FragmentTransaction fragmentTransaction) {
    }

    @Override
    public void onTabReselected(android.support.v7.app.ActionBar.Tab tab, android.support.v4.app.FragmentTransaction fragmentTransaction) {
    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        private final int ARTIST_FRAGMENT_POSITION = 0;
        private final int SONGSLIST_FRAGMENT_POSITION = 1;
        private final int PLAYLISTS_FRAGMENT_POSITION = 2;

        ArtistsFragment mArtistsFragment;
        SongsListFragment mSongsListFragment;
        PlaylistsFragment mPlaylistsFragment;

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
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
                    return "ARTISTS";
                case SONGSLIST_FRAGMENT_POSITION:
                    return "SONGS";
                case PLAYLISTS_FRAGMENT_POSITION:
                    return "PLAYLISTS";
            }
            return null;
        }
    }
}