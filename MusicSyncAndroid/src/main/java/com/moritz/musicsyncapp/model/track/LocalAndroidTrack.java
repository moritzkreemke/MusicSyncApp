package com.moritz.musicsyncapp.model.track;

import androidx.annotation.NonNull;

public class LocalAndroidTrack extends LocalTrack{


    public LocalAndroidTrack (@NonNull LocalTrack track)
    {
        super(track.getName(), track.getArtist(), track.getUri());
    }

    @Override
    public int getDuration() {
        int duration = super.getDuration();
        if(duration == -1) {
            return 5;
        } else {
            return duration;
        }
    }
}
