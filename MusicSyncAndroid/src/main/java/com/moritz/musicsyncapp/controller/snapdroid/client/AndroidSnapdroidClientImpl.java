package com.moritz.musicsyncapp.controller.snapdroid.client;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.moritz.musicsyncapp.controller.snapdroid.ISnapdroidClient;
import com.moritz.musicsyncapp.controller.snapdroid.server.AndroidSnapdroidServerImpl;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.util.UUID;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;

public class AndroidSnapdroidClientImpl implements ISnapdroidClient {
    public static final String TAG = AndroidSnapdroidClientImpl.class.toString();

    private AtomicBoolean running = new AtomicBoolean(false);
    private Context context;

    private Process clientProcess;

    public AndroidSnapdroidClientImpl(Context context) {
        this.context = context;
    }

    //default port 1704
    public void connectInternal(InetAddress addr, int port, String ID) {
        synchronized (this) {
            if(running.get())
                return;
            running.set(true);
        }

        try {
            String sampleFormat = "*:16:*";
            ProcessBuilder pb = new ProcessBuilder()
                    .command(context.getApplicationInfo().nativeLibraryDir + "/libsnapclient.so",
                            "-h", addr.getHostAddress(),
                            "-p", Integer.toString(port),
                            "--hostID", ID,
                            "--player", "oboe",
                            "--sampleformat", sampleFormat,
                            "--logfilter", "*:info,Stats:debug")
                    .redirectErrorStream(true);

            clientProcess = pb.start();

            Thread reader = new Thread(new Runnable() {
                @Override
                public void run() {
                    BufferedReader bufferedReader = new BufferedReader(
                            new InputStreamReader(clientProcess.getInputStream()));
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
        } catch (IOException e) {
            e.printStackTrace();
            running.set(false);
        }


    }
    @Override
    public void connect(InetAddress addr, int port, String ID) {
        Executors.newSingleThreadExecutor().execute(new Runnable() {
            @Override
            public void run() {
                connectInternal(addr, port, ID);
            }
        });
    }


    @Override
    public void close() {
        clientProcess.destroy();
        running.set(false);
    }
}
