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


    MusicPlaybackService mService;
    boolean mBound = false;


    @Override
    public void onCreate() {
        super.onCreate();

        AndroidMusicSyncFactory.init(getApplicationContext());
        createNotificationChannel();

        Intent intent = new Intent(this, MusicPlaybackService.class);
        bindService(intent, connection, Context.BIND_AUTO_CREATE);
        startService(intent);
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


    /**
     * Defines callbacks for service binding, passed to bindService()
     */
    private ServiceConnection connection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName className,
                                       IBinder service) {
            // We've bound to LocalService, cast the IBinder and get LocalService instance
            MusicPlaybackService.LocalBinder binder = (MusicPlaybackService.LocalBinder) service;
            mService = binder.getService();
            mBound = true;
            AndroidMusicSyncFactory.get().setLocalSoundController(new LocalSoundController(binder));
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            mBound = false;
        }
    };
}
