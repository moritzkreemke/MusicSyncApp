package com.moritz.musicsyncapp.model.client;

import com.moritz.musicsyncapp.model.device.IDevice;
import com.moritz.musicsyncapp.model.device.WifiDirectDevice;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

public class WifiDirectClient implements IClient{
    private String inetAddress;
    private IDevice device;


    public WifiDirectClient(String inetAddress, IDevice device) {
        this.inetAddress = inetAddress;
        this.device = device;
    }

    @Override
    public String getAddress() {
        return inetAddress;
    }

    @Override
    public IDevice getDevice() {
        return device;
    }
}
