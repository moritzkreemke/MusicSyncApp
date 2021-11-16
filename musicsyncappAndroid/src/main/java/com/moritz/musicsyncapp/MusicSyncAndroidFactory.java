package com.moritz.musicsyncapp;

import com.moritz.musicsyncapp.controller.playlist.IPlaylistController;

public class MusicSyncAndroidFactory implements ISyncFactory {

    @Override
    public IPlaylistController getPlaylistController() {
        return null;
    }
}
