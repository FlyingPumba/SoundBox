package com.arcusapp.soundbox.drag;


import android.view.View;

/**
 * Centers floating View under touch point.
 */
public class LiftAnimator extends SmoothAnimator {

    private float mInitDragDeltaY;
    private float mFinalDragDeltaY;

    public LiftAnimator(DragSortOrchestrator orchestrator, float smoothness, int duration) {
        super(orchestrator, smoothness, duration);
    }

    /*@Override
    public void onStart() {
        mInitDragDeltaY = mDragDeltaY;
        mFinalDragDeltaY = mFloatViewHeightHalf;
    }

    @Override
    public void onUpdate(float frac, float smoothFrac) {
        if (mDragState != DRAGGING) {
            cancel();
        } else {
            mDragDeltaY = (int) (smoothFrac * mFinalDragDeltaY + (1f - smoothFrac)
                    * mInitDragDeltaY);
            mFloatLoc.y = mY - mDragDeltaY;
            doDragFloatView(true);
        }
    }

    private void doDragFloatView(boolean forceInvalidate) {
        int movePos = getFirstVisiblePosition() + getChildCount() / 2;
        View moveItem = getChildAt(getChildCount() / 2);

        if (moveItem == null) {
            return;
        }

        doDragFloatView(movePos, moveItem, forceInvalidate);
    }

    private void doDragFloatView(int movePos, View moveItem, boolean forceInvalidate) {
        mBlockLayoutRequests = true;

        updateFloatView();

        int oldFirstExpPos = mFirstExpPos;
        int oldSecondExpPos = mSecondExpPos;

        boolean updated = updatePositions();

        if (updated) {
            adjustAllItems();
            int scroll = adjustScroll(movePos, moveItem, oldFirstExpPos, oldSecondExpPos);
            // Log.d("mobeta", "  adjust scroll="+scroll);

            setSelectionFromTop(movePos, moveItem.getTop() + scroll - getPaddingTop());
            layoutChildren();
        }

        if (updated || forceInvalidate) {
            invalidate();
        }

        mBlockLayoutRequests = false;
    }*/
}
