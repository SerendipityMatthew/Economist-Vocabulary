package com.xuwanjin.inchoate;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentContainerView;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.bottomnavigation.LabelVisibilityMode;

public class InchoateActivity extends AppCompatActivity
implements BottomNavigationView.OnNavigationItemSelectedListener{
    NavController controller;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setBackgroundColor(Color.WHITE);
        bottomNavigationView.setOnNavigationItemSelectedListener(this);
        bottomNavigationView.setLabelVisibilityMode(LabelVisibilityMode.LABEL_VISIBILITY_LABELED);
        controller = Navigation.findNavController(this, R.id.nav_host_fragment);
        InchoateApplication.NAVIGATION_CONTROLLER = controller;
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.item_today){
//            controller.navigate(R.id.navigation_today);
            Utils.navigationControllerUtils(controller, R.id.navigation_today);
        }else if (item.getItemId() == R.id.item_weekly){
//           controller.navigate(R.id.navigation_weekly);
            Utils.navigationControllerUtils(controller, R.id.navigation_weekly);
        }else if (item.getItemId() == R.id.item_bookmark){
//            controller.navigate(R.id.navigation_bookmark);
            Utils.navigationControllerUtils(controller, R.id.navigation_bookmark);
        } else if (item.getItemId() == R.id.item_setting){
//            controller.navigate(R.id.navigation_settings);
            Utils.navigationControllerUtils(controller, R.id.navigation_settings);
        }

        return false;
    }
}
