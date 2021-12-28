package com.moritz.musicsyncapp;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;

import com.moritz.musicsyncapp.controller.commuication.client.ICommunicationClient;
import com.moritz.musicsyncapp.controller.commuication.server.ICommunicationServer;
import com.moritz.musicsyncapp.controller.p2pnetwork.IP2PNetworkController;
import com.moritz.musicsyncapp.controller.p2pnetwork.WifiDirectControllerAndroid;
import com.moritz.musicsyncapp.controller.playlist.IPlaylistController;
import com.moritz.musicsyncapp.controller.playlist.PlaylistControllerAndroidImpl;
import com.moritz.musicsyncapp.controller.session.ISessionController;
import com.moritz.musicsyncapp.controller.snapdroid.ISnapdroidClient;
import com.moritz.musicsyncapp.controller.snapdroid.client.AndroidSnapdroidClientImpl;
import com.moritz.musicsyncapp.controller.snapdroid.server.AndroidSnapdroidServerImpl;
import com.moritz.musicsyncapp.controller.snapdroid.ISnapdroidServer;
import com.moritz.musicsyncapp.controller.sound.ISoundController;
import com.moritz.musicsyncapp.controller.sound.LocalSoundController;

public class AndroidMusicSyncFactory implements IAndroidSyncFactory{

    public static final String MUSIC_PLAYER_CHANNEL_ID = "Music Player Channel ID";
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

            NotificationChannel musicChannel = new NotificationChannel(MUSIC_PLAYER_CHANNEL_ID, "Music Player", NotificationManager.IMPORTANCE_DEFAULT);
            manager.createNotificationChannel(musicChannel);
        }
    }

    @Override
    public IPlaylistController getPlaylistController() {
        return new PlaylistControllerAndroidImpl(context);
    }

    private ISoundController localSoundController;
    @Override
    public ISoundController getLocalSoundController() {

        if(localSoundController == null)
            localSoundController = new LocalSoundController(context);
        return localSoundController;
    }

    @Override
    public IP2PNetworkController getNetworkController(String s) {
        if(networkController == null)
            networkController = new WifiDirectControllerAndroid(context);
        return networkController;
    }

    @Override
    public ISessionController getSessionController() {
        return MusicSyncFactory.getInstance().getSessionController();
    }

    @Override
    public ICommunicationClient getCommuicationClient() {
        return MusicSyncFactory.getInstance().getCommuicationClient();
    }

    @Override
    public ICommunicationServer getCommuicationServer() {
        return MusicSyncFactory.getInstance().getCommuicationServer();
    }

    private ISnapdroidServer snapdroidServer;
    @Override
    public ISnapdroidServer getSnapdroidServer() {
        if(snapdroidServer == null)
            snapdroidServer = new AndroidSnapdroidServerImpl(context);
        return snapdroidServer;
    }

    private ISnapdroidClient snapdroidClient;
    @Override
    public ISnapdroidClient getSnapdroidClient() {
        if(snapdroidClient == null) {
            snapdroidClient = new AndroidSnapdroidClientImpl(context);
        }
        return snapdroidClient;
    }
}
