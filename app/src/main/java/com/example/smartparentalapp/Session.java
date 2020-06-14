package com.example.smartparentalapp;

import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.util.Log;

import java.util.HashMap;
import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.UUID;

public class Session {

    private final static String TAG = "Session";
    private int totalTime;
    private HashMap<String, Object> sessionList;
    private String sessionId;

    public Session() {
        this.sessionId = UUID.randomUUID().toString();
        this.totalTime = 0;
        this.sessionList = new HashMap<>();
    }

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
        }
    }
}
