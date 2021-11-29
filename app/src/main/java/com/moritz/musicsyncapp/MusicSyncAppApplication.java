package com.moritz.musicsyncapp;

import android.app.Application;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;

import com.moritz.musicsyncapp.controller.sound.LocalSoundController;
import com.moritz.musicsyncapp.services.MusicPlaybackService;

public class MusicSyncAppApplication extends Application {


    @Override
    public void onCreate() {
        super.onCreate();

        AndroidMusicSyncFactory.init(getApplicationContext());
        AndroidMusicSyncFactory.registerSoundController(new LocalSoundController(getApplicationContext()));
        createNotificationChannel();
    }

    private void createNotificationChannel() {

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationChannel serviceChannel = new NotificationChannel(Configuration.CHANNEL_ID,
                    "Test channel",
                    NotificationManager.IMPORTANCE_DEFAULT);

            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(serviceChannel);
        }
    }

}
