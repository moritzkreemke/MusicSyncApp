package com.moritz.musicsyncapp.ui.sessionplaylist;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.moritz.musicsyncapp.AndroidMusicSyncFactory;
import com.moritz.musicsyncapp.R;
import com.moritz.musicsyncapp.model.playlist.IPlaylist;
import com.moritz.musicsyncapp.model.session.ISession;
import com.moritz.musicsyncapp.model.track.ITrack;
import com.moritz.musicsyncapp.model.track.LocalAndroidTrack;
import com.moritz.musicsyncapp.ui.selectsongs.SelectSongsActivity;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * create an instance of this fragment.
 */
public class SessionPlaylistFragment extends Fragment {



    private PropertyChangeListener sessionChanged;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_session_playlist, container, false);

        FloatingActionButton addSongsBtn = view.findViewById(R.id.btn_add_song_to_session_playlist);
        ActivityResultLauncher<Intent> activityLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult o) {
                        if(o.getResultCode() == SelectSongsActivity.SUCCESS_CODE) {
                            String[] selectedTracks = o.getData().getStringArrayExtra(SelectSongsActivity.SELECT_SONGS_RESULT);
                            for (int i = 0; i < selectedTracks.length; i++) {
                                ITrack localTrack = LocalAndroidTrack.getByUri(selectedTracks[i], getContext());
                                AndroidMusicSyncFactory.get().getSessionController().getSession().getSessionPlaylist().addTrack(localTrack);
                            }
                        }
                    }
                });
        addSongsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SessionPlaylistFragment.this.getActivity(), SelectSongsActivity.class);
                activityLauncher.launch(intent);
            }
        });

        RecyclerView sessionSongsRV = view.findViewById(R.id.recycler_view_session_playlist_songs);
        sessionSongsRV.setLayoutManager(new LinearLayoutManager(getContext()));
        SessionPlaylistAdapter sessionPlaylistAdapter = new SessionPlaylistAdapter();
        sessionSongsRV.setAdapter(sessionPlaylistAdapter);

        sessionChanged = new PropertyChangeListener() {
            @Override
            public void propertyChange(PropertyChangeEvent evt) {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        ISession session = (ISession) evt.getNewValue();
                        sessionPlaylistAdapter.setTrackList(Arrays.asList(session.getSessionPlaylist().getTracks()));
                    }
                });

            }
        };
        AndroidMusicSyncFactory.get().getSessionController().addSessionChangeListener(sessionChanged);

        sessionPlaylistAdapter.setTrackList(Arrays.asList(AndroidMusicSyncFactory.get().getSessionController().getSession().getSessionPlaylist().getTracks()));
        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        AndroidMusicSyncFactory.get().getSessionController().addSessionChangeListener(sessionChanged);
    }
}