package com.moritz.musicsyncapp.model.track;

import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

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

    @Nullable
    public static LocalAndroidTrack getByUri (String uri, Context context)
    {
        Uri collection = Uri.parse(uri);
        String[] projection = new String[]
                {
                        MediaStore.Audio.Media._ID,
                        MediaStore.Audio.Media.DISPLAY_NAME,
                        MediaStore.Audio.Media.ARTIST,
                        MediaStore.Audio.Media.DURATION,
                        MediaStore.Audio.Media.MIME_TYPE
                };


        String sortOrder = MediaStore.Audio.Media.DISPLAY_NAME + " ASC";


        try (Cursor cursor = context.getContentResolver().query(collection, projection, null, null, sortOrder)) {
            int idColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media._ID);
            int nameColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DISPLAY_NAME);
            int artistColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST);
            int duarationColoumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION);
            int mimeTypeColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.MIME_TYPE);

            while (cursor.moveToNext()) {
                long id = cursor.getLong(idColumn);
                String name = cursor.getString(nameColumn);
                String artist = cursor.getString(artistColumn);
                int duration = cursor.getInt(duarationColoumn);
                String mimeType = cursor.getString(mimeTypeColumn);

                Uri contentUri = ContentUris.withAppendedId(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, id);

                if (mimeType.equals("audio/x-wav")) {
                    LocalTrack localTrack = new LocalTrack(name, artist, contentUri.toString(), duration);
                    return new LocalAndroidTrack(localTrack, context);
                }
            }
        }
        return null;
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
