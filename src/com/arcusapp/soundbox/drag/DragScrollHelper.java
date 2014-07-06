package com.arcusapp.soundbox.drag;

import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

public class DragScrollHelper {
    private static final String TAG = "DragScrollHelper";

    /*
      Determines the start of the upward drag-scroll region
      at the top of the ListView. Specified by a fraction
      of the ListView height, thus screen resolution agnostic.
     */
    static float mDragUpScrollStartFrac = 1.0f / 3.0f;

    /*
      Determines the start of the downward drag-scroll region
      at the bottom of the ListView. Specified by a fraction
      of the ListView height, thus screen resolution agnostic.
     */
    static float mDragDownScrollStartFrac = 1.0f - 1.0f / 3.0f;

    public static void scrollList(DragSortListView list, View floatView, MotionEvent ev) {
        // assuming that the float view is now in the coordinates of the MotionEvent
        // we are going to attempt to scroll the list according to the relative position of
        // this float view inside the list.

        // first of all, determine the relative position of the float view inside the list
        int[] listPos = new int[2];
        list.getLocationOnScreen(listPos);

        int relativeY = (int) ev.getRawY() - listPos[1];

        // calculate the Y position where we start to scroll up or down
        final int listHeight = list.getHeight() - list.getPaddingTop() - list.getPaddingBottom();

        int upScrollStartY = (int) (list.getPaddingTop() + mDragUpScrollStartFrac * listHeight);
        int downScrollStartY = (int) (list.getPaddingTop() + mDragDownScrollStartFrac * listHeight);

        // check if we are going to scroll up or down
        if (relativeY <= upScrollStartY) {
            Log.i(TAG, "we have to scroll up");
            // determine the velocity of scroll according to how close we are from the top
            int deltaY = relativeY;
            int speed = listHeight / deltaY;

            // get the top Y of the first visible item
            int first = list.getFirstVisiblePosition();
            View firstView = list.getChildAt(first);
            if (firstView == null) {
                firstView = list.getChildAt(0);
            }
            int topY = firstView.getTop();

            // calculate the new top Y
            int newTopY = topY - speed;
            if(newTopY > 0) {
                list.setSelectionFromTop(first, newTopY);
            } else {
                list.setSelection(first);
            }


        } else if (relativeY >= downScrollStartY) {
            Log.i(TAG, "we have to scroll down");
            // determine the velocity of scroll according to how close we are from the bottom
            int deltaY = listHeight - relativeY;
            int speed = listHeight / deltaY;

            // get the top Y of the last visible item
            int last = list.getLastVisiblePosition();
            View lastView = list.getChildAt(last);
            if (lastView == null) {
                lastView = list.getChildAt(list.getChildCount() -1);
            }
            int topY = lastView.getTop();

            // calculate the new top Y
            int newTopY = topY + speed;
            if(newTopY < listHeight) {
                list.setSelectionFromTop(last, newTopY);
            } else {
                list.setSelection(last);
            }

        }

        list.invalidate();
    }
}
