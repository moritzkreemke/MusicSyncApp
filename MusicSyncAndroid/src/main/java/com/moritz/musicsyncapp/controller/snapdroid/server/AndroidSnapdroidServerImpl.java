package com.moritz.musicsyncapp.controller.snapdroid.server;

import android.content.Context;
import android.util.Log;

import com.moritz.musicsyncapp.controller.snapdroid.ISnapdroidServer;
import com.moritz.musicsyncapp.model.track.IPlayableTrack;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;

public class AndroidSnapdroidServerImpl implements ISnapdroidServer {

    public static final String TAG = AndroidSnapdroidServerImpl.class.toString();

    private AtomicBoolean running = new AtomicBoolean(false);
    private Context context;

    private File pipe48_16_2;
    private File pipe44_16_2;

    private FileOutputStream pipe48_16_2_out;
    private FileOutputStream pipe44_16_2_out;

    private Process serverProcess;

    public AndroidSnapdroidServerImpl(Context context) {
        this.context = context;
        pipe48_16_2 = new File(context.getCacheDir().toString() + "/snapcast48162.fifo");
        pipe44_16_2 = new File(context.getCacheDir().toString() + "/snapcast44162.fifo");
    }

    @Override
    public void start() {
        synchronized (this) {
            if(running.get())
                return;
            running.set(true);
        }
        try {
        String cache = context.getCacheDir().toString();
        ProcessBuilder pb = new ProcessBuilder()
                .command(context.getApplicationInfo().nativeLibraryDir + "/libsnapserver.so", "--server.datadir=" + cache,
                        "--stream.source=pipe:///" + pipe48_16_2.getAbsolutePath() + "?name=fifo&mode=create&sampleformat=48000:16:2&codec=flac",
                        "--stream.source=pipe:///" + pipe44_16_2 + "?name=fifo2&mode=create&sampleformat=24100:16:2&codec=flac")
                .redirectErrorStream(true);


            serverProcess = pb.start();

            pipe48_16_2_out = new FileOutputStream(pipe48_16_2);
            pipe44_16_2_out = new FileOutputStream(pipe44_16_2);
        } catch (IOException e) {
            e.printStackTrace();
            running.set(false);
        }

        Thread reader = new Thread(new Runnable() {
            @Override
            public void run() {
                BufferedReader bufferedReader = new BufferedReader(
                        new InputStreamReader(serverProcess.getInputStream()));
                String line;
                try {
                    while ((line = bufferedReader.readLine()) != null) {
                        Log.d(TAG, "run: " + line);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        reader.start();
    }

    @Override
    public void shutdown() {
        serverProcess.destroy();
        try {
        if(pipe48_16_2_out != null) {
                pipe48_16_2_out.close();
        }
        if(pipe44_16_2_out != null) {
            pipe44_16_2_out.close();
        }
        } catch (IOException e) {
            e.printStackTrace();
        }
        running.set(false);
    }

    @Override
    public void playTrack(IPlayableTrack playableTrack) {
        Executors.newSingleThreadExecutor().execute(new Runnable() {
            @Override
            public void run() {
                final int BUFFER_SIZE = 500000;
                byte[] buffer = new byte[BUFFER_SIZE];
                try {
                    InputStream stream = playableTrack.getStream();
                    int i = 0;
                    while (((i = stream.read(buffer)) != -1)) {
                        pipe48_16_2_out.write(buffer, 0, i);
                    }
                    stream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

    }

}
