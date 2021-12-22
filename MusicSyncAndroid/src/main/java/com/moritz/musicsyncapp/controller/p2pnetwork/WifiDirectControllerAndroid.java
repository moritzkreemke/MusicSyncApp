package com.moritz.musicsyncapp.controller.p2pnetwork;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.net.wifi.p2p.WifiP2pConfig;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pDeviceList;
import android.net.wifi.p2p.WifiP2pInfo;
import android.net.wifi.p2p.WifiP2pManager;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;

import com.moritz.musicsyncapp.controller.commuication.CommuicationFactory;
import com.moritz.musicsyncapp.controller.commuication.events.OnReciveMessageEvent;
import com.moritz.musicsyncapp.controller.p2pnetwork.events.P2PNetworkControllerConnectingEvent;
import com.moritz.musicsyncapp.controller.p2pnetwork.events.P2PNetworkControllerDevicesFoundEvent;
import com.moritz.musicsyncapp.controller.p2pnetwork.events.P2PNetworkControllerDiscoverDevicesEvent;
import com.moritz.musicsyncapp.controller.p2pnetwork.services.CommuicationService;
import com.moritz.musicsyncapp.model.client.IClient;
import com.moritz.musicsyncapp.model.commuication.ISendableMessage;
import com.moritz.musicsyncapp.model.commuication.events.EventMessage;
import com.moritz.musicsyncapp.model.commuication.messages.AvailableClientsChanged;
import com.moritz.musicsyncapp.model.device.IDevice;
import com.moritz.musicsyncapp.model.device.WifiDirectDevice;
import com.moritz.musicsyncapp.model.session.ISession;
import com.moritz.musicsyncapp.model.session.SessionBuilder;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.ArrayList;
import java.util.List;
@SuppressLint("MissingPermission")
public class WifiDirectControllerAndroid implements IP2PNetworkController {

    public static final String TAG = WifiDirectControllerAndroid.class.toString();

    private PropertyChangeSupport pcs = new PropertyChangeSupport(this);
    private Context context;
    private WifiP2pManager manager;
    private WifiP2pManager.Channel channel;
    private WifiDirectBroadcastReciver receiver;

    private P2PNetworkControllerDiscoverDevicesEvent p2PNetworkControllerDiscoverDevicesEvent;
    private ISession session;

    private IDevice deviceSelf;

    public enum E_WIFI_STATES {
        UNDEFINED,
        ENABLED,
        DISABLED
    }

    private enum E_DISCOVERY_STATES {
        RUNNING,
        STOPPED
    }

    private E_WIFI_STATES wifi_state;
    private E_DISCOVERY_STATES discovery_state;

    private CommuicationService.LocalBinder commuicationServivceBinder;


    public WifiDirectControllerAndroid(Context context) {
        this.context = context;
        this.wifi_state = E_WIFI_STATES.UNDEFINED;
        this.discovery_state = E_DISCOVERY_STATES.STOPPED;
        setSession(SessionBuilder.get().build());

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

        CommuicationFactory.get().getClient().addOnReviveMessageListener(new OnReciveMessageEvent() {
            @Override
            public void onReceiveMessage(ISendableMessage iSendableMessage) {
                if (iSendableMessage.getMessage() instanceof AvailableClientsChanged) {
                    AvailableClientsChanged availableClientsChanged = (AvailableClientsChanged) iSendableMessage.getMessage();
                    setSession(SessionBuilder.get(session).setClients(availableClientsChanged.getClientList()).build());
                    System.out.println("Session Count is: " + getSession().getClients().length);
                }
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
                    wifi_state = E_WIFI_STATES.ENABLED;
                } else {
                    wifi_state = E_WIFI_STATES.DISABLED;
                }

            } else if (WifiP2pManager.WIFI_P2P_DISCOVERY_CHANGED_ACTION.equals(action)) {
                Log.d(TAG, "WIFI_P2P_DISCOVERY_CHANGED_ACTION trigged");
                int extra = intent.getIntExtra(WifiP2pManager.EXTRA_DISCOVERY_STATE, -1);
                if (extra == WifiP2pManager.WIFI_P2P_DISCOVERY_STARTED) {
                    discovery_state = E_DISCOVERY_STATES.RUNNING;
                } else if (extra == WifiP2pManager.WIFI_P2P_DISCOVERY_STOPPED) {
                    discovery_state = E_DISCOVERY_STATES.STOPPED;
                    if (p2PNetworkControllerDiscoverDevicesEvent != null) {
                        p2PNetworkControllerDiscoverDevicesEvent.onFinished();
                        requestDevices(new P2PNetworkControllerDevicesFoundEvent() {
                            @Override
                            public void onDevicesFound(IDevice[] iDevices) {
                                p2PNetworkControllerDiscoverDevicesEvent.onDevicesFound(iDevices);
                            }
                        });
                    }
                }
            } else if (WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION.equals(action)) {
                // Call WifiP2pManager.requestPeers() to get a list of current peers
                Log.d(TAG, "WIFI_P2P_PEERS_CHANGED_ACTION triggered");
                WifiP2pDeviceList deviceList = intent.getParcelableExtra(WifiP2pManager.EXTRA_P2P_DEVICE_LIST);

                if(p2PNetworkControllerDiscoverDevicesEvent != null) {
                    List<IDevice> devices = new ArrayList<>();
                    for (WifiP2pDevice device : deviceList.getDeviceList()) {
                        devices.add(new WifiDirectDevice(device));
                    }
                    p2PNetworkControllerDiscoverDevicesEvent.onDevicesFound(devices.toArray(new IDevice[devices.size()]));
                }

                startCommuincation();

            } else if (WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION.equals(action)) {
                // Respond to new connection or disconnections
                Log.d(TAG, "WIFI_P2P_CONNECTION_CHANGED_ACTION triggered");
                if(p2PNetworkControllerDiscoverDevicesEvent != null) {
                    requestDevices(new P2PNetworkControllerDevicesFoundEvent() {
                        @Override
                        public void onDevicesFound(IDevice[] iDevices) {
                            p2PNetworkControllerDiscoverDevicesEvent.onDevicesFound(iDevices);
                        }
                    });
                }

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
                    setSession(SessionBuilder.get(session).setOwner(true).build());
                } else {
                    startCommuicationService.putExtra(CommuicationService.COMMUICATION_SERVICE_IS_SERVER_EXTRA, false);
                    setSession(SessionBuilder.get(session).setOwner(false).build());
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
    public void discoverDevices(P2PNetworkControllerDiscoverDevicesEvent p2PNetworkControllerDiscoverDevicesEvent
            , boolean trackChanges) {

        if(trackChanges) {
            this.p2PNetworkControllerDiscoverDevicesEvent = p2PNetworkControllerDiscoverDevicesEvent;
        }

        if(discovery_state.equals(E_DISCOVERY_STATES.RUNNING)) {
            Log.i(TAG, "skip Wifi Device discovery, already running");
            return;
        }

        manager.discoverPeers(channel, new WifiP2pManager.ActionListener() {
            @Override
            public void onSuccess() {
                p2PNetworkControllerDiscoverDevicesEvent.onFinished();
                discovery_state = E_DISCOVERY_STATES.STOPPED;
                if(!trackChanges) {
                    requestDevices(new P2PNetworkControllerDevicesFoundEvent() {
                        @Override
                        public void onDevicesFound(IDevice[] iDevices) {
                            p2PNetworkControllerDiscoverDevicesEvent.onDevicesFound(iDevices);
                        }
                    });
                }
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
                p2PNetworkControllerDiscoverDevicesEvent.onFailure(i);
                discovery_state = E_DISCOVERY_STATES.STOPPED;
            }
        });
    }

    @SuppressLint("MissingPermission")
    @Override
    public void requestDevices(P2PNetworkControllerDevicesFoundEvent devicesFoundEvent) {

        manager.requestPeers(channel, new WifiP2pManager.PeerListListener() {
            @Override
            public void onPeersAvailable(WifiP2pDeviceList wifiP2pDeviceList) {
                List<IDevice> devices = new ArrayList<>();
                for (WifiP2pDevice device : wifiP2pDeviceList.getDeviceList()) {
                    devices.add(new WifiDirectDevice(device));
                }
                devicesFoundEvent.onDevicesFound(devices.toArray(new IDevice[devices.size()]));
            }
        });
    }

    @Override
    public void sendMessage(byte[] bytes, IClient iClient) {

    }

    @SuppressLint("MissingPermission")
    @Override
    public void connectDevice(IDevice iDevice, P2PNetworkControllerConnectingEvent connectingEvent) {

        WifiP2pConfig config = new WifiP2pConfig();
        config.deviceAddress = iDevice.getID();

        if(iDevice.getStatus() == WifiP2pDevice.CONNECTED) {
            return;
        }

        manager.connect(channel, config, new WifiP2pManager.ActionListener() {
            @Override
            public void onSuccess() {
                connectingEvent.onSuccessfulConnected();
            }

            @Override
            public void onFailure(int i) {
                Log.d(this.getClass().toString(), "onFailure when connecting...");
                connectingEvent.onFailure(i);
            }
        });

    }

    public void addPropertyChangeListener(PropertyChangeListener listener) {
        pcs.addPropertyChangeListener(listener);
    }

    public void removePropertyChangeListener(PropertyChangeListener listener) {
        pcs.removePropertyChangeListener(listener);
    }

    private void setSession (ISession session)
    {
        pcs.firePropertyChange("session", this.session, session);
        this.session = session;
    }
    public ISession getSession ()
    {
        return null;
    }

    public E_WIFI_STATES getWifi_state() {
        return wifi_state;
    }

    public IDevice getDeviceSelf() {
        return deviceSelf;
    }
}
