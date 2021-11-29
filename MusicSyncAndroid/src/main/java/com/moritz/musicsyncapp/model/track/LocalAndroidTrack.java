package com.moritz.musicsyncapp.model.track;

import android.content.Context;
import android.net.Uri;

import androidx.annotation.NonNull;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

public class LocalAndroidTrack extends LocalTrack{


    private Context context;

    public LocalAndroidTrack(@NonNull LocalTrack track, Context context)
    {
        super(track.getName(), track.getArtist(), track.getUri(), track.getDuration());
        this.context = context;
    }

    @Override
    public int getDuration() {
        int duration = super.getDuration();
        if(duration == -1) {
            //TODO something more meaningful xD
            return 5;
        } else {
            return duration;
        }
    }

    @Override
    public InputStream getStream() {

        try {
            return context.getContentResolver().openInputStream(Uri.parse(getUri()));
        } catch (FileNotFoundException e) {
            return null;
        }
    }
}
