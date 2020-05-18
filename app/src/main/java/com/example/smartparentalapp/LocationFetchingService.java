package com.example.smartparentalapp;

import android.app.Notification;
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

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

public class LocationFetchingService extends Service {
    private final static String TAG = "LocationFetchingService";
    private FirebaseAuth dbAuth;
    private FusedLocationProviderClient fusedLocationClient;
    private Location currentLocation;
    private LocationRequest locationRequest;
    private LocationCallback locationCallback;
    private FirebaseFirestore fStore;
    private NotificationManager notificationManager;
    private Handler serviceHandler;
    private FirebaseUser currentUser;
    private ChildHelper childHelper;

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
                FirebaseUser currentUser = dbAuth.getCurrentUser();
                if (locationResult == null) {
                    return;
                }
                if (currentUser == null) {
                    stopLocationUpdates();
                    return;
                }
                for (Location location : locationResult.getLocations()) {
                    if(currentLocation == null) {
                        setLocation(location);
                        Log.d(TAG, "First location was changed");
                    }
                    else {
                        if (currentLocation.getLatitude() != location.getLatitude() || currentLocation.getLongitude() != location.getLongitude()) {
                            setLocation(location);
                            Log.d(TAG, "Location was changed");
                        }
                    }
                }
            }
        };

        createLocationRequest();
        getLastLocation();
        HandlerThread handlerThread = new HandlerThread("LocationFetchingService");
        handlerThread.start();
        serviceHandler = new Handler(handlerThread.getLooper());
        notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        String channelId = Build.VERSION.SDK_INT >= Build.VERSION_CODES.O ? createNotificationChannel(notificationManager) : "";
        currentUser = dbAuth.getCurrentUser();
        childHelper = new ChildHelper(null);

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, channelId);
            Notification notification = notificationBuilder.setOngoing(true)
                    .setSmallIcon(R.drawable.ic_location)
                    .setContentTitle(getString(R.string.locationServiceTitle))
                    .setContentText(getString(R.string.locationServiceName))
                    .setPriority(1)
                    .setCategory(NotificationCompat.CATEGORY_SERVICE)
                    .build();

            startForeground(1337 , notification);
        }
    }

    private String createNotificationChannel(NotificationManager notificationManager) {
        String channelId = "my_service_channelid";
        String channelName = "My Foreground Service";
        NotificationChannel channel = new NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_HIGH);
        // omitted the LED color
        channel.setImportance(NotificationManager.IMPORTANCE_NONE);
        channel.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);
        notificationManager.createNotificationChannel(channel);
        return channelId;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        requestLocationUpdates();
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        Log.d(TAG, "Location fetching service was destroyed");
        stopLocationUpdates();
        super.onDestroy();
    }

    public void requestLocationUpdates() {
        fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, Looper.myLooper());
    }

    private void stopLocationUpdates() {
        fusedLocationClient.removeLocationUpdates(locationCallback);
        serviceHandler.removeCallbacks(null);
        stopSelf();
    }

    private void getLastLocation() {
        try {
            fusedLocationClient.getLastLocation()
                    .addOnSuccessListener(new OnSuccessListener<Location>() {
                        @Override
                        public void onSuccess(Location location) {
                            if (location != null) currentLocation = location;
                        }
                    });
        }
        catch(SecurityException e) {
            Log.e(TAG, "Lost location permission.");
        }
    }

    protected void createLocationRequest() {
        locationRequest.setInterval(30000);
        locationRequest.setFastestInterval(20000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    private void setLocation(Location location) {
        currentLocation = location;
        if(currentUser != null) {
            childHelper.updateLocationData(currentUser.getUid(), currentLocation);
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
