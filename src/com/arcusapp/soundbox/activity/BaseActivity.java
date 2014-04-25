package com.arcusapp.soundbox.activity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.arcusapp.soundbox.R;
import com.arcusapp.soundbox.SoundBoxApplication;
import com.arcusapp.soundbox.fragment.ContentFragment;
import com.arcusapp.soundbox.fragment.PlayFragment;
import com.arcusapp.soundbox.player.MediaPlayerService;
import com.arcusapp.soundbox.util.SlidingUpPanelLayout;

public abstract class BaseActivity extends ActionBarActivity {

    private static final String PANEL_SAVED_STATE = "was_panel_expanded_last_time";

    SlidingUpPanelLayout mSlidingLayout;
    PlayFragment mPlayFragment;
    ContentFragment mContentFragment;
    private boolean mPanelExpanded = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base);

        startMediaPlayerService();

        mPlayFragment = (PlayFragment) getSupportFragmentManager().findFragmentById(R.id.playFragmentContainer);
        mContentFragment = (ContentFragment) getSupportFragmentManager().findFragmentById(R.id.contentFragmentContainer);

        configureSlidingPanel();

        configureActionBar();
    }

    private void configureActionBar() {
        final ActionBar actionBar = getSupportActionBar();

        //set custom action bar layout
        actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        actionBar.setCustomView(R.layout.action_bar);

        // workaround to show the actionbar above the tabs
        // http://stackoverflow.com/questions/12973143/actionbarsherlock-tabs-appearing-above-actionbar-with-custom-view
        actionBar.setDisplayShowHomeEnabled(true);
        View homeIcon = findViewById(android.R.id.home);
        ((View) homeIcon.getParent()).setVisibility(View.GONE);
    }

    private void startMediaPlayerService() {
        Intent serviceIntent = new Intent(SoundBoxApplication.ACTION_MEDIA_PLAYER_SERVICE, null, SoundBoxApplication.getContext(), MediaPlayerService.class);
        startService(serviceIntent);
    }

    private void configureSlidingPanel() {

        //set up the sliding layout
        mSlidingLayout = (SlidingUpPanelLayout) findViewById(R.id.sliding_layout);

        // set the height of the sliding panel
        final LinearLayout slidingPanel = (LinearLayout) findViewById(R.id.slidingPanel);
        slidingPanel.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                // gets called after layout has been done but before display
                int height = slidingPanel.getHeight();
                mSlidingLayout.setPanelHeight(height);
                if(Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
                    slidingPanel.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                } else {
                    slidingPanel.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                }
            }
        });

        mSlidingLayout.setPlayFragment(mPlayFragment);
        TextView dragerView = (TextView) findViewById(R.id.txtSongTitle);
        mSlidingLayout.setDragView(dragerView);

        mSlidingLayout.setPanelSlideListener(new SlidingUpPanelLayout.PanelSlideListener() {
            @Override
            public void onPanelSlide(View panel, float slideOffset) {

            }

            @Override
            public void onPanelCollapsed(View panel) {
                mContentFragment.onPanelStateChanged(true);
                //inform the fragment that panel is collapsed
                mPlayFragment.setPanelExpanded(false);
                mPanelExpanded = false;
            }

            @Override
            public void onPanelExpanded(View panel) {
                mContentFragment.onPanelStateChanged(false);
                //inform the fragment that panel is expanded
                mPlayFragment.setPanelExpanded(true);
                mPanelExpanded = true;
            }

            @Override
            public void onPanelAnchored(View panel) {

            }
        });
    }

    public void setContentFragmentInstance(ContentFragment contentFragment) {
        mContentFragment = contentFragment;
    }

    @Override
    public void onBackPressed() {
        if(mSlidingLayout.isExpanded()) {
            mSlidingLayout.collapsePane();
        } else {
            super.onBackPressed();
        }
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

        //If this onCreate is called after a configuration change,
        // we need to reconfigure PlayFragment and ContentFragment
        if(mPanelExpanded) {
            mContentFragment.onPanelStateChanged(true);
            mPlayFragment.setPanelExpanded(true);
        } else {
            mContentFragment.onPanelStateChanged(false);
            mPlayFragment.setPanelExpanded(false);
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
        //getMenuInflater().inflate(R.menu.menu_base, menu);
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
}
