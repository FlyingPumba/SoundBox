package com.arcusapp.soundbox.drag;

import android.view.MotionEvent;

public interface MultipleViewGestureDetector {

    public boolean onDown(DragSortListView list, MotionEvent e);

    public void onUp(DragSortListView list, MotionEvent e);

    public void onDrag(DragSortListView list, MotionEvent e);

    public void onShowPress(DragSortListView list, MotionEvent e);

    public boolean onSingleTapUp(DragSortListView list, MotionEvent e);

    public boolean onScroll(DragSortListView list, MotionEvent e1, MotionEvent e2, float distanceX, float distanceY);

    public void onLongPress(DragSortListView list, MotionEvent e);

    public boolean onFling(DragSortListView list, MotionEvent e1, MotionEvent e2, float velocityX, float velocityY);
}
