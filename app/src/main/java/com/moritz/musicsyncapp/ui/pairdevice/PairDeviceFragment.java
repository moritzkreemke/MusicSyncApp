package com.moritz.musicsyncapp.ui.pairdevice;

import android.net.wifi.p2p.WifiP2pDevice;
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

import com.moritz.musicsyncapp.controller.p2pnetwork.events.P2PNetworkControllerDiscoverDevicesEvent;
import com.moritz.musicsyncapp.model.device.IDevice;

import java.util.ArrayList;
import java.util.List;


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


        RecyclerView availableDevicesRV = view.findViewById(R.id.recyler_view_all_devices);
        AvailableDevicesAdapter adapterAvailableDevices = new AvailableDevicesAdapter();
        availableDevicesRV.setAdapter(adapterAvailableDevices);
        availableDevicesRV.setLayoutManager(new LinearLayoutManager(getContext()));

        RecyclerView connectedDevicesRV = view.findViewById(R.id.recycler_view_connected_devices);
        ConnectedDevicesAdapter connectedDevicesAdapter = new ConnectedDevicesAdapter();
        connectedDevicesRV.setAdapter(connectedDevicesAdapter);
        connectedDevicesRV.setLayoutManager(new LinearLayoutManager(getContext()));

        AndroidMusicSyncFactory.get().getNetworkController(null).discoverDevices(new P2PNetworkControllerDiscoverDevicesEvent() {
            @Override
            public void onDevicesFound(IDevice[] iDevices) {
                List<IDevice> connected = new ArrayList<>();
                List<IDevice> others = new ArrayList<>();

                for (IDevice iDevice : iDevices) {
                    if(iDevice.getStatus() == WifiP2pDevice.CONNECTED)
                        connected.add(iDevice);
                    else
                        others.add(iDevice);
                }

                connectedDevicesAdapter.setDevices(connected.toArray(new IDevice[connected.size()]));
                adapterAvailableDevices.setDevices(others.toArray(new IDevice[others.size()]));
            }

            @Override
            public void onFailure(int i) {

            }

            @Override
            public void onFinished() {

            }
        }, true);
        //AndroidMusicSyncFactory.get().getNetworkController(null);
        //getActivity().requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);

        return view;
    }
}