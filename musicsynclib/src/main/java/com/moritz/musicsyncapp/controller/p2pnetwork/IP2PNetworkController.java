package com.moritz.musicsyncapp.controller.p2pnetwork;

import com.moritz.musicsyncapp.model.client.IClient;
import com.moritz.musicsyncapp.model.device.IDevice;

public interface IP2PNetworkController {

    IDevice[] findDevices();
    IClient connectDevice(IDevice device);

    void sendMessage (byte[] message, IClient reciver);


}
