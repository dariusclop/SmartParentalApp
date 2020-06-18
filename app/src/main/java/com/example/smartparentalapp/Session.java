package com.example.smartparentalapp;

import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.UUID;

public class Session {

    private final static String TAG = "Session";
    private int totalTime;
    private HashMap<String, Integer> sessionList;
    private String sessionId;
    private String childId;
    private FirebaseFirestore fStore;
    private String startTime;
    private String endTime;
    private ZoneId zoneId = ZoneId.of( "Europe/Bucharest" );
    DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");

    public Session(String childId) {
        this.sessionId = UUID.randomUUID().toString();
        this.totalTime = 0;
        this.sessionList = new HashMap<>();
        this.childId = childId;
        ZonedDateTime zdt = ZonedDateTime.now(zoneId);
        this.startTime = dateTimeFormatter.format(zdt);
        this.endTime = dateTimeFormatter.format(zdt);
        fStore = FirebaseFirestore.getInstance();
    }

    public String getChildId() {
        return this.childId;
    }

    public String getSessionId() {
        return this.sessionId;
    }

    public void setTotalTime(int newTotalTime) {
        this.totalTime = newTotalTime;
    }

    public synchronized void updateSession(Context context) {
        if(!sessionList.isEmpty()) {
            setTotalTime(this.totalTime + 5);
        }

        //init managers
        UsageStatsManager usageStatsManager = (UsageStatsManager) context.getSystemService(Context.USAGE_STATS_SERVICE);
        PackageManager packageManager = context.getPackageManager();

        //init fields
        String foregroundPackageName;
        String foregroundApplicationName;
        ApplicationInfo currentApplicationInfo;
        long currTime = System.currentTimeMillis();

        //get statistics
        List<UsageStats> usageStats = usageStatsManager.queryUsageStats(UsageStatsManager.INTERVAL_DAILY, currTime - 1000*1000, currTime);
        if(usageStats != null) {
            SortedMap<Long, UsageStats> currentRunningTasks = new TreeMap<>();
            for(UsageStats stats : usageStats) {
                currentRunningTasks.put(stats.getLastTimeUsed(), stats);
            }
            try {
                foregroundPackageName = currentRunningTasks.get(currentRunningTasks.lastKey()).getPackageName();
                currentApplicationInfo = packageManager.getApplicationInfo(foregroundPackageName, 0);

            } catch (Exception e) {
                currentApplicationInfo = null;
                Log.d(TAG, "There was an error getting the foreground app: " + e.toString());
            }
            foregroundApplicationName = (String) (currentApplicationInfo != null ? packageManager.getApplicationLabel(currentApplicationInfo) : "None");
            Log.i(TAG, foregroundApplicationName);
            if(sessionList.isEmpty()) {
                createSessionEntry(foregroundApplicationName);
            }
            else {
                updateSessionEntry(foregroundApplicationName);
            }
        }
    }

    public synchronized void createSessionEntry(String applicationName) {
        sessionList.put(applicationName, 0);
        DocumentReference sessionReference = fStore.collection("sessions").document(getSessionId());
        sessionReference.set(this).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.v(TAG, "On success, session was created for at id " + getSessionId() + " for child with id " + getChildId());
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.v(TAG, "On failure, error at creating session -> " + e.toString());
            }
        });

        //start date-time update
        sessionReference.update("startTime", this.startTime).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.v(TAG, "On success, start time for session was updated for at id " + getSessionId() + " for child with id " + getChildId());
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.v(TAG, "On failure, error at updating session -> " + e.toString());
            }
        });
    }

    public synchronized void updateSessionEntry(String applicationName) {
        if(sessionList.containsKey(applicationName)) {
            int applicationTime = sessionList.get(applicationName) + 5;
            sessionList.put(applicationName, applicationTime);
        }
        else {
            sessionList.put(applicationName, 5);
        }

        ZonedDateTime zdt = ZonedDateTime.now(zoneId);
        String endTime = dateTimeFormatter.format(zdt);

        //total time update
        DocumentReference sessionReference = fStore.collection("sessions").document(getSessionId());
        sessionReference.update("totalTime", this.totalTime).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.v(TAG, "On success, total time for session was updated for at id " + getSessionId() + " for child with id " + getChildId());
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.v(TAG, "On failure, error at updating session -> " + e.toString());
            }
        });

        //end date-time update
        sessionReference.update("endTime", endTime).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.v(TAG, "On success, end time for session was updated for at id " + getSessionId() + " for child with id " + getChildId());
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.v(TAG, "On failure, error at updating session -> " + e.toString());
            }
        });

        //application data update
        sessionReference.update("sessionList", sessionList).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.v(TAG, "On success, session was updated for at id " + getSessionId() + " for child with id " + getChildId());
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.v(TAG, "On failure, error at updating session -> " + e.toString());
            }
        });

    }
}
