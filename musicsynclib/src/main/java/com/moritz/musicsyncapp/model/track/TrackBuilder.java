package com.moritz.musicsyncapp.model.track;

public class TrackBuilder {

    private String name;
    private String artist;
    private String uri;
    private int duration;

    private TrackBuilder()
    {

    }

    public static TrackBuilder create ()
    {
        return new TrackBuilder();
    }

    public ITrack build () {
        return new LocalTrack(name, artist, uri, duration);
    }

    public TrackBuilder setName(String name) {
        this.name = name;
        return this;
    }

    public TrackBuilder setArtist(String artist) {
        this.artist = artist;
        return this;
    }

    public TrackBuilder setUri(String uri) {
        this.uri = uri;
        return this;
    }

    public TrackBuilder setDuration(int duration) {
        this.duration = duration;
        return this;
    }


}
