package com.moritz.musicsyncapp.services;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.AudioAttributes;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.os.AsyncTask;
import android.os.Binder;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.media.MediaBrowserCompat;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.media.MediaBrowserServiceCompat;

import com.moritz.musicsyncapp.Configuration;
import com.moritz.musicsyncapp.MainActivity;
import com.moritz.musicsyncapp.R;
import com.moritz.musicsyncapp.controller.sound.ISoundController;
import com.moritz.musicsyncapp.model.track.IPlayableTrack;
import com.moritz.musicsyncapp.model.track.ITrack;

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

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        //always when startService(Intent) is called
        super.onStartCommand(intent,flags,startId);

        Intent notificationIntent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, PendingIntent.FLAG_IMMUTABLE);


        Notification notification = new NotificationCompat.Builder(this, Configuration.CHANNEL_ID)
                .setContentTitle("Test")
                .setContentText("Text")
                .setSmallIcon(R.drawable.ic_dashboard_black_24dp)
                .setContentIntent(pendingIntent)
                .build();

        startForeground(1, notification);

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