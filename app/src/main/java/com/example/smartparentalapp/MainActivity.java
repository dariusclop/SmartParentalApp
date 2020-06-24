package com.example.smartparentalapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.ListView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

public class MainActivity extends AppCompatActivity {
    private FirebaseAuth dbAuth;
    private MenuHelper menuHelper;
    private Parent currentParent;
    private Child currentChild;
    private FirebaseFirestore fStore;
    protected static final int PERMISSION_REQUEST_CODE = 0x1111;
    private Intent locationService;
    private Intent sessionService;
    private boolean locationServiceStarted;
    private boolean sessionServiceStarted;
    private List<Session> sessionList;
    private ArrayList<Session> currentSessionList;
    private ListView statisticsList;
    private SessionListAdapter arrayAdapter;
    private final static String TAG = "DashboardActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        menuHelper = new MenuHelper();
        dbAuth = FirebaseAuth.getInstance();
        FirebaseUser userSignedIn = dbAuth.getCurrentUser();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getSupportActionBar().hide();
        fStore = FirebaseFirestore.getInstance();
        locationServiceStarted = false;
        sessionServiceStarted = false;

        //Views
        statisticsList = findViewById(R.id.listViewDashboard);

        //Lists
        sessionList = new ArrayList<>();
        currentSessionList = new ArrayList<>();

        //Menu set click listener
        BottomNavigationView clickedMenuItem = findViewById(R.id.bottom_navigation);
        if (userSignedIn != null) {
            clickedMenuItem.getMenu().removeItem(R.id.loginPage);
        } else {
            clickedMenuItem.getMenu().removeItem(R.id.profilePage);
        }
        clickedMenuItem.setOnNavigationItemSelectedListener(onNavigationItemSelectedListener);
    }

    @Override
    protected void onStart() {
        super.onStart();
        final FirebaseUser currentUser = dbAuth.getCurrentUser();
        checkPermissions();
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
                                    fStore.collection("sessions").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                        @Override
                                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                            if (task.isSuccessful()) {
                                                if(task.getResult().size() > 0) {
                                                    for (QueryDocumentSnapshot document : task.getResult()) {
                                                        if (document.exists()) {
                                                            sessionList.add(document.toObject(Session.class));
                                                        }
                                                    }
                                                    getAssociatedSessions();
                                                    arrayAdapter = new SessionListAdapter(MainActivity.this, R.layout.list_adapter_layout, currentSessionList);
                                                    statisticsList.setAdapter(arrayAdapter);
                                                }
                                            } else {
                                                Log.d(TAG, "Error fetching sessions: ", task.getException());
                                            }
                                        }
                                    });
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
                                    locationService = new Intent(getApplicationContext(), LocationFetchingService.class);
                                    sessionService = new Intent(getApplicationContext(), SessionMonitorService.class);
                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                        startForegroundService(locationService);
                                        startForegroundService(sessionService);

                                    } else {
                                        startService(locationService);
                                        startService(sessionService);
                                    }
                                    locationServiceStarted = true;
                                    sessionServiceStarted = true;
                                }
                            }
                        });
                    }
                }
            });
        }
    }

    private void getAssociatedSessions() {
        List<String> currentParentChildIds = currentParent.getChildIds();
        for(Session session : sessionList) {
            for(String childIds : currentParentChildIds) {
                if(session != null && !(session.getEndTime().equals("notFinished")) && session.getChildId().equals(childIds)) {
                    currentSessionList.add(session);
                }
            }
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

    @Override
    protected void onResume() {
        FirebaseUser userSignedIn = dbAuth.getCurrentUser();
        if(userSignedIn == null && locationServiceStarted) {
            stopService(locationService);
            locationServiceStarted = false;
        }
        if(userSignedIn == null && sessionServiceStarted) {
            stopService(sessionService);
            sessionServiceStarted = false;
        }
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        if(locationServiceStarted) {
            stopService(locationService);
            locationServiceStarted = false;
        }
        if(locationServiceStarted) {
            stopService(sessionService);
            sessionServiceStarted = false;
        }
        super.onDestroy();
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
