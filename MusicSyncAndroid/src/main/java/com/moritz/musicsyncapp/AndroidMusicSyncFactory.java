package com.moritz.musicsyncapp;

import android.content.Context;

import com.moritz.musicsyncapp.controller.playlist.IPlaylistController;
import com.moritz.musicsyncapp.controller.playlist.PlaylistControllerAndroidImpl;

public class AndroidMusicSyncFactory implements ISyncFactory{

    private static AndroidMusicSyncFactory _instance;


    public static AndroidMusicSyncFactory get(Context context)
    {
        if(_instance == null)
            _instance = new AndroidMusicSyncFactory(context);
        return _instance;
    }

    private Context context;

    private AndroidMusicSyncFactory (Context context)
    {
        this.context = context;
    }

    @Override
    public IPlaylistController getPlaylistController() {
        return new PlaylistControllerAndroidImpl(context);
    }
}
