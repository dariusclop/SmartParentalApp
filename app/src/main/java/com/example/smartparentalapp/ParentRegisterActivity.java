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

import org.w3c.dom.Text;

public class ParentRegisterActivity extends AppCompatActivity {
    private FirebaseAuth dbAuth;
    private MenuHelper menuHelper;
    private EditText mEmailField;
    private EditText mPasswordField;
    private EditText mConfirmPasswordField;
    private EditText mDisplayNameField;
    private boolean isRegistrationValid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_parent_register);
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
        mEmailField = findViewById(R.id.emailParent);
        mPasswordField = findViewById(R.id.passwordParent);
        mConfirmPasswordField = findViewById(R.id.confirmPasswordParent);
        mDisplayNameField = findViewById(R.id.displayParentName);

        // Buttons
        Button registerParentButton = findViewById(R.id.registerParentButton);

        //Button click listener
        registerParentButton.setOnClickListener(new View.OnClickListener() {
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
