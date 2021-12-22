package com.moritz.musicsyncapp;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;

import androidx.annotation.Nullable;

import com.moritz.musicsyncapp.controller.p2pnetwork.IP2PNetworkController;
import com.moritz.musicsyncapp.controller.p2pnetwork.WifiDirectControllerAndroid;
import com.moritz.musicsyncapp.controller.playlist.IPlaylistController;
import com.moritz.musicsyncapp.controller.playlist.PlaylistControllerAndroidImpl;
import com.moritz.musicsyncapp.controller.sound.ISoundController;

public class AndroidMusicSyncFactory implements IAndroidSyncFactory{

    public static final String CHANNEL_ID = "Pair Notification Channel";
    private static AndroidMusicSyncFactory _instance;



    public static AndroidMusicSyncFactory get ()
    {
        if(_instance == null)
            throw new RuntimeException("not initalized");
        return _instance;
    }

    public static void init (Context context)
    {
        if(_instance != null)
            throw new RuntimeException("Factory already initzilized");
        _instance = new AndroidMusicSyncFactory(context);
    }

    private Context context;
    private ISoundController localSoundController;
    private IP2PNetworkController networkController;

    private AndroidMusicSyncFactory (Context context)
    {
        this.context = context;
        createNoticationChannel();
    }

    private void createNoticationChannel ()
    {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationChannel serviceChannel = new NotificationChannel(CHANNEL_ID,
                    "Test channel",
                    NotificationManager.IMPORTANCE_DEFAULT);

            NotificationManager manager = context.getSystemService(NotificationManager.class);
            manager.createNotificationChannel(serviceChannel);
        }
    }

    @Override
    public IPlaylistController getPlaylistController() {
        return new PlaylistControllerAndroidImpl(context);
    }

    public static void registerSoundController (ISoundController soundController)
    {
        get().localSoundController = soundController;
    }

    @Nullable
    @Override
    public ISoundController getLocalSoundController() {

        return localSoundController;
    }

    @Override
    public IP2PNetworkController getNetworkController(String s) {
        if(networkController == null)
            networkController = new WifiDirectControllerAndroid(context);
        return networkController;
    }
}
