package com.example.smartparentalapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class ParentRegisterActivity extends AppCompatActivity {
    private FirebaseAuth dbAuth;
    private MenuHelper menuHelper;
    private ParentHelper parentHelper;
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

        //Menu set click listener
        BottomNavigationView clickedMenuItem = findViewById(R.id.bottom_navigation);
        clickedMenuItem.getMenu().removeItem(R.id.profilePage);
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

                if(isRegistrationValid) {
                    registerUser(mEmailField.getText().toString(), mPasswordField.getText().toString());
                }
            }
        });
    }

    private void updateUI(FirebaseUser account){
        if(account != null) {
            Toast.makeText(this,"Registration success, you are logged in!",Toast.LENGTH_LONG).show();
            startActivity(new Intent(this,  MainActivity.class));
        }
        else {
            Toast.makeText(this,"Account registration failed",Toast.LENGTH_LONG).show();
        }
    }

    private void createParentUser(FirebaseUser user) {
        if(user != null) {
            String currentUserUid = user.getUid();
            String currentEmail = mEmailField.getText().toString();
            String currentDisplayName = mDisplayNameField.getText().toString();
            Parent newParent = new Parent(currentUserUid, currentEmail, currentDisplayName);
            parentHelper = new ParentHelper(newParent);
            parentHelper.addUser();
        }
    }

    private void registerUser(String email, String password) {
        dbAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            FirebaseUser user = dbAuth.getCurrentUser();
                            createParentUser(user);
                            updateUI(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            updateUI(null);
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
