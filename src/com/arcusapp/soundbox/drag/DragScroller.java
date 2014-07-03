package com.arcusapp.soundbox.drag;

import android.os.SystemClock;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;

public class DragScroller implements Runnable {

    private boolean mAbort;

    private long mPrevTime;
    private long mCurrTime;

    private int dy;
    private float dt;
    private long tStart;
    private int scrollDir;

    public final static int STOP = -1;
    public final static int UP = 0;
    public final static int DOWN = 1;

    private float mScrollSpeed; // pixels per ms

    private boolean mScrolling = false;

    private int mLastHeader;
    private int mFirstFooter;

    private DragSortListView mList;
    private View mFloatView;
    private int mFloatViewHeight;
    private int mFloatViewHeightHalf;

    public DragScroller(DragSortListView list, View floatView) {
        mList = list;
        mFloatView = floatView;
    }

    private void measureFloatView() {
        if (mFloatView != null) {
            mFloatViewHeight = mFloatView.getMeasuredHeight();
            mFloatViewHeightHalf = mFloatViewHeight / 2;
        }
    }

    public boolean isScrolling() {
        return mScrolling;
    }

    public int getScrollDir() {
        return mScrolling ? scrollDir : STOP;
    }

    public void startScrolling(int dir) {
        if (!mScrolling) {
            // Debug.startMethodTracing("dslv-scroll");
            mAbort = false;
            mScrolling = true;
            tStart = SystemClock.uptimeMillis();
            mPrevTime = tStart;
            scrollDir = dir;
            mList.post(this);
        }
    }

    public void stopScrolling(boolean now) {
        if (now) {
            mList.removeCallbacks(this);
            mScrolling = false;
        } else {
            mAbort = true;
        }
    }

    @Override
    public void run() {
        if (mAbort) {
            mScrolling = false;
            return;
        }

        final int first = mList.getFirstVisiblePosition();
        final int last = mList.getLastVisiblePosition();
        final int count = mList.getCount();
        final int padTop = mList.getPaddingTop();
        final int listHeight = mList.getHeight() - padTop - mList.getPaddingBottom();

        /*int minY = Math.min(mY, mFloatViewMid + mFloatViewHeightHalf);
        int maxY = Math.max(mY, mFloatViewMid - mFloatViewHeightHalf);

        if (scrollDir == UP) {
            View v = mList.getChildAt(0);
            // Log.d("mobeta", "vtop="+v.getTop()+" padtop="+padTop);
            if (v == null) {
                mScrolling = false;
                return;
            } else {
                if (first == 0 && v.getTop() == padTop) {
                    mScrolling = false;
                    return;
                }
            }
            mScrollSpeed = mScrollProfile.getSpeed((mUpScrollStartYF - maxY)
                    / mDragUpScrollHeight, mPrevTime);
        } else {
            View v = mList.getChildAt(last - first);
            if (v == null) {
                mScrolling = false;
                return;
            } else {
                if (last == count - 1 && v.getBottom() <= listHeight + padTop) {
                    mScrolling = false;
                    return;
                }
            }
            mScrollSpeed = -mScrollProfile.getSpeed((minY - mDownScrollStartYF)
                    / mDragDownScrollHeight, mPrevTime);
        }

        mCurrTime = SystemClock.uptimeMillis();
        dt = (float) (mCurrTime - mPrevTime);

        // dy is change in View position of a list item; i.e. positive dy
        // means user is scrolling up (list item moves down the screen,
        // remember
        // y=0 is at top of View).
        dy = (int) Math.round(mScrollSpeed * dt);

        int movePos;
        if (dy >= 0) {
            dy = Math.min(listHeight, dy);
            movePos = first;
        } else {
            dy = Math.max(-listHeight, dy);
            movePos = last;
        }

        final View moveItem = mList.getChildAt(movePos - first);
        int top = moveItem.getTop() + dy;

        if (movePos == 0 && top > padTop) {
            top = padTop;
        }

        // always do scroll
        mBlockLayoutRequests = true;

        mList.setSelectionFromTop(movePos, top - padTop);
        mList.layoutChildren();
        mList.invalidate();

        mBlockLayoutRequests = false;

        // scroll means relative float View movement
        doDragFloatView(movePos, moveItem, false);

        mPrevTime = mCurrTime;

        mList.post(this);*/
    }
}
