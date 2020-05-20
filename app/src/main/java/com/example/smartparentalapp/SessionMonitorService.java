package com.example.smartparentalapp;

import android.app.ActivityManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import java.util.Timer;

public class SessionMonitorService extends Service {
    private final static String TAG = "SessionMonitorService";
    private Handler serviceHandler;
    private NotificationManager notificationManager;
    public static int SERVICE_FETCH_DATA_RATE = 5000;

    public SessionMonitorService() {}

    @Override
    public void onCreate() {
        HandlerThread handlerThread = new HandlerThread("SessionMonitorService");
        handlerThread.start();
        serviceHandler = new Handler(handlerThread.getLooper());
        notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        String channelId = Build.VERSION.SDK_INT >= Build.VERSION_CODES.O ? createNotificationChannel(notificationManager) : "";

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, channelId);
            Notification notification = notificationBuilder.setOngoing(true)
                    .setSmallIcon(R.drawable.ic_supervisor)
                    .setContentTitle(getString(R.string.sessionMonitorServiceTitle))
                    .setContentText(getString(R.string.sessionMonitorServiceName))
                    .setPriority(1)
                    .setCategory(NotificationCompat.CATEGORY_SERVICE)
                    .build();

            startForeground(1021 , notification);
        }
    }

    private String createNotificationChannel(NotificationManager notificationManager) {
        String channelId = "my_service_channelid_session";
        String channelName = "My Session Fetching Service";
        NotificationChannel channel = new NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_HIGH);
        channel.setImportance(NotificationManager.IMPORTANCE_NONE);
        channel.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);
        notificationManager.createNotificationChannel(channel);
        return channelId;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "Session Monitor Service started");
//        if(timer == null)
//        {
//            timer = new Timer();
//            timer.schedule(new MonitoringTimerTask(), 500, SERVICE_FETCH_DATA_RATE);
//        }
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        Log.d(TAG, "Session Monitor Service was destroyed");
        serviceHandler.removeCallbacks(null);
        super.onDestroy();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
