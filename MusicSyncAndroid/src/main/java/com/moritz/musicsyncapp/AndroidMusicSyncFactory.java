package com.moritz.musicsyncapp;

import android.content.Context;

import androidx.annotation.Nullable;

import com.moritz.musicsyncapp.controller.playlist.IPlaylistController;
import com.moritz.musicsyncapp.controller.playlist.PlaylistControllerAndroidImpl;
import com.moritz.musicsyncapp.controller.sound.ISoundController;

public class AndroidMusicSyncFactory implements IAndroidSyncFactory{

    private static AndroidMusicSyncFactory _instance;



    public static AndroidMusicSyncFactory get ()
    {
        if(_instance == null)
            throw new RuntimeException("not initalized");
        return _instance;
    }

    public static void init (Context context)
    {
        if(_instance != null)
            throw new RuntimeException("Factory already initzilized");
        _instance = new AndroidMusicSyncFactory(context);
    }

    private Context context;
    private ISoundController localSoundController;

    private AndroidMusicSyncFactory (Context context)
    {
        this.context = context;
    }

    @Override
    public IPlaylistController getPlaylistController() {
        return new PlaylistControllerAndroidImpl(context);
    }

    @Override
    public void setLocalSoundController(ISoundController localSoundController) {
        this.localSoundController = localSoundController;
    }

    @Nullable
    @Override
    public ISoundController getLocalSoundController() {
        return localSoundController;
    }
}
