package com.moritz.musicsyncapp.controller.p2pnetwork;

import android.util.Log;

import com.moritz.musicsyncapp.model.client.IClient;
import com.moritz.musicsyncapp.model.device.IDevice;

class ClientsManagement {


    private static ClientsManagement _instance;
    public static ClientsManagement get() {
        if(_instance == null)
            _instance = new ClientsManagement();
        return _instance;
    }

    public void registerClient (IClient client) {
        Log.i(ClientsManagement.class.toString(), "wow cleint registerd");
    }

    public void unregisterClient (IClient client)
    {

    }


}
