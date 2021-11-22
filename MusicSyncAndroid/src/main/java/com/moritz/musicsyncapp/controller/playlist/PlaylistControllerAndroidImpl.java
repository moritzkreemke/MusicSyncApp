package com.moritz.musicsyncapp.controller.playlist;

import android.content.Context;

import com.moritz.musicsyncapp.model.playlist.IPlaylist;
import com.moritz.musicsyncapp.model.playlist.providers.IPlaylistProvider;
import com.moritz.musicsyncapp.model.playlist.providers.MediaStorePlaylistProvider;
import com.moritz.musicsyncapp.utils.playlist.PlaylistUtils;

public class PlaylistControllerAndroidImpl extends PlaylistControllerImpl {

    private Context context;

    public PlaylistControllerAndroidImpl(Context context) {
        this.context = context;
    }

    @Override
    public IPlaylist[] getPlaylistFromFilePath(CharSequence path) {
        return PlaylistUtils.convertToAndroidPlaylist(super.getPlaylistFromFilePath(path));
    }

    @Override
    public IPlaylist[] getSystemPlaylists() {
        IPlaylistProvider provider = new MediaStorePlaylistProvider(context);
        return PlaylistUtils.convertToAndroidPlaylist(provider.getAllPlaylists());
    }
}
