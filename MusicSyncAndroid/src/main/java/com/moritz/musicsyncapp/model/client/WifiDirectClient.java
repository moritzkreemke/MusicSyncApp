package com.moritz.musicsyncapp.model.client;

import com.moritz.musicsyncapp.model.device.IDevice;
import com.moritz.musicsyncapp.model.device.WifiDirectDevice;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

public class WifiDirectClient implements IClient {

    private IDevice device;
    private InputStream inputStream;
    private OutputStream outputStream;
    private OnMessageRecivedEvent recivedEvent;


    public WifiDirectClient(IDevice device, InputStream inputStream, OutputStream outputStream, OnMessageRecivedEvent recivedEvent) {
        this.device = device;
        this.inputStream = inputStream;
        this.outputStream = outputStream;
        this.recivedEvent = recivedEvent;
    }

    public IDevice getDevice() {
        return device;
    }

    @Override
    public String getID() {
        return device.getDisplayName();
    }

    public interface OnMessageRecivedEvent {
        void onRevive (String messageRaw);
    }
}
