package com.example.smartparentalapp;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

public class ParentHelper {

    private final static String TAG = "ParentHelper";
    private Parent currentParent;
    private FirebaseFirestore fStore;

    public ParentHelper(Parent currentParent) {
        this.currentParent = currentParent;
        fStore = FirebaseFirestore.getInstance();
    }

    public void findParentById(final String id, final AtomicReference<Parent> parent, final ParentFinderCallback parentCallback) {
        fStore.collection("parents").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        if(document.getId().equals(id)) {
                            parent.set(document.toObject(Parent.class));
                            break;
                        }
                    }
                    parentCallback.onCallback();
                } else {
                    Log.d(TAG, "Error finding child by id: ", task.getException());
                }
            }
        });
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

    public String generateKey() {
        String generatedKey = currentParent.generateCodeForChild();
        DocumentReference parentsReference = fStore.collection("parents").document(currentParent.getParentId());
        parentsReference.update("generatedCode", generatedKey).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.v(TAG, "On success, new generated key was created");
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.v(TAG, "On failure, couldn't generate key -> " + e.toString());
            }
        });
        return generatedKey;
    }

    public void getAllChildren(final List<Child> children) {
        fStore.collection("children").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    if(task.getResult().size() > 0) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            if (document.exists()) {
                                children.add(document.toObject(Child.class));
                            }
                        }
                    }
                } else {
                    Log.d(TAG, "Error fetching children: ", task.getException());
                }
            }
        });
    }
}
