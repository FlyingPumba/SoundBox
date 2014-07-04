package com.arcusapp.soundbox.drag;

import android.content.Context;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ListView;

/**
 * List view that inform to the Orchestrator of the gestures applied to itself
 */
public class DragSortListView extends ListView {

    private MultipleViewGestureDetector gestureOrchestrator;
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

    public void setGestureDetectorOrchestrator(MultipleViewGestureDetector detector) {
        gestureOrchestrator = detector;
    }
    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        auxiliarGestureDetector.onTouchEvent(ev);
        return super.onTouchEvent(ev);
    }

    private GestureDetector.OnGestureListener gestureListener = new GestureDetector.OnGestureListener() {
        @Override
        public boolean onDown(MotionEvent e) {
            if(gestureOrchestrator != null) {
                return gestureOrchestrator.onDown(DragSortListView.this, e);
            } else {
                return false;
            }
        }

        @Override
        public void onShowPress(MotionEvent e) {
            if(gestureOrchestrator != null) {
                gestureOrchestrator.onShowPress(DragSortListView.this, e);
            }
        }

        @Override
        public boolean onSingleTapUp(MotionEvent e) {
            if(gestureOrchestrator != null) {
                return gestureOrchestrator.onSingleTapUp(DragSortListView.this, e);
            } else {
                return false;
            }
        }

        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
            if(gestureOrchestrator != null) {
                return gestureOrchestrator.onScroll(DragSortListView.this, e1, e2, distanceX, distanceY);
            } else {
                return false;
            }
        }

        @Override
        public void onLongPress(MotionEvent e) {
            if(gestureOrchestrator != null) {
                gestureOrchestrator.onLongPress(DragSortListView.this, e);
            }
        }

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            if(gestureOrchestrator != null) {
                return gestureOrchestrator.onFling(DragSortListView.this, e1, e2, velocityX, velocityY);
            } else {
                return false;
            }
        }
    };
}
