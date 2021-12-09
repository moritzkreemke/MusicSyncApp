package com.moritz.musicsyncapp.controller.p2pnetwork;


import android.util.Log;

import com.moritz.musicsyncapp.AndroidMusicSyncFactory;
import com.moritz.musicsyncapp.model.device.IDevice;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.UnknownHostException;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;

class CommuincationClient {

    private static CommuincationClient _instance;

    public synchronized static CommuincationClient get(WifiDirectControllerAndroid wifiDirectControllerAndroid) {
        if(_instance == null)
            _instance = new CommuincationClient(wifiDirectControllerAndroid);
        return _instance;
    }


    private WifiDirectControllerAndroid wifiDirectControllerAndroid;
    private Socket socket = null;
    private AtomicBoolean socketCreated = new AtomicBoolean(false);

    public CommuincationClient(WifiDirectControllerAndroid wifiDirectControllerAndroid) {
        this.wifiDirectControllerAndroid = wifiDirectControllerAndroid;
    }

    public void connectToServer (InetAddress serverAddr) {

        if(socketCreated.get())
            return;

        Executors.newSingleThreadExecutor().execute(new Runnable() {
            @Override
            public void run() {
                try {

                    socket = new Socket();
                    socket.bind(null);
                    socket.connect(new InetSocketAddress(serverAddr, 10245) , 500);
                    socketCreated.set(true);
                    System.out.println("CONNECTED CLIENT");

                    DataOutputStream outputStream = new DataOutputStream(socket.getOutputStream());
                    outputStream.writeBytes(wifiDirectControllerAndroid.getDeviceSelf().getID());
                    outputStream.flush();
                    outputStream.close();

                } catch (UnknownHostException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    //TODO nicht gut so, aber who cared xD
                    try {
                        Thread.sleep(2000);
                        connectToServer(serverAddr);
                    } catch (InterruptedException interruptedException) {
                        interruptedException.printStackTrace();
                    }
                    e.printStackTrace();
                } finally {
                }
            }
        });

    }


}
