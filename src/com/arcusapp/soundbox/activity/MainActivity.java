package com.arcusapp.soundbox.activity;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;

import com.arcusapp.soundbox.R;
import com.arcusapp.soundbox.fragment.HomeFragment;

public class MainActivity extends SlidingPanelActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        HomeFragment contentFragment = new HomeFragment();

        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.contentFragmentContainer, contentFragment);
        transaction.commit();
    }
}
