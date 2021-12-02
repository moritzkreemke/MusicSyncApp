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
import android.os.Build;
import android.os.Looper;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
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
    private WifiDirectBroadcastReciver receiver;
    private IntentFilter intentFilter;

    private List<IDevice> devices = new ArrayList<>();
    private List<IDevice> connectedDevices = new ArrayList<>();

    private IDevice deviceSelf;
    private IClient clientSelf;

    WifiDirectControllerAndroid wifiDirectControllerAndroid;

    public enum E_WIFI_STATES {
        UNDEFINED,
        ENABLED,
        DISABLED
    }
    E_WIFI_STATES wifi_state;



    public WifiDirectControllerAndroid(Context context) {
        this.context = context;
        this.wifi_state = E_WIFI_STATES.UNDEFINED;
        wifiDirectControllerAndroid = this;

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
                checkGroupStatusAndConnectToSockets();

            } else if(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION.equals(action)) {

                WifiP2pDevice device = intent.getParcelableExtra(WifiP2pManager.EXTRA_WIFI_P2P_DEVICE);
                deviceSelf = new WifiDirectDevice(device);
            }
        }

        public void checkGroupStatusAndConnectToSockets ()
        {
            manager.requestConnectionInfo(channel, new WifiP2pManager.ConnectionInfoListener() {
                @Override
                public void onConnectionInfoAvailable(WifiP2pInfo wifiP2pInfo) {

                    if(wifiP2pInfo.groupFormed == false) {
                        Log.d("WIFIDIRECT", "no group formed");
                        manager.createGroup(channel, new WifiP2pManager.ActionListener() {
                            @Override
                            public void onSuccess() {
                                Log.d("WIFI_DIRECT", "SUCCESS");
                            }

                            @Override
                            public void onFailure(int i) {
                                Log.d("WIFI_DIRDCT", "FAILURE");
                            }
                        });
                    } else {
                        Log.d("WIFI_DIRECT", "Group formed!");
                        if(wifiP2pInfo.isGroupOwner)
                        {
                            CommuicationServer.get().startServer(wifiP2pInfo.groupOwnerAddress.toString(),
                                    deviceSelf, wifiDirectControllerAndroid);
                        } else {
                            CommuincationClient.get().connectToServer(wifiP2pInfo.groupOwnerAddress.toString(), deviceSelf);
                        }
                    }

                }
            });
        }

         void startDiscoverPeers () {
            manager.discoverPeers(channel, new WifiP2pManager.ActionListener() {
                @Override
                public void onSuccess() {
                    //ignore for now, just log?
                }

                @Override
                public void onFailure(int i) {
                    //ignore for now, just log?
                    System.out.println("mist");
                }
            });
        }
    }

    @Override
    public void discoverDevices(){
        receiver.startDiscoverPeers();
    }


    @SuppressLint("MissingPermission")
    @Override
    public IClient connectDevice(IDevice iDevice) {

        WifiP2pConfig config = new WifiP2pConfig();
        config.deviceAddress = iDevice.getID();

        manager.connect(channel, config, new WifiP2pManager.ActionListener() {
            @Override
            public void onSuccess() {
                connectedDevices.add(iDevice);
                receiver.checkGroupStatusAndConnectToSockets();
            }

            @Override
            public void onFailure(int i) {
                Log.d(this.getClass().toString(), "onFailure when connecting...");
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

    public IDevice[] getConnectedDevices() {
        return connectedDevices.toArray(new IDevice[connectedDevices.size()]);
    }

    public IDevice getConnectedDeviceByMacAddress (String macAddr)
    {
        for (IDevice connectedDevice : getConnectedDevices()) {
            if(connectedDevice.getID().equals(macAddr))
                return connectedDevice;
        }
        return null;
    }
}
