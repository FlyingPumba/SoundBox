package com.arcusapp.soundbox.drag;

import android.util.Log;
import android.view.DragEvent;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

public class DragSortOrchestrator implements View.OnDragListener{

    private static final String TAG = "DragSortOrchestrator";

    private List<DragSortListView> lists;

    public DragSortOrchestrator(View rootView) {
        lists = new ArrayList<DragSortListView>();

        //find all the DragSortListViews on the rootView and configure them
        exploreRootView(rootView);
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
            dslv.setOnDragListener(this);
        }
    }

    /**
     * Called when a drag event is dispatched to a view. This allows listeners to get a
     * chance to override base View behavior.
     */
    @Override
    public boolean onDrag(View v, DragEvent event) {
        DragSortListView dslv = (DragSortListView) v;

        switch (event.getAction()) {
            case DragEvent.ACTION_DRAG_STARTED:
                Log.i(TAG, event.toString());
                break;
            case DragEvent.ACTION_DRAG_ENTERED:
                Log.i(TAG, event.toString());
                break;
            case DragEvent.ACTION_DRAG_LOCATION:
                Log.i(TAG, event.toString());
                break;
            case DragEvent.ACTION_DRAG_EXITED:
                Log.i(TAG, event.toString());
                break;
            case DragEvent.ACTION_DROP:
                Log.i(TAG, event.toString());
                break;
            case DragEvent.ACTION_DRAG_ENDED:
                Log.i(TAG, event.toString());
                break;
        }

        // true if the drag event was handled successfully,
        // or false if the drag event was not handled.
        // Note that false will trigger the View to call its onDragEvent() handler.
        return true;
    }
}
