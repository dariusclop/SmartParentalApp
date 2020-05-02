package com.example.smartparentalapp;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.location.Location;
import android.os.Build;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.concurrent.Executor;

public class LocationFetchingService extends Service {
    private boolean isLocationActivated = false;
    private FirebaseAuth dbAuth;
    private FusedLocationProviderClient fusedLocationClient;
    private Location currentLocation;
    private double currentLocationLatitude;
    private double currentLocationLongitude;
    private LocationRequest locationRequest;
    private LocationCallback locationCallback;
    private Child currentChild;
    private FirebaseFirestore fStore;
    private NotificationManager notificationManager;
    private Handler serviceHandler;

    public LocationFetchingService() {}

    @Override
    public void onCreate() {
        dbAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();

        //Location
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        locationRequest = LocationRequest.create();

        //Location Callback function
        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                if (locationResult == null) {
                    return;
                }
                for (Location location : locationResult.getLocations()) {
                    Log.d("123", "Locations was changed");
                    if(currentLocation != null && (currentLocation.getLatitude() != location.getLatitude() || currentLocation.getLongitude() != location.getLongitude())) {
                        setLocation(location);
                    }
                }
            }
        };

        createLocationRequest();
        //getLastLocation();
        HandlerThread handlerThread = new HandlerThread("LocationFetchingService");
        handlerThread.start();
        serviceHandler = new Handler(handlerThread.getLooper());
        notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel notificationChannel = new NotificationChannel("1111", getString(R.string.locationServiceName), NotificationManager.IMPORTANCE_DEFAULT);
            notificationManager.createNotificationChannel(notificationChannel);
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        requestLocationUpdates();
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        stopLocationUpdates();
    }

    public void requestLocationUpdates() {
        fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, Looper.myLooper());
    }

    private void stopLocationUpdates() {
        fusedLocationClient.removeLocationUpdates(locationCallback);
        serviceHandler.removeCallbacks(null);
        stopSelf();
    }

//    private void getLastLocation() {
//        fusedLocationClient.getLastLocation()
//                .addOnSuccessListener(this, new OnSuccessListener<Location>() {
//                    @Override
//                    public void onSuccess(Location location) {
//                        if (location != null) currentLocation = location;
//                    }
//                });
//    }

    protected void createLocationRequest() {
        locationRequest.setInterval(60000);
        locationRequest.setFastestInterval(30000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    private void setLocation(Location location) {
        currentLocation = location;
        currentLocationLatitude = location.getLatitude();
        currentLocationLongitude = location.getLongitude();

        //do here firebase store location
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
