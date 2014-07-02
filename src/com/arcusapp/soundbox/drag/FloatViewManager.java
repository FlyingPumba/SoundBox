package com.arcusapp.soundbox.drag;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Point;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

public class FloatViewManager {

    private Bitmap mFloatBitmap;

    private ImageView mImageView;

    private int mFloatBGColor = Color.BLACK;

    public void setBackgroundColor(int color) {
        mFloatBGColor = color;
    }

    /**
     * This simple implementation creates a Bitmap copy of the view
     */
    public View onCreateFloatView(View v) {
        if (v == null) {
            return null;
        }

        v.setPressed(false);

        // Create a copy of the drawing cache so that it does not get
        // recycled by the framework when the list tries to clean up memory
        //v.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);
        v.setDrawingCacheEnabled(true);
        mFloatBitmap = Bitmap.createBitmap(v.getDrawingCache());
        v.setDrawingCacheEnabled(false);

        if (mImageView == null) {
            mImageView = new ImageView(v.getContext());
        }
        mImageView.setBackgroundColor(mFloatBGColor);
        mImageView.setPadding(0, 0, 0, 0);
        mImageView.setImageBitmap(mFloatBitmap);
        mImageView.setLayoutParams(new ViewGroup.LayoutParams(v.getWidth(), v.getHeight()));

        return mImageView;
    }

    /**
     * This does nothing
     */
    public void onDragFloatView(View floatView, Point position, Point touch) {
        // do nothing
    }

    /**
     * Removes the Bitmap from the ImageView created in
     * onCreateFloatView() and tells the system to recycle it.
     */
    public void onDestroyFloatView(View floatView) {
        ((ImageView) floatView).setImageDrawable(null);

        mFloatBitmap.recycle();
        mFloatBitmap = null;
    }

}
