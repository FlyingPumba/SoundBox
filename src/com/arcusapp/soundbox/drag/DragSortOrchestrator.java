package com.arcusapp.soundbox.drag;

import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;

import java.util.ArrayList;
import java.util.List;

public class DragSortOrchestrator implements MultipleViewGestureDetector {

    private static final String TAG = "DragSortOrchestrator";

    private List<DragSortListView> mLists;
    private FloatViewManager mFloatViewManager;

    public DragSortOrchestrator(View rootView) {
        mLists = new ArrayList<DragSortListView>();

        //find all the DragSortListViews on the rootView and configure them
        exploreRootView(rootView);
        configureLists();
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

        //View v = mFloatViewManager.onCreateFloatView(view);
    }

    @Override
    public boolean onFling(DragSortListView list, MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        return false;
    }
}
