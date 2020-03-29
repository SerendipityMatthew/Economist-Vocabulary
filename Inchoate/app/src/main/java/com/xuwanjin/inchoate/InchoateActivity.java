package com.xuwanjin.inchoate;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class InchoateActivity extends AppCompatActivity
implements BottomNavigationView.OnNavigationItemSelectedListener{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setBackgroundColor(Color.WHITE);
        bottomNavigationView.setOnNavigationItemSelectedListener(this);
        View fragment = findViewById(R.id.nav_host_fragment);


    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        Log.d("Matthew", "onNavigationItemSelected: 0 ");
        if (item.getItemId() == R.id.item_today){
            Log.d("Matthew", "onNavigationItemSelected: 1 ");
        }else if (item.getItemId() == R.id.item_weekly){
            Log.d("Matthew", "onNavigationItemSelected: 2");
        }
        return false;
    }
}
