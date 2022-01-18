package com.moritz.musicsyncapp;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import android.content.Context;

public class MockedAndroidSyncFactory {


    public static IAndroidSyncFactory get (Context context) {
        IAndroidSyncFactory androidSyncFactory = mock(AndroidMusicSyncFactory.class);
        AndroidMusicSyncFactory orginal = new AndroidMusicSyncFactory(context);

        when(androidSyncFactory.getCommuicationClient()).thenReturn(orginal.getCommuicationClient());
        when(androidSyncFactory.getCommuicationServer()).thenReturn(orginal.getCommuicationServer());
        when(androidSyncFactory.getNetworkController(null)).thenReturn(orginal.getNetworkController(null));
        when(androidSyncFactory.getPlaylistController()).thenReturn(orginal.getPlaylistController());
        when(androidSyncFactory.getLocalSoundController()).thenReturn(orginal.getLocalSoundController());
        when(androidSyncFactory.getSnapdroidClient()).thenReturn(orginal.getSnapdroidClient());
        when(androidSyncFactory.getSnapdroidServer()).thenReturn(orginal.getSnapdroidServer());
        when(androidSyncFactory.getSessionController()).thenReturn(orginal.getSessionController());

        return androidSyncFactory;

    }

}
