package com.moritz.musicsyncapp.model.playlist;

import com.moritz.musicsyncapp.model.track.ITrack;

import java.security.cert.Certificate;
import java.util.ArrayList;
import java.util.List;

public class PlaylistBuilder {


    private String provider;
    private String name;
    private List<ITrack> tracks;

    private PlaylistBuilder(String provider, String name) {
        this.provider = provider;
        this.name = name;
        tracks = new ArrayList<>();
    }

    public static PlaylistBuilder create (String provider, String name) {
        return new PlaylistBuilder(provider, name);
    }

    public IPlaylist build () {
        LocalPlaylistImpl result = new LocalPlaylistImpl(provider, name);
        result.setTracks(tracks);
        return result;
    }


    public PlaylistBuilder addTrack(ITrack track) {
        tracks.add(track);
        return this;
    }

    public PlaylistBuilder setTracks(List<ITrack> tracks) {
        this.tracks = tracks;
        return this;
    }
}
