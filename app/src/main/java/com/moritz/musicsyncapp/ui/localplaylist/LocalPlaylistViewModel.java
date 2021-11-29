package com.moritz.musicsyncapp.ui.localplaylist;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.moritz.musicsyncapp.AndroidMusicSyncFactory;
import com.moritz.musicsyncapp.model.playlist.IPlaylist;

import java.util.List;

public class LocalPlaylistViewModel extends AndroidViewModel {


    public LocalPlaylistViewModel(@NonNull Application application) {
        super(application);
    }
}