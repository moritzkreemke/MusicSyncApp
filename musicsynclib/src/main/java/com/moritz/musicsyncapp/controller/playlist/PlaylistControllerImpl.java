package com.moritz.musicsyncapp.controller.playlist;

import com.moritz.musicsyncapp.model.playlist.IPlaylist;
import com.moritz.musicsyncapp.model.playlist.IPlaylistProvider;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PlaylistControllerImpl implements IPlaylistController {

    private Map<String, IPlaylistProvider> providers;

    public PlaylistControllerImpl ()
    {
        providers = new HashMap<>();
    }

    public void addProvider (String name, IPlaylistProvider provider)
    {
        providers.put(name, provider);
    }

    @Override
    public IPlaylist[] getAllPlaylists() {
        List<IPlaylist> result = new ArrayList<>();
        for (String provider : providers.keySet()) {
            result.addAll(Arrays.asList(providers.get(provider).getAllPlaylists()));
        }
        return result.toArray(new IPlaylist[result.size()]);
    }

    @Override
    public IPlaylist[] getPlaylistByProvider(String provider) {
        return providers.get(provider).getAllPlaylists();
    }
}
