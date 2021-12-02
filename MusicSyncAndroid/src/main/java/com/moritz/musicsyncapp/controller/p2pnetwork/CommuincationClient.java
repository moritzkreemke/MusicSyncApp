package com.moritz.musicsyncapp.controller.p2pnetwork;


import android.util.Log;

import com.moritz.musicsyncapp.model.device.IDevice;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.concurrent.Executors;

class CommuincationClient {

    private static CommuincationClient _instance;

    public static CommuincationClient get() {
        if(_instance == null)
            _instance = new CommuincationClient();
        return _instance;
    }

    public void connectToServer (String inet, IDevice selfDevice) {


            Executors.newSingleThreadExecutor().execute(new Runnable() {
                @Override
                public void run() {
                    try {
                        Thread.sleep(1000);
                        Log.i(CommuincationClient.class.toString(), "connect now");
                        Socket socket = new Socket(inet, 8888);

                        DataOutputStream outputStream = new DataOutputStream(socket.getOutputStream());
                        outputStream.writeBytes(selfDevice.getDisplayName() + ";" + selfDevice.getID());
                        outputStream.flush();

                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    } catch (UnknownHostException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            });



    }


}
