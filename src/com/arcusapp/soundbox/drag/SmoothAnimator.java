package com.arcusapp.soundbox.drag;

import android.os.SystemClock;

public class SmoothAnimator implements Runnable {
    protected long mStartTime;

    private float mDurationF;

    private float mAlpha;
    private float mA, mB, mC, mD;

    private boolean mCanceled;

    protected DragSortOrchestrator mOrchestrator;

    public SmoothAnimator(DragSortOrchestrator orchestrator, float smoothness, int duration) {
        mOrchestrator = orchestrator;
        mAlpha = smoothness;
        mDurationF = (float) duration;
        mA = mD = 1f / (2f * mAlpha * (1f - mAlpha));
        mB = mAlpha / (2f * (mAlpha - 1f));
        mC = 1f / (1f - mAlpha);
    }

    public float transform(float frac) {
        if (frac < mAlpha) {
            return mA * frac * frac;
        } else if (frac < 1f - mAlpha) {
            return mB + mC * frac;
        } else {
            return 1f - mD * (frac - 1f) * (frac - 1f);
        }
    }

    public void start() {
        mStartTime = SystemClock.uptimeMillis();
        mCanceled = false;
        onStart();
        mOrchestrator.getRootView().post(this);
    }

    public void cancel() {
        mCanceled = true;
    }

    public void onStart() {
        // stub
    }

    public void onUpdate(float frac, float smoothFrac) {
        // stub
    }

    public void onStop() {
        // stub
    }

    @Override
    public void run() {
        if (mCanceled) {
            return;
        }

        float fraction = ((float) (SystemClock.uptimeMillis() - mStartTime)) / mDurationF;

        if (fraction >= 1f) {
            onUpdate(1f, 1f);
            onStop();
        } else {
            onUpdate(fraction, transform(fraction));
            mOrchestrator.getRootView().post(this);
        }
    }
}
