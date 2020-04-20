package com.example.smartparentalapp;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

public class ChildHelper {

    private final static String TAG = "ChildHelper";
    FirebaseFirestore fStore;
    private Child currentChild;

    public ChildHelper(Child currentChild) {
        this.currentChild = currentChild;
        fStore = FirebaseFirestore.getInstance();
    }

    public void getAllParents(final String code) {
        final AtomicBoolean isTokenValid = new AtomicBoolean(false);
        fStore.collection("parents").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        if(document.getString("generatedCode").equals(code)) {
                            isTokenValid.set(true);
                            break;
                        }
                    }
                } else {
                    Log.d(TAG, "Error getting documents: ", task.getException());
                }
            }
        });
    }

    public void connectToParent() {
        fStore.collection("parents").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        if(document.getString("generatedCode").equals(currentChild.getGeneratedCode())) {
                            addUser(document.getId());
                            break;
                        }
                    }
                } else {
                    Log.d(TAG, "Error getting documents: ", task.getException());
                }
            }
        });
    }

    public void addUser(String parentId) {

        //Parent add token
        DocumentReference parentsReference = fStore.collection("parents").document(parentId);
        parentsReference.update("childIds", FieldValue.arrayUnion(currentChild.getChildId())).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.v(TAG, "On success, parent user was updated");
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.v(TAG, "On failure, error updating parent user -> " + e.toString());
            }
        });

        //Child create account
        DocumentReference childrenReference = fStore.collection("children").document(currentChild.getChildId());
        childrenReference.set(currentChild).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.v(TAG, "On success, parent user was created at id "+ currentChild.getChildId());
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.v(TAG, "On failure, error at creating parent user -> " + e.toString());
            }
        });
    }
}
