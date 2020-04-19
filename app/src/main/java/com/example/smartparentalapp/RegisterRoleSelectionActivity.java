package com.example.smartparentalapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class RegisterRoleSelectionActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_role_selection);
        getSupportActionBar().hide();

        // Buttons
        Button createParentAccountButton = findViewById(R.id.createParentAcc);
        Button createChildAccountButton = findViewById(R.id.createChildAcc);
        Button backButton = findViewById(R.id.backToLogin);

        //Button click listener
        createParentAccountButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent roleSelectionIntent = new Intent(getApplicationContext(), ParentRegisterActivity.class);
                startActivityForResult(roleSelectionIntent, 0);
            }
        });

        createChildAccountButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent roleSelectionIntent = new Intent(getApplicationContext(), ChildRegisterActivity.class);
                startActivityForResult(roleSelectionIntent, 0);
            }
        });

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent roleSelectionIntent = new Intent(getApplicationContext(), LoginActivity.class);
                startActivityForResult(roleSelectionIntent, 0);
            }
        });
    }
}
