package com.moritz.musicsyncapp;


import androidx.test.core.app.ApplicationProvider;
import androidx.test.espresso.DataInteraction;
import androidx.test.espresso.ViewInteraction;
import androidx.test.filters.LargeTest;
import androidx.test.rule.ActivityTestRule;
import androidx.test.runner.AndroidJUnit4;

import android.content.Intent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;

import static androidx.test.InstrumentationRegistry.getContext;
import static androidx.test.InstrumentationRegistry.getInstrumentation;
import static androidx.test.InstrumentationRegistry.getTargetContext;
import static androidx.test.espresso.Espresso.onData;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.Espresso.pressBack;
import static androidx.test.espresso.Espresso.openActionBarOverflowOrOptionsMenu;
import static androidx.test.espresso.action.ViewActions.*;
import static androidx.test.espresso.assertion.ViewAssertions.*;
import static androidx.test.espresso.matcher.ViewMatchers.*;

import com.moritz.musicsyncapp.R;
import com.moritz.musicsyncapp.controller.commuication.client.ICommunicationClient;
import com.moritz.musicsyncapp.controller.commuication.server.ICommunicationServer;
import com.moritz.musicsyncapp.controller.p2pnetwork.IP2PNetworkController;
import com.moritz.musicsyncapp.controller.playlist.IPlaylistController;
import com.moritz.musicsyncapp.controller.session.ISessionController;
import com.moritz.musicsyncapp.controller.snapdroid.ISnapdroidClient;
import com.moritz.musicsyncapp.controller.snapdroid.ISnapdroidServer;
import com.moritz.musicsyncapp.controller.sound.ISoundController;
import com.moritz.musicsyncapp.model.client.IClient;
import com.moritz.musicsyncapp.model.playlist.IPlaylist;
import com.moritz.musicsyncapp.model.session.ISession;
import com.moritz.musicsyncapp.model.track.ITrack;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.hamcrest.core.IsInstanceOf;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.MockedStatic;
import org.mockito.internal.matchers.And;

import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.anything;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.when;

import java.beans.PropertyChangeListener;

@LargeTest
@RunWith(AndroidJUnit4.class)
public class MainActivityTest {

    @Rule
    public ActivityTestRule<MainActivity> mActivityTestRule = new ActivityTestRule<>(MainActivity.class);

    @Test
    public void mainActivityNoSessionTest() {
        ViewInteraction textView = onView(
                allOf(withText("Your Songs"),
                        withParent(allOf(withId(R.id.action_bar),
                                withParent(withId(R.id.action_bar_container)))),
                        isDisplayed()));
        textView.check(matches(withText("Your Songs")));

        ViewInteraction bottomNavigationItemView = onView(
                allOf(withId(R.id.pair_devices), withContentDescription("Devices"),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.nav_view),
                                        0),
                                1),
                        isDisplayed()));
        bottomNavigationItemView.perform(click());

        ViewInteraction textView2 = onView(
                allOf(withText("Pair Devices"),
                        withParent(allOf(withId(R.id.action_bar),
                                withParent(withId(R.id.action_bar_container)))),
                        isDisplayed()));
        textView2.check(matches(withText("Pair Devices")));

        ViewInteraction bottomNavigationItemViewSessionPlaylist = onView(
                allOf(withId(R.id.session_playlist), withContentDescription("Shared Playlist"),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.nav_view),
                                        0),
                                1),
                        isDisplayed()));
        bottomNavigationItemView.perform(click());

        ViewInteraction textView3 = onView(
                allOf(withText("Pair Devices"),
                        withParent(allOf(withId(R.id.action_bar),
                                withParent(withId(R.id.action_bar_container)))),
                        isDisplayed()));
        textView3.check(matches(withText("Pair Devices")));

    }

    @Test
    public void mainActivitySessionExistsTest() {

        //create a mock session which always exits
        ISession mockSession = mock(ISession.class);
        when(mockSession.exits()).thenReturn(true);

        //create a mock playlist
        IPlaylist mockPlaylist = mock(IPlaylist.class);
        when(mockPlaylist.getTracks()).thenReturn(new ITrack[0]);
        when(mockSession.getSessionPlaylist()).thenReturn(mockPlaylist);

        //register mock AndroidSyncFacotry
        IAndroidSyncFactory androidSyncFactory = MockedAndroidSyncFactory.get(ApplicationProvider.getApplicationContext());
        AndroidMusicSyncFactory.init(androidSyncFactory);

        //create a mock session controller, which returns the mocked session
        ISessionController sessionController = mock(ISessionController.class);
        when(sessionController.getSession()).thenReturn(mockSession);
        when(androidSyncFactory.getSessionController()).thenReturn(sessionController);

        //reopen the activity, so that everything is loaded properly
        mActivityTestRule.finishActivity();
        mActivityTestRule.launchActivity(new Intent());

        //now click on shared Playlist Button
        ViewInteraction bottomNavigationItemViewSessionPlaylist = onView(
                allOf(withId(R.id.session_playlist), withContentDescription("Shared Playlist")));
        bottomNavigationItemViewSessionPlaylist.perform(click());

        //check if the label in action bar displays Shared Playlist, if the case, the Shared Playlist is displayed
        ViewInteraction textView3 = onView(
                allOf(withText("Shared Playlist"),
                        withParent(allOf(withId(R.id.action_bar),
                                withParent(withId(R.id.action_bar_container)))),
                        isDisplayed()));
        textView3.check(matches(withText("Shared Playlist")));
    }


    private static Matcher<View> childAtPosition(
            final Matcher<View> parentMatcher, final int position) {

        return new TypeSafeMatcher<View>() {
            @Override
            public void describeTo(Description description) {
                description.appendText("Child at position " + position + " in parent ");
                parentMatcher.describeTo(description);
            }

            @Override
            public boolean matchesSafely(View view) {
                ViewParent parent = view.getParent();
                return parent instanceof ViewGroup && parentMatcher.matches(parent)
                        && view.equals(((ViewGroup) parent).getChildAt(position));
            }
        };
    }
}
