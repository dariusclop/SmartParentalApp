package com.example.smartparentalapp;

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

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class SessionMonitorService extends Service {
    private final static String TAG = "SessionMonitorService";
    private static int SERVICE_FETCH_DATA_RATE = 5000;
    private Handler serviceHandler;
    private NotificationManager notificationManager;
    private Runnable runnableCode;
    private FirebaseAuth dbAuth;
    private FirebaseUser currentUser;
    private Session currentSession;
    private SessionHelper sessionHelper;

    public SessionMonitorService() {}

    @Override
    public void onCreate() {
        dbAuth = FirebaseAuth.getInstance();

        //Create service thread
        HandlerThread handlerThread = new HandlerThread("SessionMonitorService");
        handlerThread.start();
        serviceHandler = new Handler(handlerThread.getLooper());
        notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        String channelId = Build.VERSION.SDK_INT >= Build.VERSION_CODES.O ? createNotificationChannel(notificationManager) : "";

        //Create notification for version greater than Oreo
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

        final FirebaseUser childIdSession = dbAuth.getCurrentUser();
        String childId = childIdSession.getUid();
        currentSession = new Session(childId);

        //Thread for fetching data
        runnableCode = new Runnable() {
            @Override
            public void run() {
                currentUser = dbAuth.getCurrentUser();

                if(currentUser == null) {
                    serviceHandler.removeCallbacks(runnableCode);
                    stopSelf();
                }
                else {
                    Log.d(TAG, "Session Monitor is running...");
                    if(currentSession.checkIfListEmpty()) {
                        currentSession.updateSession(getBaseContext());
                        sessionHelper = new SessionHelper(currentSession);
                        sessionHelper.createSessionEntry();
                        sessionHelper.updateChildDisplayName();
                    }
                    else {
                        currentSession.updateSession(getBaseContext());
                        sessionHelper = new SessionHelper(currentSession);
                        sessionHelper.updateSessionEntry();
                    }
                    serviceHandler.postDelayed(runnableCode, SERVICE_FETCH_DATA_RATE);
                }
            }
        };

        serviceHandler.post(runnableCode);
        return START_NOT_STICKY;
    }

    @Override
    public void onDestroy() {
        Log.d(TAG, "Session Monitor Service was destroyed");
        currentSession.updateEndOfSession();
        sessionHelper.updateEndOfSession();
        serviceHandler.removeCallbacks(runnableCode);
        stopSelf();
        super.onDestroy();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
