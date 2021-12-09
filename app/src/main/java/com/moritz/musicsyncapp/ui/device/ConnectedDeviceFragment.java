package com.moritz.musicsyncapp.ui.device;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.moritz.musicsyncapp.R;

/**
 * A simple {@link Fragment} subclass.
 * create an instance of this fragment.
 */
public class ConnectedDeviceFragment extends Fragment {



    public ConnectedDeviceFragment() {
        // Required empty public constructor
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_connected_device, container, false);
    }
}