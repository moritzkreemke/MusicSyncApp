package com.moritz.musicsyncapp.controller.playlist;

import com.moritz.musicsyncapp.model.playlist.IPlaylist;
import com.moritz.musicsyncapp.utils.playlist.PlaylistUtils;

public class PlaylistControllerAndroidImpl extends PlaylistControllerImpl{


    @Override
    public IPlaylist[] getPlaylistFromFilePath(CharSequence path) {
        return PlaylistUtils.convertToAndroidPlaylist(super.getPlaylistFromFilePath(path));
    }
}
