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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {
    private FirebaseAuth dbAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        dbAuth = FirebaseAuth.getInstance();
        FirebaseUser userSignedIn = dbAuth.getCurrentUser();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getSupportActionBar().hide();

        //Menu set click listener
        BottomNavigationView clickedMenuItem = findViewById(R.id.bottom_navigation);
        if(userSignedIn != null) {
            clickedMenuItem.getMenu().removeItem(R.id.loginPage);
        }
        else
        {
            clickedMenuItem.getMenu().removeItem(R.id.profilePage);
        }
        clickedMenuItem.setOnNavigationItemSelectedListener(onNavigationItemSelectedListener);
    }

    //Menu click listener
    private BottomNavigationView.OnNavigationItemSelectedListener onNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {
        public boolean onNavigationItemSelected(MenuItem item) {
            FirebaseUser userSignedIn = dbAuth.getCurrentUser();
            if(userSignedIn != null) {
                switch (item.getItemId()) {
                    case R.id.dashboardPage:
                        Intent dashboardIntent = new Intent(getApplicationContext(), MainActivity.class);
                        startActivityForResult(dashboardIntent, 0);
                        return true;
                    case R.id.locationPage:
                        Intent locationIntent = new Intent(getApplicationContext(), LocationActivity.class);
                        startActivityForResult(locationIntent, 0);
                        return true;
                    case R.id.profilePage:
                        Intent loginIntent = new Intent(getApplicationContext(), ProfileActivity.class);
                        startActivityForResult(loginIntent, 0);
                        return true;

                }
            }
            else {
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
            }
            return true;
        }
    };
}
