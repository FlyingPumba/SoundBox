package com.arcusapp.soundbox.drag;

import android.content.Context;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.widget.ListView;

/**
 * List view that inform to the Orchestrator of the gestures applied to itself
 */
public class DragSortListView extends ListView {

    private DragSortOrchestrator mOrchestrator;
    private GestureDetector auxiliarGestureDetector;

    public DragSortListView(Context context) {
        super(context);
        auxiliarGestureDetector = new GestureDetector(context, gestureListener);
    }

    public DragSortListView(Context context, AttributeSet attrs) {
        super(context, attrs);
        auxiliarGestureDetector = new GestureDetector(context, gestureListener);
    }

    public DragSortListView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        auxiliarGestureDetector = new GestureDetector(context, gestureListener);
    }

    public void setGestureDetectorOrchestrator(DragSortOrchestrator detector) {
        mOrchestrator = detector;
    }
    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        if(mOrchestrator != null && mOrchestrator.mDragging) {
            onDragTouchEvent(ev);
            return true;
        } else {
            auxiliarGestureDetector.onTouchEvent(ev);
            return super.onTouchEvent(ev);
        }
    }

    private void onDragTouchEvent(MotionEvent ev) {
        mOrchestrator.mFloatLoc.x = (int) ev.getRawX();
        mOrchestrator.mFloatLoc.y = (int) ev.getRawY();
        mOrchestrator.refreshFloatViewPosition();
    }

    private GestureDetector.OnGestureListener gestureListener = new GestureDetector.OnGestureListener() {
        @Override
        public boolean onDown(MotionEvent e) {
            if(mOrchestrator != null) {
                return mOrchestrator.onDown(DragSortListView.this, e);
            } else {
                return false;
            }
        }

        @Override
        public void onShowPress(MotionEvent e) {
            if(mOrchestrator != null) {
                mOrchestrator.onShowPress(DragSortListView.this, e);
            }
        }

        @Override
        public boolean onSingleTapUp(MotionEvent e) {
            if(mOrchestrator != null) {
                return mOrchestrator.onSingleTapUp(DragSortListView.this, e);
            } else {
                return false;
            }
        }

        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
            if(mOrchestrator != null) {
                return mOrchestrator.onScroll(DragSortListView.this, e1, e2, distanceX, distanceY);
            } else {
                return false;
            }
        }

        @Override
        public void onLongPress(MotionEvent e) {
            if(mOrchestrator != null) {
                mOrchestrator.onLongPress(DragSortListView.this, e);
            }
        }

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            if(mOrchestrator != null) {
                return mOrchestrator.onFling(DragSortListView.this, e1, e2, velocityX, velocityY);
            } else {
                return false;
            }
        }
    };
}
