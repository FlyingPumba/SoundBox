package com.arcusapp.soundbox.activity;

import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;

import com.arcusapp.soundbox.drag.DragSortOrchestrator;

public class DragSortActivity extends SlidingPanelActivity {

    DragSortOrchestrator mOrchestrator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // wait for the content to be displayed and then initializate the orchestrator
        final View rootView = ((ViewGroup)findViewById(android.R.id.content)).getChildAt(0);
        ViewTreeObserver observer = rootView.getViewTreeObserver();
        observer.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {

            @Override
            public void onGlobalLayout() {
                rootView.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                mOrchestrator = new DragSortOrchestrator(rootView);
            }
        });
    }
}