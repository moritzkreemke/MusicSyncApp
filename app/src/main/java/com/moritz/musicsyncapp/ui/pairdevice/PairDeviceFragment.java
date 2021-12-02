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
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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

    private PairDeviceViewModel viewModel;

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_pair_device, container, false);

        viewModel = new ViewModelProvider(this).get(PairDeviceViewModel.class);

        RecyclerView availableDevicesRV = view.findViewById(R.id.rVAvailableDevices);
        AvailableDevicesAdapter adapter = new AvailableDevicesAdapter();
        availableDevicesRV.setLayoutManager(new LinearLayoutManager(getContext()));

        availableDevicesRV.setAdapter(adapter);
        AndroidMusicSyncFactory.get().getNetworkController(null).discoverDevices();
        //AndroidMusicSyncFactory.get().getNetworkController(null);
        //getActivity().requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);

        return view;
    }
}