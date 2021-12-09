package com.moritz.musicsyncapp.controller.p2pnetwork;

import android.util.Log;

import com.moritz.musicsyncapp.controller.p2pnetwork.events.P2PNetworkControllerDevicesFoundEvent;
import com.moritz.musicsyncapp.model.client.IClient;
import com.moritz.musicsyncapp.model.client.WifiDirectClient;
import com.moritz.musicsyncapp.model.device.IDevice;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;

class CommuicationServer {


    private static CommuicationServer _instance;

    public synchronized static CommuicationServer get(WifiDirectControllerAndroid wifiDirectControllerAndroid) {
        if(_instance == null)
            _instance = new CommuicationServer(wifiDirectControllerAndroid);
        return _instance;
    }

    private WifiDirectControllerAndroid wifiDirectControllerAndroid;
    private ServerSocket serverSocket;
    private static volatile AtomicBoolean server_running = new AtomicBoolean(false);

    private List<IClient> clients = new ArrayList<>();

    public CommuicationServer(WifiDirectControllerAndroid wifiDirectControllerAndroid) {
        this.wifiDirectControllerAndroid = wifiDirectControllerAndroid;
    }

    public void stopServer ()  {

    }

    public synchronized void startServer ()
    {
            if(server_running.get())
                return;
            server_running.getAndSet(true);

            Executors.newSingleThreadExecutor().execute(new Runnable() {
                @Override
                public void run() {
                    try {

                        serverSocket = new ServerSocket(10245);

                        while (server_running.get()) {
                            waitForIncommingConnections();
                        }

                    } catch (IOException e) {
                        e.printStackTrace();
                        System.out.println("SERVER STOPPED");
                        stopServer();
                    } finally {
                        server_running.set(false);
                    }
                }
            });


    }

    private void waitForIncommingConnections () {

       try {

           Socket socket = serverSocket.accept();
            //at the beginning, a device has to send a hello message

           InputStream inputStream = socket.getInputStream();
           OutputStream outputStream = socket.getOutputStream();

           ByteArrayOutputStream result = new ByteArrayOutputStream();
           byte[] firstMessage = new byte[17];
           int length;

           while ((length = inputStream.read(firstMessage)) != -1) {
                result.write(firstMessage, 0, length);
           }

           String macAddr = result.toString(StandardCharsets.UTF_8.name());

           wifiDirectControllerAndroid.requestDevices(new P2PNetworkControllerDevicesFoundEvent() {
               @Override
               public void onDevicesFound(IDevice[] iDevices) {
                   IDevice device = IDevice.getDeviceById(iDevices, macAddr);
                   if(device == null) {
                       System.out.println("SHIIIIIIIIT");
                       return;
                   }

                   clients.add(new WifiDirectClient(device, inputStream, outputStream, new WifiDirectClient.OnMessageRecivedEvent() {
                       @Override
                       public void onRevive(String messageRaw) {
                            Log.i("JOO", messageRaw);
                       }
                   }));
               }
           });

        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("SERVER STOPPED!");
        }
    }


}
