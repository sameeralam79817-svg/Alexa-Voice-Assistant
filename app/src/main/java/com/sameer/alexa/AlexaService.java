package com.sameer.alexa;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.content.pm.ServiceInfo;
import android.os.Build;
import android.os.IBinder;

public class AlexaService extends Service {

    private static final String CHANNEL_ID = "alexa_voice";

    @Override
    public void onCreate() {
        super.onCreate();

        NotificationManager manager =
            getSystemService(NotificationManager.class);

        NotificationChannel channel =
            new NotificationChannel(
                CHANNEL_ID,
                "Alexa Voice Service",
                NotificationManager.IMPORTANCE_LOW
            );

        manager.createNotificationChannel(channel);

        Notification notification =
            new Notification.Builder(this, CHANNEL_ID)
                .setContentTitle("Alexa Assistant")
                .setContentText("Alexa voice service is active")
                .setSmallIcon(android.R.drawable.ic_btn_speak_now)
                .build();

        if (Build.VERSION.SDK_INT >= 29) {
            startForeground(
                1,
                notification,
                ServiceInfo.FOREGROUND_SERVICE_TYPE_MICROPHONE
            );
        } else {
            startForeground(1, notification);
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
