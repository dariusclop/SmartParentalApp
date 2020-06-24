package com.example.smartparentalapp;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

public class SessionHelper {

    private final static String TAG = "SessionHelper";
    private FirebaseFirestore fStore;
    private Session currentSession;

    public SessionHelper(Session currentSession) {
        this.currentSession = currentSession;
        fStore = FirebaseFirestore.getInstance();
    }

    public synchronized void updateChildDisplayName() {
        fStore.collection("children").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        if(document.getId().equals(currentSession.getChildId())) {
                            Child newChild = document.toObject(Child.class);
                            String childDisplayName = newChild.getDisplayName();
                            DocumentReference sessionReference = fStore.collection("sessions").document(currentSession.getSessionId());
                            sessionReference.update("childDisplayName", childDisplayName).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Log.v(TAG, "On success, child display name was updated for session with id " + currentSession.getSessionId());
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Log.v(TAG, "On failure, error at updating child display name -> " + e.toString());
                                }
                            });
                            break;
                        }
                    }

                } else {
                    Log.d(TAG, "Error finding child by id: ", task.getException());
                }
            }
        });
    }

    public synchronized void createSessionEntry() {
        DocumentReference sessionReference = fStore.collection("sessions").document(currentSession.getSessionId());
        sessionReference.set(currentSession).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.v(TAG, "On success, session was created for at id " + currentSession.getSessionId() + " for child with id " + currentSession.getChildId());
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.v(TAG, "On failure, error at creating session -> " + e.toString());
            }
        });
    }

    public synchronized void updateSessionEntry() {

        //total time update
        DocumentReference sessionReference = fStore.collection("sessions").document(currentSession.getSessionId());
        sessionReference.update("totalTime", currentSession.getTotalTime()).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.v(TAG, "On success, total time for session was updated for at id " + currentSession.getSessionId() + " for child with id " + currentSession.getChildId());
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.v(TAG, "On failure, error at updating session -> " + e.toString());
            }
        });

        //application data update
        sessionReference.update("sessionList", currentSession.getSessionList()).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.v(TAG, "On success, session was updated for at id " + currentSession.getSessionId() + " for child with id " + currentSession.getChildId());
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.v(TAG, "On failure, error at updating session -> " + e.toString());
            }
        });

    }

    public synchronized void updateEndOfSession() {
        DocumentReference sessionReference = fStore.collection("sessions").document(currentSession.getSessionId());

        //end date-time update
        sessionReference.update("endTime", currentSession.getEndTime()).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.v(TAG, "On success, end time for session was updated for at id " + currentSession.getSessionId() + " for child with id " + currentSession.getChildId());
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.v(TAG, "On failure, error at updating session -> " + e.toString());
            }
        });
    }
}
