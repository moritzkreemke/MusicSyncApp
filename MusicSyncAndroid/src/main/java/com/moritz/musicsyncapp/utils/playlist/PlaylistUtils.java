package com.moritz.musicsyncapp.utils.playlist;

import com.moritz.musicsyncapp.model.playlist.IPlaylist;
import com.moritz.musicsyncapp.utils.track.TrackUtils;

public class PlaylistUtils {

    public static IPlaylist[] convertToAndroidPlaylist (IPlaylist[] playlists)
    {
        IPlaylist[] result = new IPlaylist[playlists.length];
        for (int i = 0; i < playlists.length; i++) {
            result[i].setTracks(TrackUtils.convertToAndroidTrack(playlists[i].getTracks()), false);
        }
        return result;
    }

}
