package com.moritz.musicsyncapp;

import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;

import com.moritz.musicsyncapp.controller.sound.LocalSoundController;

public class MusicSyncAppApplication extends Application {


    @Override
    public void onCreate() {
        super.onCreate();
        AndroidMusicSyncFactory.init(getApplicationContext());
    }
}
