package com.moritz.musicsyncapp.model.device;

import android.net.wifi.p2p.WifiP2pDevice;

public class WifiDirectDevice implements IDevice{

    private WifiP2pDevice device;

    public WifiDirectDevice(WifiP2pDevice device) {
        this.device = device;
    }

    @Override
    public String getID() {
        return null;
    }

    @Override
    public String getDisplayName() {
        return null;
    }
}
