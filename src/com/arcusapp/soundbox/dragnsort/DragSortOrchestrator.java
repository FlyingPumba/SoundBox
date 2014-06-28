package com.arcusapp.soundbox.dragnsort;

import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;

import com.arcusapp.soundbox.dragnsort.views.DragSortListView;
import com.arcusapp.soundbox.dragnsort.views.OrchestratorFloatViewManager;

import java.util.ArrayList;
import java.util.List;

public class DragSortOrchestrator  extends OrchestratorFloatViewManager implements View.OnTouchListener, GestureDetector.OnGestureListener {

    /**
     * Drag init mode enum.
     */
    public static final int ON_DOWN = 0;
    public static final int ON_DRAG = 1;
    public static final int ON_LONG_PRESS = 2;

    private int mDragInitMode = ON_DOWN;

    private boolean mSortEnabled = true;

    /**
     * Remove mode enum.
     */
    public static final int CLICK_REMOVE = 0;
    public static final int FLING_REMOVE = 1;

    /**
     * The current remove mode.
     */
    private int mRemoveMode;

    private boolean mRemoveEnabled = false;
    private boolean mIsRemoving = false;

    private GestureDetector mDetector;

    private GestureDetector mFlingRemoveDetector;

    private int mTouchSlop;

    public static final int MISS = -1;

    private int mHitPos = MISS;
    private int mFlingHitPos = MISS;

    private int mClickRemoveHitPos = MISS;

    private int[] mTempLoc = new int[2];

    private int mItemX;
    private int mItemY;

    private int mCurrX;
    private int mCurrY;

    private boolean mDragging = false;

    private float mFlingSpeed = 500f;

    private int mDragHandleId;

    private int mClickRemoveId;

    private int mFlingHandleId;
    private boolean mCanDrag;

    private int mPositionX;

    List<DragSortListView> lists;

    public DragSortOrchestrator(View rootView) {
        lists = new ArrayList<DragSortListView>();
        //find all the DragSortListViews on the rootView
        exploreRootView(rootView);

        mDetector = new GestureDetector(rootView.getContext(), this);
        mFlingRemoveDetector = new GestureDetector(rootView.getContext(), mFlingRemoveListener);
        mFlingRemoveDetector.setIsLongpressEnabled(false);
        mTouchSlop = ViewConfiguration.get(rootView.getContext()).getScaledTouchSlop();
        mDragHandleId = dragHandleId;
        mClickRemoveId = clickRemoveId;
        mFlingHandleId = flingHandleId;
        setRemoveMode(removeMode);
        setDragInitMode(dragInitMode);

        configureLists();
    }

    private void exploreRootView(View rootView) {
        if (rootView instanceof ViewGroup) {
            for (int i = 0; i < ((ViewGroup) rootView).getChildCount(); i++) {
                exploreRootView(((ViewGroup) rootView).getChildAt(i));
            }
        } else if (rootView instanceof DragSortListView) {
            lists.add((DragSortListView) rootView);
        }
    }

    private void configureLists() {
        for(DragSortListView dslv : lists) {
            dslv.setFloatViewManager(this);
            dslv.setOnTouchListener(this);
        }
    }

    @Override
    public boolean onDown(MotionEvent e) {
        return false;
    }

    @Override
    public void onShowPress(MotionEvent e) {

    }

    @Override
    public boolean onSingleTapUp(MotionEvent e) {
        return false;
    }

    @Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
        return false;
    }

    @Override
    public void onLongPress(MotionEvent e) {

    }

    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        return false;
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        return false;
    }

    private GestureDetector.OnGestureListener mFlingRemoveListener =
            new GestureDetector.SimpleOnGestureListener() {
                @Override
                public final boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
                                             float velocityY) {
                    // Log.d("mobeta", "on fling remove called");
                    if (mRemoveEnabled && mIsRemoving) {
                        int w = mDslv.getWidth();
                        int minPos = w / 5;
                        if (velocityX > mFlingSpeed) {
                            if (mPositionX > -minPos) {
                                mDslv.stopDragWithVelocity(true, velocityX);
                            }
                        } else if (velocityX < -mFlingSpeed) {
                            if (mPositionX < minPos) {
                                mDslv.stopDragWithVelocity(true, velocityX);
                            }
                        }
                        mIsRemoving = false;
                    }
                    return false;
                }
            };
}
