package com.moritz.musicsyncapp.model.device;

import android.os.Build;

import androidx.annotation.RequiresApi;

import java.util.Arrays;
import java.util.Optional;

public enum EWifiDirectStatusCodes {

    AVAILABLE(3),
    CONNECTED(0),
    FAILED(2),
    INVITED(1),
    UNAVAILABLE(4),
    UNKNOWN(-1);


    private int code;

    EWifiDirectStatusCodes(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }

    public static EWifiDirectStatusCodes valueOf(int value) {
        for (EWifiDirectStatusCodes eWifiDirectStatusCodes : values()) {
            if(eWifiDirectStatusCodes.code == value)
                return eWifiDirectStatusCodes;
        }
        return UNKNOWN;
    }




}
