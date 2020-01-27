package com.example.fitrunner.MusicPlayer;

import android.app.Application;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.media.AudioManager;
import android.opengl.Visibility;

public class MusicChannel extends Application {
    public static final String channel_id="com.example.fitrunner";
    public static final String channel_name="musicplayer";

    @Override
    public void onCreate() {
        super.onCreate();
        createNotification();
    }

    public void createNotification(){
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationChannel channel=new NotificationChannel(
                    channel_id,
                    channel_name,
                    NotificationManager.IMPORTANCE_DEFAULT
            );
            channel.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);
            channel.enableLights(true);
            channel.setSound(null,null);

            NotificationManager manager=getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel);
        }

    }
}
