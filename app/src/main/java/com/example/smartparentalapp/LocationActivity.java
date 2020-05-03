package com.example.smartparentalapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.IntentSender;
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
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.concurrent.atomic.AtomicReference;

public class LocationActivity extends AppCompatActivity implements OnMapReadyCallback {
    protected static final int REQUEST_CHECK_SETTINGS = 0x1;
    private FirebaseAuth dbAuth;
    private FusedLocationProviderClient fusedLocationClient;
    private Location currentLocation;
    private double currentLocationLatitude;
    private double currentLocationLongitude;
    private MenuHelper menuHelper;
    private GoogleMap mMap;
    private SupportMapFragment mapFragment;
    private Parent currentParent;
    private Child currentChild;
    private FirebaseFirestore fStore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location);
        getSupportActionBar().hide();

        menuHelper = new MenuHelper();
        dbAuth = FirebaseAuth.getInstance();
        FirebaseUser userSignedIn = dbAuth.getCurrentUser();
        fStore = FirebaseFirestore.getInstance();

        //Menu set click listener
        BottomNavigationView clickedMenuItem = findViewById(R.id.bottom_navigation);
        if(userSignedIn != null) {
            clickedMenuItem.getMenu().removeItem(R.id.loginPage);
        }
        else {
            clickedMenuItem.getMenu().removeItem(R.id.profilePage);
        }
        clickedMenuItem.setOnNavigationItemSelectedListener(onNavigationItemSelectedListener);

        //Add maps fragment
        mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        final FirebaseUser currentUser = dbAuth.getCurrentUser();
        if(currentUser != null) {
            fStore.collection("parents").document(currentUser.getUid()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if(task.getResult().exists()) {
                        ParentHelper parentHelper = new ParentHelper(null);
                        final String userUid = currentUser.getUid();
                        final AtomicReference<Parent> currentParentReference = new AtomicReference<>(null);
                        parentHelper.findParentById(userUid, currentParentReference, new ParentFinderCallback() {
                            @Override
                            public void onCallback() {
                                if(currentParentReference.get() != null) {
                                    currentParent = currentParentReference.get();
                                    currentChild = null;
                                }
                            }
                        });
                    }
                    else {
                        ChildHelper childHelper = new ChildHelper(null);
                        final String userUid = currentUser.getUid();
                        final AtomicReference<Child> currentChildReference = new AtomicReference<>();
                        childHelper.findChildById(userUid, currentChildReference, new ChildFinderCallback() {
                            @Override
                            public void onCallback() {
                                if(currentChildReference.get() != null) {
                                    currentChild = currentChildReference.get();
                                    currentParent = null;
                                }
                            }
                        });
                    }
                }
            });
        }
    }

    private void setLocation(Location location) {
        currentLocation = location;
        currentLocationLatitude = location.getLatitude();
        currentLocationLongitude = location.getLongitude();

        if(mMap != null) {
            mMap.clear();
            LatLng moveToCurrent = new LatLng(currentLocationLatitude, currentLocationLongitude);
            mMap.addMarker(new MarkerOptions()
                    .position(moveToCurrent)
                    .title("Marker"));
            mMap.moveCamera(CameraUpdateFactory.newLatLng(moveToCurrent));
        }
    }

    public void onMapReady(GoogleMap map) {
        mMap = map;
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
