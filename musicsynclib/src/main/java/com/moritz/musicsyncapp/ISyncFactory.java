package com.moritz.musicsyncapp;

import com.moritz.musicsyncapp.controller.playlist.IPlaylistController;

public interface ISyncFactory {

    IPlaylistController getPlaylistController();

}
