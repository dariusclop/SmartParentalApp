package com.example.smartparentalapp;

import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.util.Log;

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
    private String childDisplayName;
    private String startTime;
    private String endTime;
    private ZoneId zoneId = ZoneId.of( "Europe/Bucharest" );
    private DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
    private ZonedDateTime zdt = ZonedDateTime.now(zoneId);

    public Session() {}

    public Session(String childId) {
        this.sessionId = UUID.randomUUID().toString();
        this.totalTime = 0;
        this.sessionList = new HashMap<>();
        this.childId = childId;
        this.childDisplayName = childId;
        this.startTime = dateTimeFormatter.format(zdt);
        this.endTime = dateTimeFormatter.format(zdt);
    }

    public String getChildId() {
        return this.childId;
    }

    public String getSessionId() {
        return this.sessionId;
    }

    public String getChildDisplayName() { return this.childDisplayName; }

    public void setTotalTime(int newTotalTime) {
        this.totalTime = newTotalTime;
    }

    public synchronized void updateSession(Context context) {
        setTotalTime(this.totalTime + 5);

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

    public boolean checkIfListEmpty() {
        return sessionList.isEmpty();
    }

    public synchronized void createSessionEntry(String applicationName) {
        sessionList.put(applicationName, 5);
    }

    public synchronized void updateSessionEntry(String applicationName) {
        if(sessionList.containsKey(applicationName)) {
            int applicationTime = sessionList.get(applicationName) + 5;
            sessionList.put(applicationName, applicationTime);
        }
        else {
            sessionList.put(applicationName, 5);
        }

    }

    public synchronized void updateEndOfSession() {
        ZonedDateTime zdt = ZonedDateTime.now(zoneId);
        String endTime = dateTimeFormatter.format(zdt);
        setEndTime(endTime);
    }

    public String getEndTime() {
        return this.endTime;
    }

    public String getStartTime() {
        return this.startTime;
    }

    public int getTotalTime() {
        return this.totalTime;
    }

    public HashMap<String, Integer> getSessionList() {
        return this.sessionList;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }
}
