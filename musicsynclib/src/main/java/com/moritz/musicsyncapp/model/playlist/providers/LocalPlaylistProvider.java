package com.moritz.musicsyncapp.model.playlist.providers;

import com.moritz.musicsyncapp.Constants;
import com.moritz.musicsyncapp.model.playlist.IPlaylist;
import com.moritz.musicsyncapp.model.playlist.PlaylistBuilder;
import com.moritz.musicsyncapp.model.track.ITrack;
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
        PlaylistBuilder playlistBuilder = PlaylistBuilder.create(getName().toString().toLowerCase(), "local");
        for (int i = 0; i < files.length; i++) {
            try {
                var audio =  AudioSystem.getAudioFileFormat(files[i]);
                ITrack track =  TrackBuilder.create()
                        .setName(files[i].getName())
                        .setDuration(audio.getFrameLength())
                        .setArtist("moritz")
                        .build();
                playlistBuilder.addTrack(track);
            } catch (UnsupportedAudioFileException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        IPlaylist result = playlistBuilder.build();
        if(result.getTracks().length == 0)
            return new IPlaylist[0];
        else
            return new IPlaylist[] {result};
    }

    @Override
    public EProviderTypes getName() {
        return EProviderTypes.LOCAL;
    }
}
