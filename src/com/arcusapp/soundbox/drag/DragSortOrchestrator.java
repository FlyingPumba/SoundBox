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

    public void refreshFloatViewPosition() {
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

    private int viewIdHitPosition(DragSortListView mDslv, MotionEvent ev) {
        final int x = (int) ev.getX();
        final int y = (int) ev.getY();

        int touchPos = mDslv.pointToPosition(x, y); // includes headers/footers

        final int numHeaders = mDslv.getHeaderViewsCount();
        final int numFooters = mDslv.getFooterViewsCount();
        final int count = mDslv.getCount();

        // We're only interested if the touch was on an
        // item that's not a header or footer.
        if (touchPos != AdapterView.INVALID_POSITION && touchPos >= numHeaders
                && touchPos < (count - numFooters)) {
            return touchPos;
        }

        return MISS;
    }

    @Override
    public boolean onDown(DragSortListView list, MotionEvent e) {
        return false;
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
        int position = hittedPosition - list.getHeaderViewsCount();

        View child = list.getChildAt(position + list.getHeaderViewsCount() - list.getFirstVisiblePosition());

        mFloatView = mFloatViewManager.onCreateFloatView(child);
        mFloatLoc.x = (int) e.getRawX();
        mFloatLoc.y = (int) e.getRawY();

        mDragging = true;
        if(mListener != null){
            mListener.onDragStarted(list, hittedPosition);
        }

        mRootView.invalidate();
    }

    @Override
    public boolean onFling(DragSortListView list, MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        return false;
    }
}
