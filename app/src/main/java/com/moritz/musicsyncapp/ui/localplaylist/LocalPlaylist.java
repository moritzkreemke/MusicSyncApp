package com.moritz.musicsyncapp.ui.localplaylist;

import androidx.annotation.RequiresApi;
import androidx.lifecycle.ViewModelProvider;

import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import com.moritz.musicsyncapp.AndroidMusicSyncFactory;
import com.moritz.musicsyncapp.MusicSyncFactory;
import com.moritz.musicsyncapp.R;

public class LocalPlaylist extends Fragment {

    private LocalPlaylistViewModel mViewModel;


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        super.onCreateView(inflater, container, savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(LocalPlaylistViewModel.class);

        View localPlaylistView = inflater.inflate(R.layout.local_playlist_fragment, container, false);

        RecyclerView recyclerView = localPlaylistView.findViewById(R.id.recyclerView);
        PlaylistAdapter adapter = new PlaylistAdapter(getContext());
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        //recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(adapter);


        adapter.test();
        return localPlaylistView;
    }



}