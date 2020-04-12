package com.example.smartparentalapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.MenuItem;
import android.location.Location;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Locale;

public class LocationActivity extends AppCompatActivity {
    private FirebaseAuth dbAuth;
    private FusedLocationProviderClient fusedLocationClient;
    private Location currentLocation;
    private MenuHelper menuHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        menuHelper = new MenuHelper();
        dbAuth = FirebaseAuth.getInstance();
        FirebaseUser userSignedIn = dbAuth.getCurrentUser();

        //Location
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location);
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

    private void getLocation() {
        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        if(location.equals(currentLocation))
                        {
                            Toast.makeText(getApplicationContext(), "Location fetching failed. Make sure to have location activated", Toast.LENGTH_LONG).show();
                        }
                        if (location != null) {
                            currentLocation = location;
                            Toast.makeText(getApplicationContext(), "Location fetching success", Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }

    //Menu click listener
    private BottomNavigationView.OnNavigationItemSelectedListener onNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {
        public boolean onNavigationItemSelected(MenuItem item) {
            FirebaseUser userSignedIn = dbAuth.getCurrentUser();
            Intent intent = menuHelper.navigationMenu(item, userSignedIn, getApplicationContext());
            startActivityForResult(intent, 0);
            return true;
        }
    };
}
