package com.moritz.musicsyncapp.controller.sound;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;

import com.moritz.musicsyncapp.AndroidMusicSyncFactory;
import com.moritz.musicsyncapp.controller.sound.services.MusicPlaybackService;
import com.moritz.musicsyncapp.model.track.IPlayableTrack;

public class LocalSoundController extends SoundControllerBase {


    MusicPlaybackService mService;
    boolean mBound = false;
    private MusicPlaybackService.LocalBinder binder;
    private Intent serviceIntent = null;

   private Context context;

    public LocalSoundController(Context context) {
        this.context = context;
        serviceIntent = new Intent(context, MusicPlaybackService.class);
        context.bindService(serviceIntent, connection, Context.BIND_AUTO_CREATE);
    }

    @Override
    public void play(IPlayableTrack iTrack) {
        super.play(iTrack);
        context.startService(serviceIntent);
        //todo not the best approach
        if(!mBound)
            System.out.println("shit I should care about that");
        binder.play(iTrack);
    }

    @Override
    public void stop() {
        context.stopService(serviceIntent);
    }

    /**
     * Defines callbacks for service binding, passed to bindService()
     */
    private ServiceConnection connection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName className,
                                       IBinder service) {
            // We've bound to LocalService, cast the IBinder and get LocalService instance
            binder = (MusicPlaybackService.LocalBinder) service;
            mService = binder.getService();
            mBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            mBound = false;
        }
    };
}
