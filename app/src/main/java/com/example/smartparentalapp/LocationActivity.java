package com.example.smartparentalapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
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

public class LocationActivity extends AppCompatActivity implements OnMapReadyCallback {
    private FirebaseAuth dbAuth;
    private MenuHelper menuHelper;
    private GoogleMap mMap;
    private SupportMapFragment mapFragment;
    private Parent currentParent;
    private Child currentChild;
    private FirebaseFirestore fStore;
    private List<Child> childList;
    private List<Child> currentChildList;
    private List<String> currentChildDisplayNames;
    private Spinner childSpinner;
    private Button refreshButton;
    private Button refreshChildButton;
    private String currentSelected;
    private final static String TAG = "LocationActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location);
        getSupportActionBar().hide();

        menuHelper = new MenuHelper();
        dbAuth = FirebaseAuth.getInstance();
        final FirebaseUser userSignedIn = dbAuth.getCurrentUser();
        fStore = FirebaseFirestore.getInstance();

        //Buttons
        refreshButton = findViewById(R.id.refreshButton);
        refreshChildButton  = findViewById(R.id.refreshChildButton);
        refreshButton.setVisibility(View.GONE);
        refreshChildButton.setVisibility(View.GONE);

        //Spinner
        childSpinner = findViewById(R.id.childrenDropdown);
        childSpinner.setVisibility(View.GONE);

        //Lists
        childList = new ArrayList<>();
        currentChildList = new ArrayList<>();
        currentChildDisplayNames = new ArrayList<>();

        //Menu set click listener
        BottomNavigationView clickedMenuItem = findViewById(R.id.bottom_navigation);
        if(userSignedIn != null) {
            clickedMenuItem.getMenu().removeItem(R.id.loginPage);
        }
        else {
            clickedMenuItem.getMenu().removeItem(R.id.profilePage);
        }
        clickedMenuItem.setOnNavigationItemSelectedListener(onNavigationItemSelectedListener);

        //Spinner click listener
        childSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent,
                                       View view, int pos, long id) {
                currentSelected = parent.getItemAtPosition(pos).toString();
                if(currentChildList != null && currentChildList.size() > 0) {
                    for(Child child : currentChildList) {
                        if(currentSelected.equals(child.getDisplayName())) {
                            setLocation(child.getLatitude(), child.getLongitude());
                        }
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        //Button click listener
        refreshButton.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View v) {
                 fStore.collection("children").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                     @Override
                     public void onComplete(@NonNull Task<QuerySnapshot> task) {
                         if (task.isSuccessful()) {
                             if (task.getResult().size() > 0) {
                                 String currSelected = childSpinner.getSelectedItem().toString();
                                 clearLists();
                                 for (QueryDocumentSnapshot document : task.getResult()) {
                                     if (document.exists()) {
                                         childList.add(document.toObject(Child.class));
                                     }
                                 }
                                 getAssociatedChildren();
                                 ArrayAdapter<String> childListAdapter = new ArrayAdapter<>(LocationActivity.this, android.R.layout.simple_spinner_item, currentChildDisplayNames);
                                 childListAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                                 childSpinner.setAdapter(childListAdapter);
                                 childSpinner.setSelection(getIndex(childSpinner, currSelected));
                                 for(Child child : childList) {
                                     if(currSelected.equals(child.getDisplayName())) {
                                         setLocation(child.getLatitude(), child.getLongitude());
                                     }
                                 }
                             }
                         }
                     }
                 });
             }
        });

        refreshChildButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fStore.collection("children").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                FirebaseUser user = dbAuth.getCurrentUser();
                                if(document.getId().equals(user.getUid())) {
                                    currentChild = document.toObject(Child.class);
                                    setLocation(currentChild.getLatitude(), currentChild.getLongitude());
                                    break;
                                }
                            }
                        } else {
                            Log.d(TAG, "Couldn't find child by id: ", task.getException());
                        }
                    }
                });
            }
        });

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
                        childSpinner.setVisibility(View.VISIBLE);
                        refreshButton.setVisibility(View.VISIBLE);
                        refreshChildButton.setVisibility(View.GONE);
                        final ParentHelper parentHelper = new ParentHelper(null);
                        final String userUid = currentUser.getUid();
                        final AtomicReference<Parent> currentParentReference = new AtomicReference<>(null);
                        parentHelper.findParentById(userUid, currentParentReference, new ParentFinderCallback() {
                            @Override
                            public void onCallback() {
                                if(currentParentReference.get() != null) {
                                    currentParent = currentParentReference.get();
                                    currentChild = null;
                                    fStore.collection("children").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                        @Override
                                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                            if (task.isSuccessful()) {
                                                if(task.getResult().size() > 0) {
                                                    for (QueryDocumentSnapshot document : task.getResult()) {
                                                        if (document.exists()) {
                                                            childList.add(document.toObject(Child.class));
                                                        }
                                                    }
                                                    getAssociatedChildren();
                                                    ArrayAdapter<String> childListAdapter = new ArrayAdapter<>(LocationActivity.this, android.R.layout.simple_spinner_item, currentChildDisplayNames);
                                                    childListAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                                                    childSpinner.setAdapter(childListAdapter);
                                                }
                                            } else {
                                                Log.d(TAG, "Error fetching children: ", task.getException());
                                            }
                                        }
                                    });
                                }
                            }
                        });
                    }
                    else {
                        childSpinner.setVisibility(View.GONE);
                        refreshButton.setVisibility(View.GONE);
                        refreshChildButton.setVisibility(View.VISIBLE);
                        ChildHelper childHelper = new ChildHelper(null);
                        final String userUid = currentUser.getUid();
                        final AtomicReference<Child> currentChildReference = new AtomicReference<>();
                        childHelper.findChildById(userUid, currentChildReference, new ChildFinderCallback() {
                            @Override
                            public void onCallback() {
                                if(currentChildReference.get() != null) {
                                    currentChild = currentChildReference.get();
                                    currentParent = null;
                                    setLocation(currentChild.getLatitude(), currentChild.getLongitude());
                                }
                            }
                        });
                    }
                }
            });
        }
        else {
            Intent notLogged = new Intent(getApplicationContext(), LoginActivity.class);
            startActivityForResult(notLogged, 0);
        }
    }

    private void getAssociatedChildren() {
        List<String> currentParentChildIds = currentParent.getChildIds();
        for(String childIds : currentParentChildIds) {
            for(Child child : childList) {
                if(childIds.equals(child.getChildId())) {
                    currentChildList.add(child);
                    currentChildDisplayNames.add(child.getDisplayName());
                }
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        FirebaseUser currentUser = dbAuth.getCurrentUser();
        if(currentUser == null) {
            clearLists();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        clearLists();
    }

    private void clearLists() {
        while(childList.size() != 0) {
            childList.remove(childList.size() - 1);
        }
        while(currentChildList.size() != 0) {
            currentChildList.remove(currentChildList.size() - 1);
        }
        while(currentChildDisplayNames.size() != 0) {
            currentChildDisplayNames.remove(currentChildDisplayNames.size() - 1);
        }
    }


    private int getIndex(Spinner spinner, String myString){
        int index = 0;

        for (int i=0;i<spinner.getCount();i++){
            if (spinner.getItemAtPosition(i).equals(myString)){
                index = i;
            }
        }
        return index;
    }

    private void setLocation(double currentLocationLatitude, double currentLocationLongitude) {

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
