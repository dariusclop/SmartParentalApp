package com.example.smartparentalapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.lang.ref.WeakReference;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

public class ChildRegisterActivity extends AppCompatActivity {
    private FirebaseAuth dbAuth;
    private MenuHelper menuHelper;
    private EditText mEmailField;
    private EditText mPasswordField;
    private EditText mConfirmPasswordField;
    private EditText mDisplayNameField;
    private EditText mGeneratedCodeField;
    private boolean isRegistrationValid;
    private AtomicBoolean isValidToken;
    ChildHelper tokenValidation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_child_register);
        getSupportActionBar().hide();
        menuHelper = new MenuHelper();
        tokenValidation = new ChildHelper(null);
        dbAuth = FirebaseAuth.getInstance();
        isValidToken = new AtomicBoolean(false);

        //Menu set click listener
        BottomNavigationView clickedMenuItem = findViewById(R.id.bottom_navigation);
        clickedMenuItem.getMenu().removeItem(R.id.profilePage);
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
                if(mPasswordField.getText().toString().length() < 6) {
                    isRegistrationValid = false;
                    mPasswordField.setError("Password needs to be at least 6 characters");
                }
                if(isRegistrationValid) {
                    tokenValidation.isTokenValid(mGeneratedCodeField.getText().toString().trim(), new TokenFinderCallback() {
                        @Override
                        public void onCallback() {
                            if(isValidToken.get()) {
                                registerUser(mEmailField.getText().toString(), mPasswordField.getText().toString());
                            }
                            else {
                                isRegistrationValid = false;
                                mGeneratedCodeField.setError("Token is invalid");
                            }
                        }
                    }, isValidToken);
                }
            }
        });
    }

    private void updateUI(FirebaseUser account){
        if(account != null){
            Toast.makeText(this,"Registration success, you are logged in!",Toast.LENGTH_LONG).show();
            startActivity(new Intent(this,  MainActivity.class));
        }
        else {
            Toast.makeText(this,"Account registration failed",Toast.LENGTH_LONG).show();
        }
    }

    private void createChildUser(FirebaseUser user) {
        if(user != null) {
            String currentUserUid = user.getUid();
            String currentEmail = mEmailField.getText().toString();
            String currentDisplayName = mDisplayNameField.getText().toString();
            String currentGeneratedCode = mGeneratedCodeField.getText().toString();
            Child newChild = new Child(currentUserUid, currentEmail, currentDisplayName, currentGeneratedCode);
            ChildHelper childHelper = new ChildHelper(newChild);
            childHelper.connectToParent();
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
                            createChildUser(user);
                            updateUI(user);
                        } else {
                            // If register fails, display a message to the user.
                            Log.w("Account registration failed", task.getException().getMessage());
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
