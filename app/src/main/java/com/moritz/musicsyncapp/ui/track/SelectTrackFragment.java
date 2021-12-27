package com.moritz.musicsyncapp.ui.track;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.moritz.musicsyncapp.R;


public class SelectTrackFragment extends Fragment {


    public SelectTrackFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View view = inflater.inflate(R.layout.fragment_select_track_item, container, false);

        return view;
    }
}