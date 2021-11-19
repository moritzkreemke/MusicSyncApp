package com.moritz.musicsyncapp.ui.localplaylist;

import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.moritz.musicsyncapp.MusicSyncFactory;
import com.moritz.musicsyncapp.R;
import com.moritz.musicsyncapp.model.playlist.providers.EProviderTypes;

public class LocalPlaylist extends Fragment {

    private LocalPlaylistViewModel mViewModel;

    public static LocalPlaylist newInstance() {
        return new LocalPlaylist();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        mViewModel = new ViewModelProvider(this).get(LocalPlaylistViewModel.class);

        //Log.println(getContext().getApplicationContext().getCacheDir().getAbsolutePath())
        new MusicSyncFactory().getPlaylistController().getPlaylist((CharSequence) getContext().getApplicationContext().getCacheDir().getAbsolutePath(), EProviderTypes.LOCAL);
        return inflater.inflate(R.layout.local_playlist_fragment, container, false);
    }

}