package com.moritz.musicsyncapp.ui.pairdevice;

import static android.os.Looper.getMainLooper;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.p2p.WifiP2pManager;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.moritz.musicsyncapp.AndroidMusicSyncFactory;
import com.moritz.musicsyncapp.R;


/**
 * A simple {@link Fragment} subclass.
 * create an instance of this fragment.
 */
public class PairDeviceFragment extends Fragment {


    WifiP2pManager manager;
    WifiP2pManager.Channel channel;
    BroadcastReceiver receiver;
    IntentFilter intentFilter;



    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_pair_device, container, false);
        AndroidMusicSyncFactory.get().getNetworkController(null);
        //getActivity().requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);

        return view;
    }
}