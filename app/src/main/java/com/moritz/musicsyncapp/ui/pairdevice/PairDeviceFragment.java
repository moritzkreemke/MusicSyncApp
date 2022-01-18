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
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.moritz.musicsyncapp.AndroidMusicSyncFactory;
import com.moritz.musicsyncapp.R;

import com.moritz.musicsyncapp.controller.p2pnetwork.WifiDirectControllerAndroid;
import com.moritz.musicsyncapp.controller.p2pnetwork.events.P2PNetworkControllerDiscoverDevicesEvent;
import com.moritz.musicsyncapp.databinding.FragmentPairDeviceBinding;
import com.moritz.musicsyncapp.model.device.EWifiDirectStatusCodes;
import com.moritz.musicsyncapp.model.device.IDevice;
import com.moritz.musicsyncapp.model.device.WifiDirectDevice;
import com.moritz.musicsyncapp.model.session.ISession;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 * create an instance of this fragment.
 */
public class PairDeviceFragment extends Fragment {

    private PairDeviceViewModel viewModel;

    private TextView textView_header;
    private Button changeGroupOwnerBtn;

    private ProgressBar progressBarSearchDevices;
    private Button restartDeviceSearch;
    private TextView deviceSearchStatus;

    private AvailableDevicesAdapter adapterAvailableDevices;
    private ConnectedDevicesAdapter connectedDevicesAdapter;
    private WifiDirectControllerAndroid wifiDirectControllerAndroid;

    private PropertyChangeListener pcs;
    private PropertyChangeListener sessionChanged;

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_pair_device, container, false);


        viewModel = new ViewModelProvider(this).get(PairDeviceViewModel.class);

        wifiDirectControllerAndroid = (WifiDirectControllerAndroid) AndroidMusicSyncFactory.get().getNetworkController(null);

        textView_header = view.findViewById(R.id.text_view_pair_devcices_header);
        changeGroupOwnerBtn = view.findViewById(R.id.btn_become_group_leader);
        changeGroupOwnerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AndroidMusicSyncFactory.get().getCommuicationClient().cancelConnect();

                WifiDirectControllerAndroid controllerAndroid = (WifiDirectControllerAndroid) AndroidMusicSyncFactory.get().getNetworkController(null);
                controllerAndroid.startCommuincation();
            }
        });


        RecyclerView availableDevicesRV = view.findViewById(R.id.recyler_view_all_devices);
        adapterAvailableDevices = new AvailableDevicesAdapter();
        availableDevicesRV.setLayoutManager(new LinearLayoutManager(getContext()));
        availableDevicesRV.setAdapter(adapterAvailableDevices);


        RecyclerView connectedDevicesRV = view.findViewById(R.id.recycler_view_connected_devices);
        connectedDevicesAdapter = new ConnectedDevicesAdapter();
        connectedDevicesRV.setLayoutManager(new LinearLayoutManager(getContext()));
        connectedDevicesRV.setAdapter(connectedDevicesAdapter);



        progressBarSearchDevices = view.findViewById(R.id.progressBar_search_devices);
        restartDeviceSearch = view.findViewById(R.id.btn_restart_device_search);
        restartDeviceSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AndroidMusicSyncFactory.get().getNetworkController(null).discoverDevices();
            }
        });
        deviceSearchStatus = view.findViewById(R.id.text_view_decive_search_status);

        pcs = new PropertyChangeListener() {
            @Override
            public void propertyChange(PropertyChangeEvent evt) {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if(evt.getPropertyName().equals(WifiDirectControllerAndroid.DISCOVERY_STATE_CHANGED_EVENT)) {
                            WifiDirectControllerAndroid.E_DISCOVERY_STATES discovery_state = (WifiDirectControllerAndroid.E_DISCOVERY_STATES) evt.getNewValue();
                            if(discovery_state.equals(WifiDirectControllerAndroid.E_DISCOVERY_STATES.RUNNING)) {
                                progressBarSearchDevices.setIndeterminate(true);
                                restartDeviceSearch.setEnabled(false);
                                deviceSearchStatus.setText("running");
                            } else if(discovery_state.equals(WifiDirectControllerAndroid.E_DISCOVERY_STATES.STOPPED)) {
                                progressBarSearchDevices.setIndeterminate(false);
                                restartDeviceSearch.setEnabled(true);
                                deviceSearchStatus.setText("stopped");
                            }
                        } else if(evt.getPropertyName().equals(WifiDirectControllerAndroid.DEVICES_CHANGED_EVENT)) {
                            IDevice[] devices = (IDevice[]) evt.getNewValue();
                            ArrayList<IDevice> connected = new ArrayList<>();
                            ArrayList<IDevice> otherDevices = new ArrayList<>();

                            for (IDevice device : devices) {
                                if(device.getStatus() == EWifiDirectStatusCodes.CONNECTED.getCode()) {
                                    connected.add(device);
                                } else {
                                    otherDevices.add(device);
                                }
                            }
                            connectedDevicesAdapter.setDevices(connected.toArray(new IDevice[connected.size()]));
                            adapterAvailableDevices.setDevices(otherDevices.toArray(new IDevice[otherDevices.size()]));
                        }
                    }
                });

            }
        };

        sessionChanged = new PropertyChangeListener() {
            @Override
            public void propertyChange(PropertyChangeEvent evt) {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        ISession session = (ISession) evt.getNewValue();
                        if(!session.exits()) {
                            textView_header.setText("please connect to other devices");
                        } else {
                            if(AndroidMusicSyncFactory.get().getNetworkController(null).isOwner()) {
                                textView_header.setText("you are the DJ");
                            } else {
                                textView_header.setText("you are listener");
                            }
                        }
                    }
                });
            }
        };


        initValues();
        AndroidMusicSyncFactory.get().getSessionController().addSessionChangeListener(sessionChanged);
        wifiDirectControllerAndroid.addPropertyChangeListener(pcs);

        AndroidMusicSyncFactory.get().getNetworkController(null).discoverDevices();

        return view;
    }

    private void initValues () {
        ISession session = viewModel.getSession();

        if (!session.exits()) {
            textView_header.setText("Please connect to other devices");
        } else {
            if (AndroidMusicSyncFactory.get().getNetworkController(null).isOwner()) {
                textView_header.setText("you are the DJ");
            } else {
                textView_header.setText("you are the listener");
            }
        }

        if(viewModel.getDiscoveryState().equals(WifiDirectControllerAndroid.E_DISCOVERY_STATES.STOPPED)) {
            progressBarSearchDevices.setIndeterminate(false);
            restartDeviceSearch.setEnabled(true);
            deviceSearchStatus.setText("stopped");
        } else if(viewModel.getDiscoveryState().equals(WifiDirectControllerAndroid.E_DISCOVERY_STATES.RUNNING)) {
            progressBarSearchDevices.setIndeterminate(false);
            restartDeviceSearch.setEnabled(true);
            deviceSearchStatus.setText("stopped");
        }

        connectedDevicesAdapter.setDevices(viewModel.getConnectedDevices());
        adapterAvailableDevices.setDevices(viewModel.getAvailableDevices());
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        wifiDirectControllerAndroid.removePropertyChangeListener(pcs);
        AndroidMusicSyncFactory.get().getSessionController().removeSessionChangeListener(sessionChanged);
    }
}