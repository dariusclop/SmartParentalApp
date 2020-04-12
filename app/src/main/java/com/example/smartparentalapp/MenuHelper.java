package com.example.smartparentalapp;

import android.content.Context;
import android.content.Intent;
import android.view.MenuItem;

import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseUser;

public class MenuHelper extends AppCompatActivity {

    public Intent navigationMenu(MenuItem item, FirebaseUser userSignedIn, Context currentContext) {
        if(userSignedIn != null) {
            switch (item.getItemId()) {
                case R.id.dashboardPage:
                    Intent dashboardIntent = new Intent(currentContext, MainActivity.class);
                    return dashboardIntent;
                case R.id.locationPage:
                    Intent locationIntent = new Intent(currentContext, LocationActivity.class);
                    return locationIntent;
                case R.id.profilePage:
                    Intent loginIntent = new Intent(currentContext, ProfileActivity.class);
                    return loginIntent;

            }
        }
        else {
            switch (item.getItemId()) {
                case R.id.dashboardPage:
                    Intent dashboardIntent = new Intent(currentContext, MainActivity.class);
                    return dashboardIntent;
                case R.id.locationPage:
                    Intent locationIntent = new Intent(currentContext, LocationActivity.class);
                    return locationIntent;
                case R.id.loginPage:
                    Intent loginIntent = new Intent(currentContext, LoginActivity.class);
                    return loginIntent;

            }
        }
        return null;
    }
};
