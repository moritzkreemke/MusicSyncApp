package com.moritz.musicsyncapp;

import com.moritz.musicsyncapp.controller.playlist.IPlaylistController;
import com.moritz.musicsyncapp.controller.playlist.PlaylistControllerImpl;


public class MusicSyncFactory implements ISyncFactory {

    public IPlaylistController getPlaylistController ()
    {
        return new PlaylistControllerImpl();
    }

}