package com.moritz.musicsyncapp.controller.sound;

import com.moritz.musicsyncapp.model.track.ITrack;

public interface ISoundController {

    void play(ITrack track);
    void stop();

}
