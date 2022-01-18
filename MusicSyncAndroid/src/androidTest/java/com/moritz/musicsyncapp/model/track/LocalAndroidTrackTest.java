package com.moritz.musicsyncapp.model.track;



import android.content.Context;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;
import android.content.Context;

import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.*;

import com.moritz.musicsyncapp.AndroidMusicSyncFactory;

import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
class LocalAndroidTrackTest {

   @Test
    void getByUri() {

    Context appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();
    AndroidMusicSyncFactory.init(appContext);

    AndroidMusicSyncFactory.get().getPlaylistController().getSystemPlaylists();
   }

    @Test
    void getDuration() {
    }
}