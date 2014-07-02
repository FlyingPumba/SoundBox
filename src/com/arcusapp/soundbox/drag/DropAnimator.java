package com.arcusapp.soundbox.drag;

import android.view.View;

/**
 * Centers floating View over drop slot before destroying.
 */
public class DropAnimator extends SmoothAnimator {

    private int mDropPos;
    private int srcPos;
    private float mInitDeltaY;
    private float mInitDeltaX;

    public DropAnimator(DragSortOrchestrator orchestrator, float smoothness, int duration) {
        super(orchestrator, smoothness, duration);
    }

    /*@Override
    public void onStart() {
        mDropPos = mFloatPos;
        srcPos = mSrcPos;
        mDragState = DROPPING;
        mInitDeltaY = mFloatLoc.y - getTargetY();
        mInitDeltaX = mFloatLoc.x - getPaddingLeft();
    }

    private int getTargetY() {
        final int first = getFirstVisiblePosition();
        final int otherAdjust = (mItemHeightCollapsed + getDividerHeight()) / 2;
        View v = getChildAt(mDropPos - first);
        int targetY = -1;
        if (v != null) {
            if (mDropPos == srcPos) {
                targetY = v.getTop();
            } else if (mDropPos < srcPos) {
                // expanded down
                targetY = v.getTop() - otherAdjust;
            } else {
                // expanded up
                targetY = v.getBottom() + otherAdjust - mFloatViewHeight;
            }
        } else {
            // drop position is not on screen?? no animation
            cancel();
        }

        return targetY;
    }

    @Override
    public void onUpdate(float frac, float smoothFrac) {
        final int targetY = getTargetY();
        final int targetX = getPaddingLeft();
        final float deltaY = mFloatLoc.y - targetY;
        final float deltaX = mFloatLoc.x - targetX;
        final float f = 1f - smoothFrac;
        if (f < Math.abs(deltaY / mInitDeltaY) || f < Math.abs(deltaX / mInitDeltaX)) {
            mFloatLoc.y = targetY + (int) (mInitDeltaY * f);
            mFloatLoc.x = getPaddingLeft() + (int) (mInitDeltaX * f);
            doDragFloatView(true);
        }
    }

    @Override
    public void onStop() {
        dropFloatView();
    }*/

}
