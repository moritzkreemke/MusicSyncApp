package com.moritz.musicsyncapp.ui.pairdevice;

import android.net.wifi.p2p.WifiP2pDevice;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.moritz.musicsyncapp.AndroidMusicSyncFactory;
import com.moritz.musicsyncapp.R;
import com.moritz.musicsyncapp.controller.p2pnetwork.WifiDirectControllerAndroid;
import com.moritz.musicsyncapp.controller.p2pnetwork.events.P2PNetworkControllerConnectingEvent;
import com.moritz.musicsyncapp.controller.p2pnetwork.events.P2PNetworkControllerDevicesFoundEvent;
import com.moritz.musicsyncapp.model.device.EWifiDirectStatusCodes;
import com.moritz.musicsyncapp.model.device.IDevice;

public class AvailableDevicesAdapter extends RecyclerView.Adapter<AvailableDevicesAdapter.AvailableDevicesHolder> {


    private IDevice[] devices = new IDevice[0];

    public AvailableDevicesAdapter() {

    }

    @NonNull
    @Override
    public AvailableDevicesHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View trackView = LayoutInflater.from(parent.getContext()).inflate(R.layout.fragment_available_device, parent, false);

        return new AvailableDevicesAdapter.AvailableDevicesHolder(trackView);
    }

    @Override
    public void onBindViewHolder(@NonNull AvailableDevicesHolder availableDevicesHolder, int i) {
        availableDevicesHolder.deviceName.setText(devices[i].getDisplayName());
        availableDevicesHolder.deviceStatus.setText(EWifiDirectStatusCodes.valueOf(devices[i].getStatus()).toString());
        availableDevicesHolder.progressBar.setVisibility(View.INVISIBLE);
        availableDevicesHolder.cancelBtn.setVisibility(View.INVISIBLE);

        availableDevicesHolder.connectBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                availableDevicesHolder.progressBar.setVisibility(View.VISIBLE);
                availableDevicesHolder.progressBar.setIndeterminate(true);
                availableDevicesHolder.connectBtn.setEnabled(false);

                availableDevicesHolder.connectBtn.setVisibility(View.INVISIBLE);
                availableDevicesHolder.cancelBtn.setVisibility(View.VISIBLE);

                AndroidMusicSyncFactory.get().getNetworkController(null).connectDevice(devices[availableDevicesHolder.getAdapterPosition()]);
            }
        });
    }

    public void setDevices(IDevice[] devices) {
        this.devices = devices;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return devices.length;
    }

    static class AvailableDevicesHolder extends RecyclerView.ViewHolder {

        TextView deviceName;
        TextView deviceStatus;
        ProgressBar progressBar;
        Button connectBtn;
        Button cancelBtn;

        public AvailableDevicesHolder(@NonNull View itemView) {
            super(itemView);
            deviceName = itemView.findViewById(R.id.text_view_device_name_value_available_devices);
            deviceStatus = itemView.findViewById(R.id.text_view_device_status_value_available_devices);
            progressBar = itemView.findViewById(R.id.progress_bar_connecting_available_devices);
            connectBtn = itemView.findViewById(R.id.btn_connect_to_device_available_devices);
            cancelBtn = itemView.findViewById(R.id.btn_cancel_connect_to_device_available_devices);
        }
    }

}
