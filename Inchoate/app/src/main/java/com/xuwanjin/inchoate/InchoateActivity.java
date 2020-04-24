package com.xuwanjin.inchoate;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.FragmentManager;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.bottomnavigation.LabelVisibilityMode;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;
import com.xuwanjin.inchoate.ui.playing.AudioPlayerFragment;

import java.util.List;
import java.util.concurrent.BlockingDeque;


public class InchoateActivity extends AppCompatActivity implements
        BottomNavigationView.OnNavigationItemSelectedListener,
        BottomNavigationController {
    NavController controller;
    BottomNavigationView bottomNavigationView;
    ConstraintLayout mConstraintLayout;
    FrameLayout nowPlayingControl;
    public SlidingUpPanelLayout slidingUpPanelLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mConstraintLayout = findViewById(R.id.main_activity);
        bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setBackgroundColor(Color.WHITE);
        bottomNavigationView.setOnNavigationItemSelectedListener(this);
        bottomNavigationView.setLabelVisibilityMode(LabelVisibilityMode.LABEL_VISIBILITY_LABELED);
        controller = Navigation.findNavController(this, R.id.nav_host_fragment);
        slidingUpPanelLayout = findViewById(R.id.slide_layout);
        InchoateApplication.NAVIGATION_CONTROLLER = controller;
        nowPlayingControl = findViewById(R.id.now_playing_control);
        AudioPlayerFragment audioPlayerFragment = new AudioPlayerFragment();
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager
                .beginTransaction()
                .replace(R.id.now_playing_control, audioPlayerFragment)
                .commitAllowingStateLoss();
        slidingUpPanelLayout.setPanelHeight(55);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.item_today) {
            Utils.navigationController(controller, R.id.navigation_today);
        } else if (item.getItemId() == R.id.item_weekly) {
            Utils.navigationController(controller, R.id.navigation_weekly);
        } else if (item.getItemId() == R.id.item_bookmark) {
            Utils.navigationController(controller, R.id.navigation_bookmark);
        } else if (item.getItemId() == R.id.item_setting) {
            Utils.navigationController(controller, R.id.navigation_settings);
        }

        return false;
    }

    @Override
    public void isShowBottomNavigation(boolean isShow) {
        Log.d("Matthew", "isShowBottomNavigation: isShow = " + isShow);
        if (isShow) {
            bottomNavigationView = findViewById(R.id.bottom_navigation);
            bottomNavigationView.setVisibility(View.VISIBLE);
            bottomNavigationView.setBackgroundColor(Color.WHITE);
            bottomNavigationView.setOnNavigationItemSelectedListener(this);
            bottomNavigationView.setLabelVisibilityMode(LabelVisibilityMode.LABEL_VISIBILITY_LABELED);

        } else {
            bottomNavigationView.removeAllViews();
        }
    }
}
