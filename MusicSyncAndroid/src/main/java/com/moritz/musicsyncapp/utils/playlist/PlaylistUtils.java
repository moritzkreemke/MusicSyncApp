package com.moritz.musicsyncapp.utils.playlist;

import com.moritz.musicsyncapp.model.playlist.IPlaylist;
import com.moritz.musicsyncapp.utils.track.TrackUtils;

public class PlaylistUtils {

    public static IPlaylist[] convertToAndroidPlaylist (IPlaylist[] playlists)
    {
        for (IPlaylist playlist : playlists) {
            playlist.setTracks(TrackUtils.convertToAndroidTrack(playlist.getTracks()), false);
        }
        return playlists;
    }

}
