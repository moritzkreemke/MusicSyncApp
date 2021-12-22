package com.moritz.musicsyncapp.controller.p2pnetwork.services;


import android.app.Notification;
import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.moritz.musicsyncapp.AndroidMusicSyncFactory;
import com.moritz.musicsyncapp.R;
import com.moritz.musicsyncapp.controller.commuication.CommuicationFactory;
import com.moritz.musicsyncapp.controller.commuication.client.ICommunicationClient;
import com.moritz.musicsyncapp.controller.commuication.events.OnConnectEvent;
import com.moritz.musicsyncapp.controller.commuication.server.ICommunicationServer;
import com.moritz.musicsyncapp.controller.p2pnetwork.WifiDirectControllerAndroid;
import com.moritz.musicsyncapp.model.client.IClient;
import com.moritz.musicsyncapp.model.commuication.events.EventMessage;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.UUID;
import java.util.concurrent.Executors;

public class CommuicationService extends Service {

    public static final String COMMUICATION_SERVICE_IS_SERVER_EXTRA = "COMMUICATION_SERVICE_IS_SERVER_EXTRA";
    public static final String TARGET_INET_ADDR_EXTRA = "CommuincationClient.TARGET_INET_ADDR";
    private final IBinder binder = new LocalBinder();

    private WifiDirectControllerAndroid wifiDirectControllerAndroid;
    private Socket socket = null;


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);

        /*Intent notificationIntent = new Intent(this, CommuincationClient.);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);*/

        Notification notification = new NotificationCompat.Builder(this, AndroidMusicSyncFactory.CHANNEL_ID)
                .setContentTitle("Client Connection")
                .setContentText("Client trying to connect")
                .setSmallIcon(R.drawable.ic_android_black_24dp)
                //.setContentIntent(pendingIntent)
                .build();

        startForeground(1, notification);

        boolean isServer = intent.getBooleanExtra(COMMUICATION_SERVICE_IS_SERVER_EXTRA, true);

        if(isServer) {
            CommuicationFactory.get().getServer().start(8080);
        }

        String inet =  intent.getStringExtra(TARGET_INET_ADDR_EXTRA);
        try {
            CommuicationFactory.get().getClient().connect(InetAddress.getByName(inet), 8080, 10, UUID.randomUUID().toString(), new OnConnectEvent() {
                @Override
                public void success() {
                    CommuicationFactory.get().getClient().sendMessage(EventMessage.EVENT_CLIENTS_UPDATED, IClient.EVENT);
                }

                @Override
                public void onFailure(int i) {

                }
            });
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }



        return START_STICKY;
    }

    public class LocalBinder extends Binder {
        public CommuicationService getService ()
        {
            return CommuicationService.this;
        }

        public void setWifiDirectControllerAndroid (WifiDirectControllerAndroid wifiDirectControllerAndroid)
        {
            getService().wifiDirectControllerAndroid = wifiDirectControllerAndroid;
        }
    }


}
