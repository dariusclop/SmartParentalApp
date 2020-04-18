package com.example.smartparentalapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.view.MenuItem;
import android.location.Location;
import android.widget.Toast;

import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LocationActivity extends AppCompatActivity {
    protected static final int REQUEST_CHECK_SETTINGS = 0x1;
    protected static final int PERMISSION_REQUEST_CODE = 0x1111;
    private boolean isLocationActivated = false;
    private FirebaseAuth dbAuth;
    private FusedLocationProviderClient fusedLocationClient;
    private Location currentLocation;
    private MenuHelper menuHelper;
    private LocationRequest locationRequest;
    private LocationCallback locationCallback;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location);
        getSupportActionBar().hide();

        menuHelper = new MenuHelper();
        dbAuth = FirebaseAuth.getInstance();
        FirebaseUser userSignedIn = dbAuth.getCurrentUser();

        //Location
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        locationRequest = LocationRequest.create();

        //Location Callback function
        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                if (locationResult == null) {
                    Toast.makeText(getApplicationContext(), "In Callback function", Toast.LENGTH_LONG).show();
                    return;
                }
                for (Location location : locationResult.getLocations()) {
                    // Update UI with location data
                    // ...
                    Toast.makeText(getApplicationContext(), "In Callback function", Toast.LENGTH_LONG).show();
                }
            }
        };

        //Menu set click listener
        BottomNavigationView clickedMenuItem = findViewById(R.id.bottom_navigation);
        if(userSignedIn != null) {
            clickedMenuItem.getMenu().removeItem(R.id.loginPage);
        }
        else {
            clickedMenuItem.getMenu().removeItem(R.id.profilePage);
        }
        clickedMenuItem.setOnNavigationItemSelectedListener(onNavigationItemSelectedListener);
    }

    @Override
    protected void onStart() {
        super.onStart();
        checkPermissions();
        createLocationRequest();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(isLocationActivated) {
            startLocationUpdates();
        }
    }

    private void checkPermissions() {
        boolean permissionAccessFineLocationApproved =
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                        == PackageManager.PERMISSION_GRANTED;

        if (permissionAccessFineLocationApproved) {
            boolean backgroundLocationPermissionApproved =
                    ActivityCompat.checkSelfPermission(this,
                            Manifest.permission.ACCESS_BACKGROUND_LOCATION)
                            == PackageManager.PERMISSION_GRANTED;

            if (backgroundLocationPermissionApproved) {
                // App can access location both in the foreground and in the background.
                // Start your service that doesn't have a foreground service type
                // defined.
            } else {
                ActivityCompat.requestPermissions(this, new String[] {
                                Manifest.permission.ACCESS_BACKGROUND_LOCATION},
                        PERMISSION_REQUEST_CODE);
            }
        } else {
            // App doesn't have access to the device's location at all. Make full request
            // for permission.
            ActivityCompat.requestPermissions(this, new String[] {
                            Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.ACCESS_BACKGROUND_LOCATION
                    },
                    PERMISSION_REQUEST_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(Build.VERSION.SDK_INT >= 29 && ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_BACKGROUND_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            new AlertDialog.Builder(this)
                    .setTitle("No background location provided")
                    .setMessage("The application must have all-the-time access to location in order to function properly. Do you want to enable them?")
                    .setNegativeButton(R.string.noButton, null)
                    .setPositiveButton(R.string.yesButton, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialoginterface, int i) {
                            checkPermissions();
                        }
                    })
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .show();
        }
    }

    private void getLastLocation() {
        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        if (location != null) {
                            currentLocation = location;
                        }
                    }
                });
    }

    protected void createLocationRequest() {
        locationRequest.setInterval(5000);
        locationRequest.setFastestInterval(3000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(locationRequest);

        SettingsClient client = LocationServices.getSettingsClient(this);
        Task<LocationSettingsResponse> task = client.checkLocationSettings(builder.build());

        task.addOnSuccessListener(this, new OnSuccessListener<LocationSettingsResponse>() {
            @Override
            public void onSuccess(LocationSettingsResponse locationSettingsResponse) {
                if(currentLocation == null) {
                    //get a starting point for the location
                    getLastLocation();
                }
                isLocationActivated = true;
            }
        });

        task.addOnFailureListener(this, new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                if (e instanceof ResolvableApiException) {
                    Toast.makeText(getApplicationContext(), "Location fetching failed. Make sure to have location activated", Toast.LENGTH_LONG).show();
                    isLocationActivated = false;
                    try {
                        ResolvableApiException resolvable = (ResolvableApiException) e;
                        resolvable.startResolutionForResult(LocationActivity.this,
                                REQUEST_CHECK_SETTINGS);
                    } catch (IntentSender.SendIntentException sendEx) {
                        sendEx.printStackTrace();
                    }
                }
            }
        });
    }

    private void startLocationUpdates() {
        fusedLocationClient.requestLocationUpdates(locationRequest,
                locationCallback,
                Looper.getMainLooper());
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
