package com.moritz.musicsyncapp.controller.playlist;

import com.moritz.musicsyncapp.model.playlist.IPlaylist;

public interface IPlaylistController {

    IPlaylist[] getAllPlaylists();
    IPlaylist[] getPlaylistByProvider(String provider);

}
