package com.example.smartparentalapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class ProfileActivity extends AppCompatActivity {
    private FirebaseAuth dbAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        dbAuth = FirebaseAuth.getInstance();
        FirebaseUser userSignedIn = dbAuth.getCurrentUser();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        getSupportActionBar().hide();

        // Buttons
        Button logoutButton = (Button) findViewById(R.id.logoutButton);

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

        //Button click listener
        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               signOut();
            }
        });
    }

    private void signOut() {
        dbAuth.signOut();
        updateUI(null);
    }

    public void updateUI(FirebaseUser account){
        if(account != null){
            Toast.makeText(this,"Log out failed",Toast.LENGTH_LONG).show();
        }else {
            Toast.makeText(this,"Logged out",Toast.LENGTH_LONG).show();
            startActivity(new Intent(this,  MainActivity.class));
        }
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
