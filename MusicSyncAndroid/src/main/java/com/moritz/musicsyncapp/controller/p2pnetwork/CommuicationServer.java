package com.moritz.musicsyncapp.controller.p2pnetwork;

import android.util.Log;

import com.moritz.musicsyncapp.model.client.WifiDirectClient;
import com.moritz.musicsyncapp.model.device.IDevice;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;

class CommuicationServer {


    private static CommuicationServer _instance;

    public static CommuicationServer get() {
        if(_instance == null)
            _instance = new CommuicationServer();
        return _instance;
    }

    private ServerSocket serverSocket;
    private boolean server_running;
    private List<Socket> connectedSockets = new ArrayList<>();
    private WifiDirectControllerAndroid wifiDirectControllerAndroid;

    public void stopServer ()  {

    }

    public void startServer (String inetAddress, IDevice self, WifiDirectControllerAndroid wifiDirectControllerAndroid)
    {
        if(serverSocket != null) {
            stopServer();
        }
        try {
            this.wifiDirectControllerAndroid = wifiDirectControllerAndroid;
            serverSocket = new ServerSocket(8888);
            server_running = true;
            ClientsManagement.get().registerClient(new WifiDirectClient(inetAddress, self));

             Executors.newSingleThreadExecutor().execute(() -> {
                 while (server_running) {
                    waitForIncommingConnections();
                 }
             });

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void waitForIncommingConnections () {

        try {
            Socket socket = serverSocket.accept();
            //at the beginning, a device has to send a hello message
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            //Format DeviceName;MACAddres
            String line;
            String macAddress = "";
            while ((line = bufferedReader.readLine()) != null) {
                String deviceName = line.split(";")[0];
                macAddress = line.split(";")[1];
            }
            IDevice device = wifiDirectControllerAndroid.getConnectedDeviceByMacAddress(macAddress);
            if(device == null) {
                Log.d(CommuicationServer.class.toString(), "Shit");
            } else {
                ClientsManagement.get().registerClient(new WifiDirectClient(socket.getInetAddress().toString(), device));
            }


        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}
