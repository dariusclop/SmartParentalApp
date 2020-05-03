package com.example.smartparentalapp;

import android.location.Location;
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

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

public class ChildHelper {

    private final static String TAG = "ChildHelper";
    FirebaseFirestore fStore;
    private Child currentChild;

    public ChildHelper(Child currentChild) {
        this.currentChild = currentChild;
        fStore = FirebaseFirestore.getInstance();
    }

    public void findChildById(final String id, final AtomicReference<Child> child, final ChildFinderCallback childCallback) {
        fStore.collection("children").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        if(document.getId().equals(id)) {
                            child.set(document.toObject(Child.class));
                            break;
                        }
                    }
                    childCallback.onCallback();
                } else {
                    Log.d(TAG, "Error finding child by id: ", task.getException());
                }
            }
        });
    }

    public void isTokenValid(final String code, final TokenFinderCallback callback, final AtomicBoolean isValid) {
        fStore.collection("parents").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        if(document.getString("generatedCode").equals(code)) {
                            isValid.set(true);
                            break;
                        }
                    }
                    callback.onCallback();
                } else {
                    Log.d(TAG, "Error validating token: ", task.getException());
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
                Log.v(TAG, "On success, child user was created at id "+ currentChild.getChildId());
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.v(TAG, "On failure, error at creating parent user -> " + e.toString());
            }
        });
    }

    public void updateLocationData(String childId, Location location) {
        DocumentReference childReference = fStore.collection("children").document(childId);

        //Update latitude
        childReference.update("latitude", location.getLatitude()).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.v(TAG, "On success, child location latitude was updated");
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.v(TAG, "On failure, error updating child latitude data -> " + e.toString());
            }
        });

        //Update longitude
        childReference.update("longitude", location.getLongitude()).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.v(TAG, "On success, child location longitude was updated");
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.v(TAG, "On failure, error updating child longitude data -> " + e.toString());
            }
        });
    }
}
