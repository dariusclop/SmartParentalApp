package com.example.smartparentalapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Button;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        BottomNavigationView clickedMenuItem = findViewById(R.id.bottom_navigation);
        clickedMenuItem.setOnNavigationItemSelectedListener(onNavigationItemSelectedListener);
    }
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
