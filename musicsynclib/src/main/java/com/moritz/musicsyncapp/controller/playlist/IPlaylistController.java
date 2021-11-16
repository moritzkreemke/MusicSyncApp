package com.moritz.musicsyncapp.controller.playlist;

import com.moritz.musicsyncapp.model.playlist.IPlaylist;
import com.moritz.musicsyncapp.model.playlist.providers.EProviderTypes;

public interface IPlaylistController {

    IPlaylist[] getPlaylist(CharSequence path, EProviderTypes type);

}
