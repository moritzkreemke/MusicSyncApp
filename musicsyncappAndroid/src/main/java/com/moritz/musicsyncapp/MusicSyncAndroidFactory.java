package com.moritz.musicsyncapp;

import com.moritz.musicsyncapp.controller.playlist.IPlaylistController;

public class MusicSyncAndroidFactory implements ISyncFactory {


    private static MusicSyncAndroidFactory _instance;

    public static ISyncFactory getInstance ()
    {
        return new MusicSyncAndroidFactory();
    }

    private MusicSyncAndroidFactory() {
    }

    @Override
    public IPlaylistController getPlaylistController() {
        return null;
    }
}
