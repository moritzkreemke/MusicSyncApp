package com.moritz.musicsyncapp.controller.playlist;

import com.moritz.musicsyncapp.model.playlist.IPlaylist;
import com.moritz.musicsyncapp.model.playlist.providers.EProviderTypes;
import com.moritz.musicsyncapp.model.playlist.providers.IPlaylistProvider;
import com.moritz.musicsyncapp.model.playlist.providers.LocalPlaylistProvider;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PlaylistControllerImpl implements IPlaylistController {


    @Override
    public IPlaylist[] getPlaylist(CharSequence path, EProviderTypes type) {
        if(type.equals(EProviderTypes.LOCAL)) {
            IPlaylistProvider provider = new LocalPlaylistProvider(path);
            return provider.getAllPlaylists();
        } else {
            return new IPlaylist[0];
        }
    }
}
