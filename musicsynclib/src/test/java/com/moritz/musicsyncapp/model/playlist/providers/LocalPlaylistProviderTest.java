package com.moritz.musicsyncapp.model.playlist.providers;

import com.moritz.musicsyncapp.model.playlist.IPlaylist;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class LocalPlaylistProviderTest {

    @Test
    void getAllPlaylists() {

        LocalPlaylistProvider provider = new LocalPlaylistProvider("./src/test/resources/local_music/");
        IPlaylist[] playlists = provider.getAllPlaylists();

        assertEquals(1, playlists.length);
        assertEquals("local", playlists[0].getName());
        assertEquals(1, playlists[0].getTracks().length);
        assertEquals("track1.wav", playlists[0].getTracks()[0].getName());


    }
}