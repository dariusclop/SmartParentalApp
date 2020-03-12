package com.example.smartparentalapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.view.MenuItem;
import android.widget.Toast;
import android.view.View;


import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {
    private FirebaseAuth dbAuth;
    private EditText mEmailField;
    private EditText mPasswordField;
    private boolean firstTimeRedirect = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        dbAuth = FirebaseAuth.getInstance();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        getSupportActionBar().hide();

        // Views
        mEmailField = findViewById(R.id.emailField);
        mPasswordField = findViewById(R.id.passwordField);

        // Buttons
        Button signInButton = (Button) findViewById(R.id.signInButton);
        //Button createAccountButton = (Button) findViewById(R.id.createAccountButton);

        //Menu set click listener
        BottomNavigationView clickedMenuItem = findViewById(R.id.bottom_navigation);
        clickedMenuItem.setOnNavigationItemSelectedListener(onNavigationItemSelectedListener);

        //Button click listener
        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(TextUtils.isEmpty(mEmailField.getText().toString())) {
                    mEmailField.setError("Field cannot be empty");
                } else if(TextUtils.isEmpty(mPasswordField.getText().toString())) {
                    mPasswordField.setError("Field cannot be empty");
                } else {
                    signIn(mEmailField.getText().toString(), mPasswordField.getText().toString());
                }
            }
        });
        //createAccountButton.setOnClickListener();
    }

    @Override
    protected void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = dbAuth.getCurrentUser();
        //updateUI(currentUser);
    }

    public void updateUI(FirebaseUser account){
        if(account != null && firstTimeRedirect){
            Toast.makeText(this,"Signed in",Toast.LENGTH_LONG).show();
            startActivity(new Intent(this,  MainActivity.class));
            firstTimeRedirect = false;
        }else {
            Toast.makeText(this,"Account sign in failed",Toast.LENGTH_LONG).show();
        }
    }

    //Sign In button click listener

    private void signIn(String email, String password) {
        dbAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            FirebaseUser user = dbAuth.getCurrentUser();
                            updateUI(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            Toast.makeText(LoginActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                            updateUI(null);
                        }
                    }
                });
    }

    //Menu click listener
    private BottomNavigationView.OnNavigationItemSelectedListener onNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {
        public boolean onNavigationItemSelected(MenuItem item) {
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
            return true;
        }
    };
}
