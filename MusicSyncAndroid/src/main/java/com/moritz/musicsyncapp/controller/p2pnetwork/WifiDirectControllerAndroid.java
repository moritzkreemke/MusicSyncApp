package com.moritz.musicsyncapp.controller.p2pnetwork;

import com.moritz.musicsyncapp.model.client.IClient;
import com.moritz.musicsyncapp.model.device.IDevice;

public class WifiDirectControllerAndroid implements IP2PNetworkController{

    @Override
    public IDevice[] findDevices() {
        return new IDevice[0];
    }

    @Override
    public IClient connectDevice(IDevice iDevice) {
        return null;
    }

    @Override
    public void sendMessage(byte[] bytes, IClient iClient) {

    }
}
