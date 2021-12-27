package com.moritz.musicsyncapp.ui.sessionplaylist;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.moritz.musicsyncapp.R;
import com.moritz.musicsyncapp.model.playlist.IPlaylist;
import com.moritz.musicsyncapp.ui.selectsongs.SelectSongsActivity;

/**
 * A simple {@link Fragment} subclass.
 * create an instance of this fragment.
 */
public class SessionPlaylistFragment extends Fragment {




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

                    }
                });
        addSongsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SessionPlaylistFragment.this.getActivity(), SelectSongsActivity.class);
                activityLauncher.launch(intent);
            }
        });

        return view;
    }
}