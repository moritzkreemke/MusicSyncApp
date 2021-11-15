package com.moritz.musicsyncapp.model.playlist;

public class LocalPlaylistProvider implements IPlaylistProvider{

    @Override
    public IPlaylist[] getAllPlaylists() {
        return new IPlaylist[0];
    }
}
