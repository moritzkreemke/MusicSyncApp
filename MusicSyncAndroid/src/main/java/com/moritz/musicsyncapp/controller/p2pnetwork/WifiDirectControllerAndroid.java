package com.moritz.musicsyncapp.controller.p2pnetwork;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.p2p.WifiP2pConfig;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pDeviceList;
import android.net.wifi.p2p.WifiP2pInfo;
import android.net.wifi.p2p.WifiP2pManager;
import android.os.Looper;

import androidx.lifecycle.LiveData;

import com.moritz.musicsyncapp.controller.p2pnetwork.events.IP2PNetworkControllerDevicesFoundChangedEvent;
import com.moritz.musicsyncapp.model.client.IClient;
import com.moritz.musicsyncapp.model.device.IDevice;
import com.moritz.musicsyncapp.model.device.WifiDirectDevice;

import java.util.ArrayList;
import java.util.List;

public class WifiDirectControllerAndroid extends P2PNetworkControllerBase{



    private Context context;
    private WifiP2pManager manager;
    private WifiP2pManager.Channel channel;
    private BroadcastReceiver receiver;
    private IntentFilter intentFilter;

    private List<IDevice> devices = new ArrayList<>();

    public enum E_WIFI_STATES {
        UNDEFINED,
        ENABLED,
        DISABLED
    }
    E_WIFI_STATES wifi_state;


    public WifiDirectControllerAndroid(Context context) {
        this.context = context;
        this.wifi_state = E_WIFI_STATES.UNDEFINED;

        manager = (WifiP2pManager) context.getSystemService(Context.WIFI_P2P_SERVICE);
        channel = manager.initialize(context, context.getMainLooper(), null);

        receiver = new WifiDirectBroadcastReciver();
        intentFilter = new IntentFilter();
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION);
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION);
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION);
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION);

        context.registerReceiver(receiver, intentFilter);

    }

    @SuppressLint("MissingPermission")
    public class WifiDirectBroadcastReciver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            //the adapter state, if wifi on or off?
            if(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION.equals(action)) {
                int state = intent.getIntExtra(WifiP2pManager.EXTRA_WIFI_STATE, -1);
                if(state == WifiP2pManager.WIFI_P2P_STATE_ENABLED) {
                    wifi_state = E_WIFI_STATES.ENABLED;
                    startDiscoverPeers();
                } else {
                    wifi_state = E_WIFI_STATES.DISABLED;
                }
            } else if (WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION.equals(action)) {
                // Call WifiP2pManager.requestPeers() to get a list of current peers
                devices.clear();

                manager.requestPeers(channel, new WifiP2pManager.PeerListListener() {
                    @Override
                    public void onPeersAvailable(WifiP2pDeviceList wifiP2pDeviceList) {
                        for (WifiP2pDevice wifiP2pDevice : wifiP2pDeviceList.getDeviceList()) {
                            devices.add(new WifiDirectDevice(wifiP2pDevice));
                        }
                        WifiDirectControllerAndroid.super.onDevicesFoundTrigger(devices.toArray(new IDevice[devices.size()]));
                    }
                });
            } else if(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION.equals(action)) {
                // Respond to new connection or disconnections
                manager.requestConnectionInfo(channel, new WifiP2pManager.ConnectionInfoListener() {
                    @Override
                    public void onConnectionInfoAvailable(WifiP2pInfo wifiP2pInfo) {

                    }
                });
            }
        }

        private void startDiscoverPeers () {
            manager.discoverPeers(channel, new WifiP2pManager.ActionListener() {
                @Override
                public void onSuccess() {
                    //ignore for now, just log?
                }

                @Override
                public void onFailure(int i) {
                    //ignore for now, just log?
                }
            });
        }
    }

    @Override
    public IDevice[] findDevices(){

        return devices.toArray(new IDevice[devices.size()]);
    }

    @Override
    public IClient[] getClients() {
        return new IClient[0];
    }

    @SuppressLint("MissingPermission")
    @Override
    public IClient connectDevice(IDevice iDevice) {

        WifiP2pConfig config = new WifiP2pConfig();
        config.deviceAddress = iDevice.getID();
        manager.connect(channel, config, new WifiP2pManager.ActionListener() {
            @Override
            public void onSuccess() {

            }

            @Override
            public void onFailure(int i) {

            }
        });

        return null;
    }

    @Override
    public void sendMessage(byte[] bytes, IClient iClient) {

    }

    public E_WIFI_STATES getWifi_state() {
        return wifi_state;
    }
}
