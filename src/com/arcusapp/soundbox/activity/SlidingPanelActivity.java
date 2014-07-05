package com.arcusapp.soundbox.activity;

import android.content.ComponentName;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Parcelable;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.widget.Adapter;
import android.widget.HeaderViewListAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.arcusapp.soundbox.R;
import com.arcusapp.soundbox.adapter.MediaListAdapter;
import com.arcusapp.soundbox.drag.DragSortListView;
import com.arcusapp.soundbox.drag.DragSortListener;
import com.arcusapp.soundbox.drag.DragSortRootView;
import com.arcusapp.soundbox.fragment.MediaListFragment;
import com.arcusapp.soundbox.fragment.SongTitleFragment;
import com.arcusapp.soundbox.model.BundleExtra;
import com.arcusapp.soundbox.model.MediaEntry;
import com.arcusapp.soundbox.model.Song;
import com.arcusapp.soundbox.player.MediaPlayerService;
import com.arcusapp.soundbox.util.SlidingPanelHost;
import com.arcusapp.soundbox.util.SlidingUpPanelLayout;
import com.nineoldandroids.view.animation.AnimatorProxy;

import java.util.ArrayList;
import java.util.List;

public class SlidingPanelActivity extends MediaServiceAwareActivity implements SlidingPanelHost,
        DragSortListener {

    public static final String SAVED_STATE_ACTION_BAR_HIDDEN = "saved_state_action_bar_hidden";

    private SlidingUpPanelLayout mSlidingLayout;

    private SongTitleFragment mSongTitleFragment;
    private MediaListFragment mCurrentPlaylistFragment;

    private MediaPlayerService mMediaService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // request action bar overlay to be able to hide whenever we want
        getWindow().requestFeature(Window.FEATURE_ACTION_BAR_OVERLAY);
        setContentView(R.layout.sliding_panel_activity);

        mSongTitleFragment = (SongTitleFragment) getSupportFragmentManager().findFragmentById(R.id.songTitleFragmentContainer);
        mCurrentPlaylistFragment = (MediaListFragment) getSupportFragmentManager().findFragmentById(R.id.currentPlaylistFragment);

        configureDragSort();

        configureSlidingLayout();

        configureActionBar(savedInstanceState);
    }

    private void configureDragSort() {
        DragSortRootView view = (DragSortRootView) findViewById(R.id.dragSortRootView);
        view.setDragSortListener(this);

        mCurrentPlaylistFragment.setDropEnabled(true);
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

    @Override
    public void onBackPressed() {
        if (mSlidingLayout != null && (mSlidingLayout.isPanelExpanded() || mSlidingLayout.isPanelAnchored())) {
            mSlidingLayout.collapsePanel();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(SAVED_STATE_ACTION_BAR_HIDDEN, mSlidingLayout.isPanelExpanded());
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
    public void setPanelExpanded(boolean expanded) {
        if(expanded){
            mSlidingLayout.expandPanel();
        } else {
            mSlidingLayout.collapsePanel();
        }
    }

    @Override
    public boolean isPanelExpanded() {
        return mSlidingLayout.isPanelExpanded();
    }

    @Override
    public void onMediaPlayerStateChanged() {
        // pass the current media to the current playlist fragment
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

    @Override
    public void onServiceConnected(ComponentName name, IBinder binder) {
        mMediaService = ((MediaPlayerService.MyBinder) binder).getService();
        mMediaService.registerListener(this);

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

    @Override
    public void onServiceDisconnected(ComponentName name) {

    }


    MediaEntry mediaBeingDragged = null;

    @Override
    public void onDragStarted(DragSortListView originList, int position) {
        if(!mSlidingLayout.isPanelExpanded()) {
            mSlidingLayout.expandPanel();
        }

        MediaListAdapter originalAdapter;

        if(originList.getAdapter() instanceof HeaderViewListAdapter) {
            originalAdapter = (MediaListAdapter)((HeaderViewListAdapter) originList.getAdapter()).getWrappedAdapter();
        } else {
            originalAdapter = (MediaListAdapter) originList.getAdapter();
        }

        mediaBeingDragged = originalAdapter.getMediaItem(position);
    }

    @Override
    public void onDragFinished(DragSortListView targetList, int position) {
        MediaListAdapter originalAdapter;

        if(targetList.getAdapter() instanceof HeaderViewListAdapter) {
            originalAdapter = (MediaListAdapter)((HeaderViewListAdapter) targetList.getAdapter()).getWrappedAdapter();
        } else {
            originalAdapter = (MediaListAdapter) targetList.getAdapter();
        }

        originalAdapter.addMediaItemAtPosition(mediaBeingDragged, position);
        originalAdapter.notifyDataSetInvalidated();
    }
}