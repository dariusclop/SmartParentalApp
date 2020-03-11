package com.example.smartparentalapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class LocationActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location);
        getSupportActionBar().hide();

        //Menu set click listener
        BottomNavigationView clickedMenuItem = findViewById(R.id.bottom_navigation);
        clickedMenuItem.setOnNavigationItemSelectedListener(onNavigationItemSelectedListener);
    }

    //Menu click listener
    private BottomNavigationView.OnNavigationItemSelectedListener onNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {
        public boolean onNavigationItemSelected(MenuItem item) {
            switch (item.getItemId()) {
                case R.id.dashboardPage:
                    Intent dashboardIntent = new Intent(getApplicationContext(), MainActivity.class);
                    startActivityForResult(dashboardIntent, 0);
                    return true;
                case R.id.locationPage:
                    Intent locationIntent = new Intent(getApplicationContext(), LocationActivity.class);
                    startActivityForResult(locationIntent, 0);
                    return true;
                case R.id.loginPage:
                    Intent loginIntent = new Intent(getApplicationContext(), LoginActivity.class);
                    startActivityForResult(loginIntent, 0);
                    return true;

            }
            return true;
        }
    };
}
