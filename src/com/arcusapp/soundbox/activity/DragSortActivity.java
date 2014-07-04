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
    }
}