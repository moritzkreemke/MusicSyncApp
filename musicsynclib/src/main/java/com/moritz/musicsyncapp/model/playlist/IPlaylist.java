package com.moritz.musicsyncapp.model.playlist;

import com.moritz.musicsyncapp.model.track.ITrack;

public interface IPlaylist {

    String getProvider();
    String getName ();
    ITrack[] getTracks();

    void addTrack(ITrack track);

}
