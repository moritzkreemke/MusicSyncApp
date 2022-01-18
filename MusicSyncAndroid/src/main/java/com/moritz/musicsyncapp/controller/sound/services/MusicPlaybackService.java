package com.moritz.musicsyncapp.controller.sound.services;

import static android.media.session.PlaybackState.ACTION_PLAY;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.media.AudioAttributes;
import android.media.AudioFormat;
import android.media.AudioTrack;
import android.media.MediaMetadata;
import android.media.MediaSession2;
import android.media.session.MediaSession;
import android.media.session.PlaybackState;
import android.os.Binder;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.media.MediaBrowserCompat;
import android.support.v4.media.MediaMetadataCompat;
import android.support.v4.media.session.MediaSessionCompat;
import android.support.v4.media.session.PlaybackStateCompat;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;
import androidx.media.MediaBrowserServiceCompat;


import com.moritz.musicsyncapp.AndroidMusicSyncFactory;

import com.moritz.musicsyncapp.R;
import com.moritz.musicsyncapp.model.track.IPlayableTrack;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class MusicPlaybackService extends Service {
    public MusicPlaybackService() {
    }

    private final IBinder binder = new LocalBinder();

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        return binder;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        //called only once the service is created
    }

    @RequiresApi(api = Build.VERSION_CODES.Q)
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        //always when startService(Intent) is called
        super.onStartCommand(intent,flags,startId);

        //Intent notificationIntent = new Intent(this, MainActivity.class);
        //PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, PendingIntent.FLAG_IMMUTABLE);

        MediaSession mediaSession = new MediaSession(getApplicationContext(), "LOL");
        mediaSession.setFlags(MediaSession.FLAG_HANDLES_MEDIA_BUTTONS);
        PlaybackState playbackState = new PlaybackState.Builder().setState(PlaybackState.STATE_PLAYING,
                1, 1).build();
        MediaMetadata metadataCompat = new MediaMetadata.Builder().putString("String", "String").build();
        mediaSession.setPlaybackState(playbackState);
        mediaSession.setMetadata(metadataCompat);
        mediaSession.setActive(true);

        Notification.MediaStyle mediaStyle = new Notification.MediaStyle();
        mediaStyle.setMediaSession(mediaSession.getSessionToken());

        Notification mediaNotfication = new Notification.Builder(this, AndroidMusicSyncFactory.MUSIC_PLAYER_CHANNEL_ID)
                .setVisibility(Notification.VISIBILITY_PUBLIC)
                .setSmallIcon(R.drawable.ic_android_black_24dp)
                .setContentTitle("Track title")
                .setContentText("Artist - Album")
                .setStyle(mediaStyle)
                .build();

        startForeground(1, mediaNotfication);

        return START_NOT_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    /**
     * Class used for the client Binder.  Because we know this service always
     * runs in the same process as its clients, we don't need to deal with IPC.
     */
    public class LocalBinder extends Binder {
        public MusicPlaybackService getService() {
            // Return this instance of LocalService so clients can call public methods
            return MusicPlaybackService.this;
        }

        public void play (IPlayableTrack track)
        {

            Executor executor = Executors.newSingleThreadExecutor();
            executor.execute(new Runnable() {
                @Override
                public void run() {
                    final int BUFFER_SIZE = 500000;
                    byte[] buffer = new byte[BUFFER_SIZE];

                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
                        AudioTrack player = new AudioTrack.Builder()
                                .setAudioAttributes(new AudioAttributes.Builder()
                                        .setUsage(AudioAttributes.USAGE_MEDIA)
                                        .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                                        .build())
                                .setAudioFormat(new AudioFormat.Builder()
                                        .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
                                        .setSampleRate(48000)
                                        .setChannelMask(AudioFormat.CHANNEL_OUT_STEREO)
                                        .build())
                                .setBufferSizeInBytes(BUFFER_SIZE)
                                .setTransferMode(AudioTrack.MODE_STREAM)
                                .build();


                        int i = 0;
                        player.play();
                        try {
                            InputStream stream = track.getStream();
                            while (((i = stream.read(buffer)) != -1)) {
                                player.write(buffer, 0, i);
                            }
                            stream.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                    }
                }
            });
        }

    }

}