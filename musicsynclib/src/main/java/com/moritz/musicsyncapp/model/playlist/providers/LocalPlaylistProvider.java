package com.moritz.musicsyncapp.model.playlist.providers;

import com.moritz.musicsyncapp.Constants;
import com.moritz.musicsyncapp.model.playlist.IPlaylist;
import com.moritz.musicsyncapp.model.track.TrackBuilder;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.UnsupportedAudioFileException;

public class LocalPlaylistProvider implements IPlaylistProvider{


    private CharSequence path;

    public LocalPlaylistProvider(CharSequence path) {
        this.path = path;
    }

    @Override
    public IPlaylist[] getAllPlaylists() {

        File[] files = new File(path.toString()).listFiles(Constants.SUPPORTED_MUSIC_FILES_FILTER());
        for (int i = 0; i < files.length; i++) {
            try {
                //AudioSystem.getAudioFileFormat(files[i]).
            } catch (UnsupportedAudioFileException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return new IPlaylist[0];
    }
}
