package com.arcusapp.soundbox.dragnsort.views;

import android.graphics.Point;
import android.view.View;
import android.widget.ListView;


/**
 * Interface for customization of the floating View appearance
 * and dragging behavior. Implement
 * your own and pass it to {@link #setFloatViewManager}. If
 * your own is not passed, the default {@link com.arcusapp.soundbox.dragnsort.SimpleFloatViewManager}
 * implementation is used.
 */
public interface FloatViewManager {
    /**
     * Return the floating View for item at <code>position</code>.
     * DragSortListView will measure and layout this View for you,
     * so feel free to just inflate it. You can help DSLV by
     * setting some {@link android.view.ViewGroup.LayoutParams} on this View;
     * otherwise it will set some for you (with a width of FILL_PARENT
     * and a height of WRAP_CONTENT).
     *
     * @param position Position of item to drag (NOTE:
     * <code>position</code> excludes header Views; thus, if you
     * want to call {@link android.widget.ListView#getChildAt(int)}, you will need
     * to add {@link android.widget.ListView#getHeaderViewsCount()} to the index).
     *
     * @return The View you wish to display as the floating View.
     */
    public View onCreateFloatView(ListView list, int position);

    /**
     * Called whenever the floating View is dragged. Float View
     * properties can be changed here. Also, the upcoming location
     * of the float View can be altered by setting
     * <code>location.x</code> and <code>location.y</code>.
     *
     * @param floatView The floating View.
     * @param location The location (top-left; relative to DSLV
     * top-left) at which the float
     * View would like to appear, given the current touch location
     * and the offset provided in {@link DragSortListView#startDrag}.
     * @param touch The current touch location (relative to DSLV
     * top-left).
     * @param pendingScroll
     */
    public void onDragFloatView(View floatView, Point location, Point touch);

    /**
     * Called when the float View is dropped; lets you perform
     * any necessary cleanup. The internal DSLV floating View
     * reference is set to null immediately after this is called.
     *
     * @param floatView The floating View passed to
     * {@link #onCreateFloatView(int)}.
     */
    public void onDestroyFloatView(View floatView);
}
