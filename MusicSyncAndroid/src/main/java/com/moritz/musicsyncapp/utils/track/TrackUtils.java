package com.moritz.musicsyncapp.utils.track;

import com.moritz.musicsyncapp.model.track.ITrack;
import com.moritz.musicsyncapp.model.track.LocalAndroidTrack;
import com.moritz.musicsyncapp.model.track.LocalTrack;

public class TrackUtils {

    public static ITrack[] convertToAndroidTrack (ITrack[] tracks) {
        ITrack[] result = new ITrack[tracks.length];
        for (int i = 0; i < tracks.length; i++) {
            if(tracks[i] instanceof LocalTrack) {
                result[i] = new LocalAndroidTrack((LocalTrack) tracks[i]);
            } else {
                tracks[i] = tracks[i];
            }
        }
        return result;
    }

}
