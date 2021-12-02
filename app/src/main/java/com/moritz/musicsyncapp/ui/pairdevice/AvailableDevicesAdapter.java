package com.moritz.musicsyncapp.ui.pairdevice;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.moritz.musicsyncapp.AndroidMusicSyncFactory;
import com.moritz.musicsyncapp.R;
import com.moritz.musicsyncapp.controller.p2pnetwork.events.IP2PNetworkControllerDevicesFoundChangedEvent;
import com.moritz.musicsyncapp.model.device.IDevice;
import com.moritz.musicsyncapp.ui.localplaylist.PlaylistAdapter;

public class AvailableDevicesAdapter extends RecyclerView.Adapter<AvailableDevicesAdapter.AvailableDevicesHolder> {


    private IDevice[] devices = new IDevice[0];

    public AvailableDevicesAdapter() {
        AndroidMusicSyncFactory.get().getNetworkController(null).addOnDevicesFoundChangeListener(new IP2PNetworkControllerDevicesFoundChangedEvent() {
            @Override
            public void onDevicesFound(IDevice[] iDevices) {
                devices = iDevices;
                notifyDataSetChanged();
            }
        });
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
        availableDevicesHolder.connectBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AndroidMusicSyncFactory.get().getNetworkController(null).connectDevice(devices[availableDevicesHolder.getAdapterPosition()]);
            }
        });
    }

    @Override
    public int getItemCount() {
        return devices.length;
    }

    static class AvailableDevicesHolder extends RecyclerView.ViewHolder {

        TextView deviceName;
        Button connectBtn;

        public AvailableDevicesHolder(@NonNull View itemView) {
            super(itemView);
            deviceName = itemView.findViewById(R.id.textView_devicename);
            connectBtn = itemView.findViewById(R.id.btn_device_connect);
        }
    }

}
