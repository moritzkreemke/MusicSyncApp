package com.moritz.musicsyncapp.controller.p2pnetwork;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.net.wifi.WpsInfo;
import android.net.wifi.p2p.WifiP2pConfig;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pDeviceList;
import android.net.wifi.p2p.WifiP2pInfo;
import android.net.wifi.p2p.WifiP2pManager;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;

import com.moritz.musicsyncapp.controller.commuication.client.CommunicationClientImpl;
import com.moritz.musicsyncapp.controller.commuication.events.OnReciveMessageEvent;
import com.moritz.musicsyncapp.controller.p2pnetwork.events.P2PNetworkControllerConnectingEvent;
import com.moritz.musicsyncapp.controller.p2pnetwork.events.P2PNetworkControllerDevicesFoundEvent;
import com.moritz.musicsyncapp.controller.p2pnetwork.events.P2PNetworkControllerDiscoverDevicesEvent;
import com.moritz.musicsyncapp.controller.p2pnetwork.services.CommuicationService;
import com.moritz.musicsyncapp.model.client.IClient;
import com.moritz.musicsyncapp.model.commuication.ISendableMessage;
import com.moritz.musicsyncapp.model.commuication.messages.AvailableClientsChanged;
import com.moritz.musicsyncapp.model.device.IDevice;
import com.moritz.musicsyncapp.model.device.WifiDirectDevice;
import com.moritz.musicsyncapp.model.session.ISession;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
@SuppressLint("MissingPermission")
public class WifiDirectControllerAndroid implements IP2PNetworkController {

    public static final String TAG = WifiDirectControllerAndroid.class.toString();
    public static final String DEVICES_CHANGED_EVENT = "devices";
    public static final String WIFI_STATE_CHANGED_EVENT = "wifi_state";
    public static final String DISCOVERY_STATE_CHANGED_EVENT = "discovery_state";

    private PropertyChangeSupport pcs = new PropertyChangeSupport(this);
    private Context context;
    private WifiP2pManager manager;
    private WifiP2pManager.Channel channel;
    private WifiDirectBroadcastReciver receiver;

    private boolean isOwner;
    private IDevice deviceSelf;

    public enum E_WIFI_STATES {
        UNDEFINED,
        ENABLED,
        DISABLED
    }

    public enum E_DISCOVERY_STATES {
        RUNNING,
        STOPPED
    }

    private E_WIFI_STATES wifi_state;
    private E_DISCOVERY_STATES discovery_state;

    private CommuicationService.LocalBinder commuicationServivceBinder;

    private IDevice[] devices;


    public WifiDirectControllerAndroid(Context context) {
        this.context = context;
        this.wifi_state = E_WIFI_STATES.UNDEFINED;
        this.discovery_state = E_DISCOVERY_STATES.STOPPED;
        setDevices(new IDevice[0]);


        receiver = new WifiDirectBroadcastReciver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION);
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_DISCOVERY_CHANGED_ACTION);
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION);
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION);
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION);
        context.registerReceiver(receiver, intentFilter);

        manager = (WifiP2pManager) context.getSystemService(Context.WIFI_P2P_SERVICE);
        channel = manager.initialize(context, context.getMainLooper(), null);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            manager.requestDeviceInfo(channel, new WifiP2pManager.DeviceInfoListener() {
                @Override
                public void onDeviceInfoAvailable(@Nullable WifiP2pDevice wifiP2pDevice) {
                    deviceSelf = new WifiDirectDevice(wifiP2pDevice);
                }
            });
        }

        Intent clientServiceIntent = new Intent(context, CommuicationService.class);
        context.bindService(clientServiceIntent, new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                commuicationServivceBinder = (CommuicationService.LocalBinder) service;
                commuicationServivceBinder.setWifiDirectControllerAndroid(WifiDirectControllerAndroid.this);
            }

            @Override
            public void onServiceDisconnected(ComponentName name) {

            }
        }, Context.BIND_AUTO_CREATE);

        manager.requestConnectionInfo(channel, new WifiP2pManager.ConnectionInfoListener() {
            @Override
            public void onConnectionInfoAvailable(WifiP2pInfo wifiP2pInfo) {
                startCommuincation();
            }
        });
    }

    @SuppressLint("MissingPermission")
    public class WifiDirectBroadcastReciver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();

            //the adapter state, if wifi on or off?
            if (WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION.equals(action)) {
                Log.d(TAG, "WIFI_P2P_STATE_CHANGED_ACTION triggered");
                int state = intent.getIntExtra(WifiP2pManager.EXTRA_WIFI_STATE, -1);
                if (state == WifiP2pManager.WIFI_P2P_STATE_ENABLED) {
                    setWifi_state(E_WIFI_STATES.ENABLED);
                } else {
                     setWifi_state(E_WIFI_STATES.DISABLED);
                }
            } else if (WifiP2pManager.WIFI_P2P_DISCOVERY_CHANGED_ACTION.equals(action)) {
                Log.d(TAG, "WIFI_P2P_DISCOVERY_CHANGED_ACTION trigged");
                int extra = intent.getIntExtra(WifiP2pManager.EXTRA_DISCOVERY_STATE, -1);
                if (extra == WifiP2pManager.WIFI_P2P_DISCOVERY_STARTED) {
                    setDiscovery_state(E_DISCOVERY_STATES.RUNNING);
                } else if (extra == WifiP2pManager.WIFI_P2P_DISCOVERY_STOPPED) {
                    setDiscovery_state(E_DISCOVERY_STATES.STOPPED);
                }
            } else if (WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION.equals(action)) {
                // Call WifiP2pManager.requestPeers() to get a list of current peers
                Log.d(TAG, "WIFI_P2P_PEERS_CHANGED_ACTION triggered");
                WifiP2pDeviceList deviceList = intent.getParcelableExtra(WifiP2pManager.EXTRA_P2P_DEVICE_LIST);
                List<IDevice> ndevices = new ArrayList<>();
                for (WifiP2pDevice device : deviceList.getDeviceList()) {
                    ndevices.add(new WifiDirectDevice(device));
                }
                setDevices(ndevices.toArray(new IDevice[ndevices.size()]));

            } else if (WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION.equals(action)) {
                // Respond to new connection or disconnections
                Log.d(TAG, "WIFI_P2P_CONNECTION_CHANGED_ACTION triggered");
                startCommuincation();

            } else if (WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION.equals(action)) {
                //On devices running Android 10 and higher, the following broadcast intent is non-sticky (it can be missed)
                Log.d(TAG, "WIFI_P2P_THIS_DEVICE_CHANGED_ACTION triggerd");
                WifiP2pDevice device = intent.getParcelableExtra(WifiP2pManager.EXTRA_WIFI_P2P_DEVICE);
                deviceSelf = new WifiDirectDevice(device);
            }
        }
    }


    private void startCommuincation ()
    {
        WifiDirectControllerAndroid instance = this;
        manager.requestConnectionInfo(channel, new WifiP2pManager.ConnectionInfoListener() {
            @Override
            public void onConnectionInfoAvailable(WifiP2pInfo info) {
                if(!info.groupFormed) {
                    formGroup();
                    return;
                }
                Intent startCommuicationService = new Intent(context, CommuicationService.class);

                if(info.isGroupOwner) {
                    startCommuicationService.putExtra(CommuicationService.COMMUICATION_SERVICE_IS_SERVER_EXTRA, true);
                    isOwner = true;
                } else {
                    startCommuicationService.putExtra(CommuicationService.COMMUICATION_SERVICE_IS_SERVER_EXTRA, false);
                    isOwner = false;
                }
                startCommuicationService.putExtra(CommuicationService.TARGET_INET_ADDR_EXTRA, info.groupOwnerAddress.getHostAddress());
                context.startService(startCommuicationService);
            }
        });
    }

    @SuppressLint("MissingPermission")
    private void formGroup() {
        manager.createGroup(channel, new WifiP2pManager.ActionListener() {
            @Override
            public void onSuccess() {
                Log.i(TAG, "temporary group formed");
            }

            @Override
            public void onFailure(int i) {
                Log.e(TAG, "Failure while forming random group " + i);
            }
        });
    }

    @SuppressLint("MissingPermission")
    @Override
    public void discoverDevices() {

        if(discovery_state.equals(E_DISCOVERY_STATES.RUNNING)) {
            Log.i(TAG, "skip Wifi Device discovery, already running");
            return;
        }
        manager.discoverPeers(channel, new WifiP2pManager.ActionListener() {
            @Override
            public void onSuccess() {
                setDiscovery_state(E_DISCOVERY_STATES.RUNNING);
            }

            @Override
            public void onFailure(int i) {
                //ignore for now
                String reason = "unsupported";
                if(i == WifiP2pManager.ERROR)
                    reason = "error";
                else if(i == WifiP2pManager.BUSY)
                    reason = "busy";
                Log.d(TAG, "discovery devices failed, error code: " + i);
                setDiscovery_state(E_DISCOVERY_STATES.STOPPED);
            }
        });
    }


    @Override
    public void sendMessage(byte[] bytes, IClient iClient) {

    }

    @Override
    public void disconnect() {
        manager.cancelConnect(channel, new WifiP2pManager.ActionListener() {
            @Override
            public void onSuccess() {

            }

            @Override
            public void onFailure(int i) {

            }
        });
    }

    @SuppressLint("MissingPermission")
    @Override
    public void connectDevice(IDevice iDevice) {

        WifiP2pConfig config = new WifiP2pConfig();
        config.deviceAddress = iDevice.getID();

        if(iDevice.getStatus() == WifiP2pDevice.CONNECTED) {
            return;
        }

        manager.connect(channel, config, new WifiP2pManager.ActionListener() {
            @Override
            public void onSuccess() {
                //ignore it
            }

            @Override
            public void onFailure(int i) {
                Log.d(this.getClass().toString(), "onFailure when connecting...");

            }
        });

    }


    public void addPropertyChangeListener(PropertyChangeListener listener) {
        pcs.addPropertyChangeListener(listener);
    }

    public void removePropertyChangeListener(PropertyChangeListener listener) {
        pcs.removePropertyChangeListener(listener);
    }

    public E_DISCOVERY_STATES getDiscovery_state() {
        return discovery_state;
    }

    private void setDiscovery_state(E_DISCOVERY_STATES discovery_state) {
        pcs.firePropertyChange(DISCOVERY_STATE_CHANGED_EVENT, this.discovery_state, discovery_state);
        this.discovery_state = discovery_state;
    }

    private void setWifi_state(E_WIFI_STATES wifi_state) {
        pcs.firePropertyChange(WIFI_STATE_CHANGED_EVENT, this.wifi_state, wifi_state);
        this.wifi_state = wifi_state;
    }
    public E_WIFI_STATES getWifi_state() {
        return wifi_state;
    }

    private void setDevices (IDevice[] devices) {
        pcs.firePropertyChange(DEVICES_CHANGED_EVENT, this.devices, devices);
        this.devices = devices;
    }
    @Override
    public IDevice[] getDevices() {
        return devices;
    }

    @Override
    public boolean isOwner() {
        return isOwner;
    }

    public IDevice getDeviceSelf() {
        return deviceSelf;
    }
}
