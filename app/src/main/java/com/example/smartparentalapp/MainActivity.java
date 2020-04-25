package com.example.smartparentalapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.concurrent.atomic.AtomicReference;

public class MainActivity extends AppCompatActivity {
    private FirebaseAuth dbAuth;
    private MenuHelper menuHelper;
    private Parent currentParent;
    private Child currentChild;
    private FirebaseFirestore fStore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        menuHelper = new MenuHelper();
        dbAuth = FirebaseAuth.getInstance();
        FirebaseUser userSignedIn = dbAuth.getCurrentUser();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getSupportActionBar().hide();
        fStore = FirebaseFirestore.getInstance();

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
