package com.moritz.musicsyncapp.ui.pairdevice;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.moritz.musicsyncapp.R;
import com.moritz.musicsyncapp.model.device.IDevice;

class ConnectedDevicesAdapter  extends RecyclerView.Adapter<ConnectedDevicesAdapter.ConnectedDevicesHolder> {

    private IDevice[] devices = new IDevice[0];

    @NonNull
    @Override
    public ConnectedDevicesHolder onCreateViewHolder(@NonNull ViewGroup parent, int index) {
        View trackView = LayoutInflater.from(parent.getContext()).inflate(R.layout.fragment_connected_device, parent, false);

        return new ConnectedDevicesAdapter.ConnectedDevicesHolder(trackView);
    }

    @Override
    public void onBindViewHolder(@NonNull ConnectedDevicesHolder connectedDevicesHolder, int i) {
        connectedDevicesHolder.deviceName.setText(devices[i].getDisplayName());
        connectedDevicesHolder.connectBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //AndroidMusicSyncFactory.get().getNetworkController(null).connectDevice(devices[availableDevicesHolder.getAdapterPosition()]);
            }
        });
    }

    @Override
    public int getItemCount() {
        return devices.length;
    }

    public void setDevices(IDevice[] devices) {
        this.devices = devices;
        notifyDataSetChanged();
    }

    static class ConnectedDevicesHolder extends RecyclerView.ViewHolder {

        TextView deviceName;
        Button connectBtn;

        public ConnectedDevicesHolder(@NonNull View itemView) {
            super(itemView);
            deviceName = itemView.findViewById(R.id.text_view_device_name_value_connected_devices);
            connectBtn = itemView.findViewById(R.id.btn_device_disconnect_connected_devices);
        }
    }
}
