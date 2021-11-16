package com.moritz.musicsyncapp.model.playlist.providers;

import com.moritz.musicsyncapp.model.playlist.IPlaylist;

public interface IPlaylistProvider {

    IPlaylist[] getAllPlaylists();

}
