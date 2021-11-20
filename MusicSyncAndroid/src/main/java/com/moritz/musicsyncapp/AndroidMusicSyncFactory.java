package com.moritz.musicsyncapp;

import com.moritz.musicsyncapp.controller.playlist.IPlaylistController;
import com.moritz.musicsyncapp.controller.playlist.PlaylistControllerAndroidImpl;

public class AndroidMusicSyncFactory implements ISyncFactory{
    @Override
    public IPlaylistController getPlaylistController() {
        return new PlaylistControllerAndroidImpl();
    }
}
