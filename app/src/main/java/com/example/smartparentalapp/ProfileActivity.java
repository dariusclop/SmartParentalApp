package com.example.smartparentalapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.concurrent.atomic.AtomicReference;

public class ProfileActivity extends AppCompatActivity {
    private FirebaseAuth dbAuth;
    private MenuHelper menuHelper;
    private FirebaseFirestore fStore;
    private Button generateCodeButton;
    private EditText generatedCodeText;
    private TextView generatedCodeHelper;
    private TextView fullNameText;
    private Parent currentParent;
    private Child currentChild;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        menuHelper = new MenuHelper();
        dbAuth = FirebaseAuth.getInstance();
        FirebaseUser userSignedIn = dbAuth.getCurrentUser();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        getSupportActionBar().hide();
        fStore = FirebaseFirestore.getInstance();

        // Buttons
        Button logoutButton = findViewById(R.id.logoutButton);
        generateCodeButton = findViewById(R.id.generateCode);

        // TextViews
        generatedCodeText = findViewById(R.id.generatedCodeText);
        generatedCodeHelper = findViewById(R.id.generatedCodeHelperText);
        fullNameText = findViewById(R.id.fullNameText);

        // Hide buttons
        generateCodeButton.setVisibility(View.GONE);
        generatedCodeText.setVisibility(View.GONE);
        generatedCodeHelper.setVisibility(View.GONE);

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
        generateCodeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                generateCode();
            }
        });
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
                        generateCodeButton.setVisibility(View.VISIBLE);
                        ParentHelper parentHelper = new ParentHelper(null);
                        final String userUid = currentUser.getUid();
                        final AtomicReference<Parent> currentParentReference = new AtomicReference<>(null);
                        parentHelper.findParentById(userUid, currentParentReference, new ParentFinderCallback() {
                            @Override
                            public void onCallback() {
                                if(currentParentReference.get() != null) {
                                    currentParent = currentParentReference.get();
                                    currentChild = null;
                                    fullNameText.setText(currentParent.getDisplayName());
                                }
                            }
                        });
                    }
                    else {
                        generateCodeButton.setVisibility(View.GONE);
                        generatedCodeText.setVisibility(View.GONE);
                        generatedCodeHelper.setVisibility(View.GONE);
                        ChildHelper childHelper = new ChildHelper(null);
                        final String userUid = currentUser.getUid();
                        final AtomicReference<Child> currentChildReference = new AtomicReference<>(null);
                        childHelper.findChildById(userUid, currentChildReference, new ChildFinderCallback() {
                            @Override
                            public void onCallback() {
                                if(currentChildReference.get() != null) {
                                    currentChild = currentChildReference.get();
                                    currentParent = null;
                                    fullNameText.setText(currentChild.getDisplayName());
                                }
                            }
                        });
                    }
                }
            });
        }
    }

    private void generateCode() {
        if(currentParent != null) {
            ParentHelper parentHelper = new ParentHelper(currentParent);
            generatedCodeText.setVisibility(View.VISIBLE);
            generatedCodeHelper.setVisibility(View.VISIBLE);
            String displayKey = parentHelper.generateKey();
            generatedCodeText.setText(displayKey);
        }
    }

    private void signOut() {
        dbAuth.signOut();
        updateUIAfterLogout(null);
    }

    private void updateUIAfterLogout(FirebaseUser account){
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
            Intent intent = menuHelper.navigationMenu(item, userSignedIn, getApplicationContext());
            startActivityForResult(intent, 0);
            return true;
        }
    };
}
