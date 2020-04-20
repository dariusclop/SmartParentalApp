package com.example.smartparentalapp;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class ParentHelper {

    private final static String TAG = "ParentHelper";
    private Parent currentParent;
    private FirebaseFirestore fStore;

    public ParentHelper(Parent currentParent) {
        this.currentParent = currentParent;
        fStore = FirebaseFirestore.getInstance();
    }

    public void addUser() {
        DocumentReference parentsReference = fStore.collection("parents").document(currentParent.getParentId());
        parentsReference.set(currentParent).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.v(TAG, "On success, parent user was created at id "+ currentParent.getParentId());
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.v(TAG, "On failure, error at creating parent user -> " + e.toString());
            }
        });
    }
}
