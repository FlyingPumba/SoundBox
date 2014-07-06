package com.arcusapp.soundbox.drag;

import android.graphics.Point;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;

import java.util.ArrayList;
import java.util.List;

public class DragSortOrchestrator implements MultipleViewGestureDetector {

    private static final String TAG = "DragSortOrchestrator";
    private static final int MISS = -1;

    private DragSortRootView mRootView;
    private List<DragSortListView> mLists;
    private FloatViewManager mFloatViewManager;

    private DragSortListener mListener;

    public View mFloatView;
    public Point mFloatLoc = new Point();
    public int mFloatViewHeight;
    public int mFloatViewWidth;

    public boolean mDragging = false;

    public DragSortOrchestrator(DragSortRootView rootView) {
        mRootView = rootView;
        mFloatViewManager = new FloatViewManager();

        invalidateChildViews();
    }

    public void invalidateChildViews() {
        //find all the DragSortListViews on the RootView and configure them
        mLists = new ArrayList<DragSortListView>();
        exploreRootView(mRootView);
        configureLists();
    }

    public void refreshFloatViewPosition(int x, int y) {
        mFloatLoc.x = x;
        mFloatLoc.y = y;
        mRootView.invalidate();
    }

    public void setDragSortListener(DragSortListener listener) {
        mListener = listener;
    }

    public void removeDragSortListener() {
        mListener = null;
    }

    private void exploreRootView(View rootView) {
        if (rootView instanceof DragSortListView) {
            mLists.add((DragSortListView) rootView);
        } else if (rootView instanceof ViewGroup) {
            for (int i = 0; i < ((ViewGroup) rootView).getChildCount(); i++) {
                exploreRootView(((ViewGroup) rootView).getChildAt(i));
            }
        }
    }

    private void configureLists() {
        for(DragSortListView dslv : mLists) {
            dslv.setGestureDetectorOrchestrator(this);
        }
    }

    private int viewIdHitPosition(DragSortListView list, MotionEvent ev) {
        final int x = (int) ev.getX();
        final int y = (int) ev.getY();

        int touchPos = list.pointToPosition(x, y); // includes headers/footers

        final int numHeaders = list.getHeaderViewsCount();
        final int numFooters = list.getFooterViewsCount();
        final int count = list.getCount();

        // We're only interested if the touch was on an
        // item that's not a header or footer.
        if (touchPos != AdapterView.INVALID_POSITION && touchPos >= numHeaders
                && touchPos < (count - numFooters)) {
            return touchPos;
        }

        return MISS;
    }

    private boolean inRegion(float x, float y, View v) {
        int[] loc = new int[2];
        v.getLocationOnScreen(loc);

        return loc[0] + v.getWidth() > x &&    // right edge
                loc[1] + v.getHeight() > y &&   // bottom edge
                loc[0] < x &&                   // left edge
                loc[1] < y;                     // top edge
    }

    @Override
    public boolean onDown(DragSortListView list, MotionEvent e) {
        return false;
    }

    @Override
    public void onUp(DragSortListView origin, MotionEvent e) {
        Log.i(TAG, "onUp");

        // if we were dragging, finish it and check if we can drop
        if(mDragging) {

            for(DragSortListView target : mLists) {
                // check if we can drop in target
                if(target.isDropEnabled()) {
                    //check if we are droppping in a valid position
                    int dropPosition = viewIdHitPosition(target, e);
                    if(dropPosition != MISS) {
                        // yeah ! dropping from origin list to target list
                        if(mListener != null) {
                            mListener.onDragFinished(target, dropPosition);
                            break;
                        }
                    }
                }
            }

            mDragging = false;
            mRootView.invalidate();
        }
    }

    @Override
    public void onDrag(DragSortListView list, MotionEvent e) {
        Log.i(TAG, "onDrag");
        refreshFloatViewPosition((int) e.getRawX(), (int) e.getRawY());

        // check if we are above a droppable list, and thus can be scrolled
        for(DragSortListView target : mLists) {
            if(target.isDropEnabled()) {
                boolean inside = inRegion(e.getRawX(), e.getRawY(), target);
                if(inside) {
                    Log.i(TAG, "found a list to scroll");
                    DragScrollHelper.scrollList(target, mFloatView, e);
                }
            }
        }
    }

    @Override
    public void onShowPress(DragSortListView list, MotionEvent e) {

    }

    @Override
    public boolean onSingleTapUp(DragSortListView list, MotionEvent e) {
        return false;
    }

    @Override
    public boolean onScroll(DragSortListView list, MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
        return false;
    }

    @Override
    public void onLongPress(DragSortListView list, MotionEvent e) {
        Log.i(TAG, "onLongPress");

        if(!list.isDragEnabled()){
            return;
        }

        int hittedPosition = viewIdHitPosition(list, e);
        if(hittedPosition != MISS){

            // get hitted child
            View child = list.getChildAt(hittedPosition - list.getFirstVisiblePosition());

            // create float view of the child
            mFloatView = mFloatViewManager.onCreateFloatView(child);

            // store touch position
            mFloatLoc.x = (int) e.getRawX();
            mFloatLoc.y = (int) e.getRawY();

            // measure and store height and width of the float view

            ViewGroup.LayoutParams lp = mFloatView.getLayoutParams();
            if (lp == null) {
                lp = new AbsListView.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                mFloatView.setLayoutParams(lp);
            }

            mFloatViewHeight = lp.height;
            mFloatViewWidth = lp.width;

            mDragging = true;
            if(mListener != null){
                mListener.onDragStarted(list, hittedPosition);
            }

            mRootView.invalidate();
        }
    }

    @Override
    public boolean onFling(DragSortListView list, MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        return false;
    }
}
