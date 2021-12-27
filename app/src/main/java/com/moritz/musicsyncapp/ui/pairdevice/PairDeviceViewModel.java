package com.moritz.musicsyncapp.ui.pairdevice;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.moritz.musicsyncapp.AndroidMusicSyncFactory;
import com.moritz.musicsyncapp.controller.p2pnetwork.WifiDirectControllerAndroid;
import com.moritz.musicsyncapp.model.device.EWifiDirectStatusCodes;
import com.moritz.musicsyncapp.model.device.IDevice;
import com.moritz.musicsyncapp.model.session.ISession;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.List;

public class PairDeviceViewModel extends ViewModel {


        private PropertyChangeListener pcs;
        private PropertyChangeListener sessionChangeListener;
        private WifiDirectControllerAndroid wifiDirectControllerAndroid;

        private WifiDirectControllerAndroid.E_DISCOVERY_STATES discovery_state;
        private IDevice[] devices;
        private ISession session;

        public PairDeviceViewModel() {

                this.wifiDirectControllerAndroid = (WifiDirectControllerAndroid) AndroidMusicSyncFactory.get().getNetworkController(null);
                discovery_state = wifiDirectControllerAndroid.getDiscovery_state();
                devices = wifiDirectControllerAndroid.getDevices();
                session = AndroidMusicSyncFactory.get().getSessionController().getSession();

                pcs = evt -> {
                        if(evt.getPropertyName().equals(WifiDirectControllerAndroid.DISCOVERY_STATE_CHANGED_EVENT)) {
                                discovery_state = (WifiDirectControllerAndroid.E_DISCOVERY_STATES) evt.getNewValue();
                        } else if(evt.getPropertyName().equals(WifiDirectControllerAndroid.DEVICES_CHANGED_EVENT)) {
                                devices = (IDevice[]) evt.getNewValue();
                        }
                };
                sessionChangeListener = new PropertyChangeListener() {
                        @Override
                        public void propertyChange(PropertyChangeEvent evt) {
                                session = (ISession) evt.getNewValue();
                        }
                };
                AndroidMusicSyncFactory.get().getSessionController().addSessionChangeListener(sessionChangeListener);
                 wifiDirectControllerAndroid.addPropertyChangeListener(pcs);
        }

        public IDevice[] getConnectedDevices () {
                ArrayList<IDevice> connected = new ArrayList<>();
                for (IDevice device : devices) {
                        if(device.getStatus() == EWifiDirectStatusCodes.CONNECTED.getCode()) {
                                connected.add(device);
                        }
                }
                return connected.toArray(new IDevice[connected.size()]);
        }
        public IDevice[] getAvailableDevices () {
                ArrayList<IDevice> otherDevices = new ArrayList<>();

                for (IDevice device : devices) {
                        if(device.getStatus() == EWifiDirectStatusCodes.CONNECTED.getCode()) {
                             //   connected.add(device);
                        } else {
                                otherDevices.add(device);
                        }
                }
                return otherDevices.toArray(new IDevice[otherDevices.size()]);
        }
        public WifiDirectControllerAndroid.E_DISCOVERY_STATES getDiscoveryState () {
                return discovery_state;
        }

        public ISession getSession() {
                return session;
        }

        @Override
        protected void onCleared() {
                super.onCleared();
                wifiDirectControllerAndroid.removePropertyChangeListener(pcs);
                AndroidMusicSyncFactory.get().getSessionController().removeSessionChangeListener(sessionChangeListener);
        }
}
