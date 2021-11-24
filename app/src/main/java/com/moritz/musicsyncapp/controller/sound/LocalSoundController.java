package com.moritz.musicsyncapp.controller.sound;

import android.app.Service;

import com.moritz.musicsyncapp.model.track.IPlayableTrack;
import com.moritz.musicsyncapp.services.MusicPlaybackService;

public class LocalSoundController implements ISoundController{


   private MusicPlaybackService.LocalBinder localBinder;

    public LocalSoundController(MusicPlaybackService.LocalBinder localBinder) {
        this.localBinder = localBinder;
    }

    @Override
    public void play(IPlayableTrack iTrack) {
        localBinder.play(iTrack);
    }

    @Override
    public void stop() {

    }
}
