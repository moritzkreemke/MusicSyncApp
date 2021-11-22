package com.moritz.musicsyncapp.model.playlist.providers;

import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;

import com.moritz.musicsyncapp.model.playlist.IPlaylist;
import com.moritz.musicsyncapp.model.playlist.PlaylistBuilder;
import com.moritz.musicsyncapp.model.track.LocalTrack;

import java.util.ArrayList;
import java.util.List;

public class MediaStorePlaylistProvider implements IPlaylistProvider{


    private Context context;

    public MediaStorePlaylistProvider(Context context) {
        this.context = context;
    }

    @Override
    public String getName() {
        return "ANDROID_MEDIA_STORE";
    }

    @Override
    public IPlaylist[] getAllPlaylists() {

        PlaylistBuilder builder =  PlaylistBuilder.create(getName(), "LOCAL");

        Uri collection;
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            //collection = MediaStore.Audio.Media.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY);
            collection = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        } else {
            collection = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        }

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

                if(mimeType.equals("audio/x-wav")) {
                    LocalTrack localTrack = new LocalTrack(name, artist, contentUri.toString(), duration);
                    builder.addTrack(localTrack);
                }
            }
        }

        return new IPlaylist[] { builder.build() };
    }
}
