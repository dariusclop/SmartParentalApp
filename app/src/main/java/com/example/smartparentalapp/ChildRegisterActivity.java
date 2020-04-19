package com.example.smartparentalapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class ChildRegisterActivity extends AppCompatActivity {
    private FirebaseAuth dbAuth;
    private MenuHelper menuHelper;
    private EditText mEmailField;
    private EditText mPasswordField;
    private EditText mConfirmPasswordField;
    private EditText mDisplayNameField;
    private EditText mGeneratedCodeField;
    private boolean isRegistrationValid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_child_register);
        getSupportActionBar().hide();
        menuHelper = new MenuHelper();
        dbAuth = FirebaseAuth.getInstance();
        FirebaseUser userSignedIn = dbAuth.getCurrentUser();

        //Menu set click listener
        BottomNavigationView clickedMenuItem = findViewById(R.id.bottom_navigation);
        if(userSignedIn != null) {
            clickedMenuItem.getMenu().removeItem(R.id.loginPage);
        }
        else {
            clickedMenuItem.getMenu().removeItem(R.id.profilePage);
        }
        clickedMenuItem.setOnNavigationItemSelectedListener(onNavigationItemSelectedListener);

        // Views
        mEmailField = findViewById(R.id.emailChild);
        mPasswordField = findViewById(R.id.passwordChild);
        mConfirmPasswordField = findViewById(R.id.confirmPasswordChild);
        mDisplayNameField = findViewById(R.id.displayChildName);
        mGeneratedCodeField = findViewById(R.id.generatedCode);

        // Buttons
        Button registerChildButton = findViewById(R.id.registerChildButton);

        //Button click listener
        registerChildButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isRegistrationValid = true;

                if(TextUtils.isEmpty(mEmailField.getText().toString())) {
                    isRegistrationValid = false;
                    mEmailField.setError("Field cannot be empty");
                }
                if(TextUtils.isEmpty(mPasswordField.getText().toString())) {
                    isRegistrationValid = false;
                    mPasswordField.setError("Field cannot be empty");
                }
                if(TextUtils.isEmpty(mConfirmPasswordField.getText().toString())) {
                    isRegistrationValid = false;
                    mConfirmPasswordField.setError("Field cannot be empty");
                }
                if(TextUtils.isEmpty(mDisplayNameField.getText().toString())) {
                    isRegistrationValid = false;
                    mDisplayNameField.setError("Field cannot be empty");
                }
                if(TextUtils.isEmpty(mGeneratedCodeField.getText().toString())) {
                    isRegistrationValid = false;
                    mGeneratedCodeField.setError("Field cannot be empty");
                }
                if(TextUtils.isDigitsOnly(mPasswordField.getText().toString()))
                {
                    isRegistrationValid = false;
                    mPasswordField.setError("Password can't be only digits");
                }
                if(!TextUtils.equals(mPasswordField.getText().toString(), mConfirmPasswordField.getText().toString()))
                {
                    isRegistrationValid = false;
                    mConfirmPasswordField.setError("Passwords don't match");
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
