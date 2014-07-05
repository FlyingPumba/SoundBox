package com.arcusapp.soundbox.drag;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.LinearLayout;

public class DragSortRootView extends LinearLayout {

    private DragSortOrchestrator mOrchestrator;

    public DragSortRootView(Context context) {
        super(context);
    }

    public DragSortRootView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public DragSortRootView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();

        mOrchestrator = new DragSortOrchestrator(this);
    }

    public void invalidateChildViews() {
        if(mOrchestrator != null) {
            mOrchestrator.invalidateChildViews();
        }
    }

    public void drawFromViewToCanvas(final View view, final Rect rect, final Canvas canvas) {
        final int widthSpec = View.MeasureSpec.makeMeasureSpec(rect.width(), View.MeasureSpec.EXACTLY);
        final int heightSpec = View.MeasureSpec.makeMeasureSpec(rect.height(), View.MeasureSpec.EXACTLY);
        view.measure(widthSpec, heightSpec);
        // Lay the view out with the known dimensions
        view.layout(0, 0, rect.width(), rect.height());
        // Translate the canvas so the view is drawn at the proper coordinates
        canvas.save();
        canvas.translate(rect.left, rect.top);
        // Draw the View and clear the translation
        view.draw(canvas);
        canvas.restore();
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        super.dispatchDraw(canvas);
        if(mOrchestrator.mFloatView != null) {
            // draw the float view over everything
            ViewGroup.LayoutParams lp = mOrchestrator.mFloatView.getLayoutParams();
            if (lp == null) {
                lp = new AbsListView.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                mOrchestrator.mFloatView.setLayoutParams(lp);
            }

            int x = mOrchestrator.mFloatLoc.x;
            int y = mOrchestrator.mFloatLoc.y;
            int w = lp.width;
            int h = lp.height;

            Rect rect = new Rect(x-w/2,y-h,x+w/2,y);

            drawFromViewToCanvas(mOrchestrator.mFloatView, rect, canvas);
        }
    }
}
