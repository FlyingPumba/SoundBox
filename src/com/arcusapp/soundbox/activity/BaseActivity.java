package com.arcusapp.soundbox.activity;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Parcelable;
import android.support.v7.app.ActionBarActivity;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.arcusapp.soundbox.R;
import com.arcusapp.soundbox.SoundBoxApplication;
import com.arcusapp.soundbox.fragment.ContentFragment;
import com.arcusapp.soundbox.fragment.MediaListFragment;
import com.arcusapp.soundbox.fragment.PlayControlsFragment;
import com.arcusapp.soundbox.fragment.SongTitleFragment;
import com.arcusapp.soundbox.model.BundleExtra;
import com.arcusapp.soundbox.model.MediaEntry;
import com.arcusapp.soundbox.model.MediaPlayerServiceListener;
import com.arcusapp.soundbox.model.Song;
import com.arcusapp.soundbox.player.MediaPlayerService;
import com.arcusapp.soundbox.util.SlidingUpPanelLayout;
import com.nineoldandroids.view.animation.AnimatorProxy;

import java.util.ArrayList;
import java.util.List;

public abstract class BaseActivity extends ActionBarActivity {
    public static final String SAVED_STATE_ACTION_BAR_HIDDEN = "saved_state_action_bar_hidden";

    private SlidingUpPanelLayout mSlidingLayout;
    private SongTitleFragment mSongTitleFragment;
    private MediaListFragment mCurrentPlaylistFragment;
    private boolean mPanelExpanded = false;

    private MediaPlayerService mMediaService;
    private ServiceConnection mServiceConnection;
    private MediaPlayerServiceListener mServiceListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().requestFeature(Window.FEATURE_ACTION_BAR_OVERLAY);
        setContentView(R.layout.activity_base);

        mSongTitleFragment = (SongTitleFragment) getSupportFragmentManager().findFragmentById(R.id.songTitleFragmentContainer);
        mCurrentPlaylistFragment = (MediaListFragment) getSupportFragmentManager().findFragmentById(R.id.currentPlaylistFragment);

        startMediaPlayerService();

        configureSlidingLayout();

        configureActionBar(savedInstanceState);
    }

    private void configureActionBar(Bundle savedInstanceState) {
        boolean actionBarHidden = savedInstanceState != null && savedInstanceState.getBoolean(SAVED_STATE_ACTION_BAR_HIDDEN, false);
        if (actionBarHidden) {
            int actionBarHeight = getActionBarHeight();
            setActionBarTranslation(-actionBarHeight);//will "hide" an ActionBar
        }
    }

    private void configureSlidingLayout() {
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

        //mSlidingLayout.setPlayFragment(mSongTitleFragment);
        TextView dragerView = (TextView) findViewById(R.id.txtSongTitle);
        mSlidingLayout.setDragView(dragerView);

        mSlidingLayout.setPanelSlideListener(new SlidingUpPanelLayout.PanelSlideListener() {
            @Override
            public void onPanelSlide(View panel, float slideOffset) {
                setActionBarTranslation(mSlidingLayout.getCurrentParalaxOffset());
            }

            @Override
            public void onPanelExpanded(View panel) {
                mSongTitleFragment.setPanelExpanded(true);
            }

            @Override
            public void onPanelCollapsed(View panel) {
                mSongTitleFragment.setPanelExpanded(false);
            }

            @Override
            public void onPanelAnchored(View panel) {
            }

            @Override
            public void onPanelHidden(View panel) {
            }
        });
    }

    private void startMediaPlayerService() {

        mServiceListener = new MediaPlayerServiceListener() {
            @Override
            public void onMediaPlayerStateChanged() {
                // pass the current media to the navigation drawer
                if(mMediaService != null){
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Bundle bundle = new Bundle();
                            List<MediaEntry> currentMedia = mMediaService.getLoadedMedia();
                            bundle.putParcelableArrayList(BundleExtra.MEDIA_ENTRY_LIST, new ArrayList<Parcelable>(currentMedia));
                            Song currentSong = mMediaService.getCurrentSong();
                            bundle.putString(BundleExtra.CURRENT_ID, currentSong.getID());

                            mCurrentPlaylistFragment.setMedia(bundle);
                        }
                    });
                }
            }

            @Override
            public void onExceptionRaised(Exception ex) {

            }
        };

        Intent serviceIntent = new Intent(SoundBoxApplication.ACTION_MEDIA_PLAYER_SERVICE, null, SoundBoxApplication.getContext(), MediaPlayerService.class);
        startService(serviceIntent);
    }

    private void bindMediaPlayerService() {
        mServiceConnection = new ServiceConnection() {
            public void onServiceConnected(ComponentName className, IBinder binder) {
                mMediaService = ((MediaPlayerService.MyBinder) binder).getService();
                mMediaService.registerListener(mServiceListener);

                // configure the navigation drawer for the first time
                Bundle bundle = new Bundle();
                List<MediaEntry> currentMedia = mMediaService.getLoadedMedia();
                if(currentMedia != null){
                    bundle.putParcelableArrayList(BundleExtra.MEDIA_ENTRY_LIST, new ArrayList<MediaEntry>(currentMedia));
                    Song currentSong = mMediaService.getCurrentSong();
                    bundle.putString(BundleExtra.CURRENT_ID, currentSong.getID());

                    mCurrentPlaylistFragment.setMedia(bundle);
                }
            }

            public void onServiceDisconnected(ComponentName className) {
            }
        };

        Intent serviceIntent = new Intent(SoundBoxApplication.ACTION_MEDIA_PLAYER_SERVICE, null, SoundBoxApplication.getContext(), MediaPlayerService.class);
        bindService(serviceIntent, mServiceConnection, Context.BIND_AUTO_CREATE);
    }

    @Override
    protected void onStart() {
        super.onStart();
        SoundBoxApplication.notifyForegroundStateChanged(true);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mMediaService == null) {
            bindMediaPlayerService();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mMediaService != null) {
            mMediaService.unRegisterListener(mServiceListener);
            unbindService(mServiceConnection);
            mMediaService = null;
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        SoundBoxApplication.notifyForegroundStateChanged(false);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(SAVED_STATE_ACTION_BAR_HIDDEN, mSlidingLayout.isPanelExpanded());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_base, menu);
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

    private int getActionBarHeight(){
        int actionBarHeight = 0;
        TypedValue tv = new TypedValue();
        if (getTheme().resolveAttribute(android.R.attr.actionBarSize, tv, true)) {
            actionBarHeight = TypedValue.complexToDimensionPixelSize(tv.data,getResources().getDisplayMetrics());
        }
        return actionBarHeight;
    }

    public void setActionBarTranslation(float y) {
        // Figure out the actionbar height
        int actionBarHeight = getActionBarHeight();
        // A hack to add the translation to the action bar
        ViewGroup content = ((ViewGroup) findViewById(android.R.id.content).getParent());
        int children = content.getChildCount();
        for (int i = 0; i < children; i++) {
            View child = content.getChildAt(i);
            if (child.getId() != android.R.id.content) {
                // TODO: FIX, "*2" only if the content fragment has tabs, like the Home Fragment
                if (y <= -actionBarHeight*2) {
                    child.setVisibility(View.GONE);
                } else {
                    child.setVisibility(View.VISIBLE);
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                        child.setTranslationY(y);
                    } else {
                        AnimatorProxy.wrap(child).setTranslationY(y);
                    }
                }
            }
        }
    }

    @Override
    public void onBackPressed() {
        if (mSlidingLayout != null && mSlidingLayout.isPanelExpanded() || mSlidingLayout.isPanelAnchored()) {
            mSlidingLayout.collapsePanel();
        } else {
            super.onBackPressed();
        }
    }
}