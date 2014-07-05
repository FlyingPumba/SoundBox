package com.arcusapp.soundbox.drag;

public interface DragSortListener {
    public void onDragStarted(DragSortListView originList, int position);

    public void onDragFinished(DragSortListView targetList, int position);
}
