package com.moritz.musicsyncapp.model.device;

import android.net.wifi.p2p.WifiP2pDevice;

import androidx.annotation.Nullable;

public class WifiDirectDevice implements IDevice{

    private WifiP2pDevice device;

    public WifiDirectDevice(WifiP2pDevice device) {
        this.device = device;
    }

    @Override
    public String getID() {
        return device.deviceAddress;
    }

    @Override
    public String getDisplayName() {
        return device.deviceName;
    }

    @Override
    public int getStatus() {
        return device.status;
    }

    public WifiP2pDevice getNativeDevice() {
        return device;
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        if(obj instanceof IDevice) {
            return getID().equals(((IDevice) obj).getID());
        } else {
            return false;
        }
    }

}
