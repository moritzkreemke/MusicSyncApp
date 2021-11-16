package com.moritz.musicsyncapp.model.playlist;

import com.moritz.musicsyncapp.model.track.ITrack;

import java.util.ArrayList;
import java.util.List;

class LocalPlaylistImpl implements IPlaylist {

    private String provider;
    private String name;
    private List<ITrack> tracks;

    public LocalPlaylistImpl(String provider, String name) {
        this.provider = provider;
        this.name = name;
        this.tracks = new ArrayList<>();
    }

    @Override
    public String getProvider() {
        return provider;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public ITrack[] getTracks() {
        return tracks.toArray(new ITrack[tracks.size()]);
    }

    @Override
    public void addTrack(ITrack track) {

    }

    void setTracks(List<ITrack> tracks) {
        this.tracks = tracks;
    }
}
