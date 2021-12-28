package com.moritz.musicsyncapp.ui.sessionplaylist;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.moritz.musicsyncapp.AndroidMusicSyncFactory;
import com.moritz.musicsyncapp.R;
import com.moritz.musicsyncapp.model.track.IPlayableTrack;
import com.moritz.musicsyncapp.model.track.ITrack;
import com.moritz.musicsyncapp.ui.pairdevice.AvailableDevicesAdapter;
import com.moritz.musicsyncapp.ui.selectsongs.SelectSongsAdapter;

import java.util.ArrayList;
import java.util.List;

class SessionPlaylistAdapter extends RecyclerView.Adapter<SessionPlaylistAdapter.SessionTracksHolder> {

    private List<ITrack> trackList = new ArrayList<>();

    @NonNull
    @Override
    public SessionTracksHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        View trackView = LayoutInflater.from(parent.getContext()).inflate(R.layout.fragment_session__playlist__track, parent, false);

        return new SessionPlaylistAdapter.SessionTracksHolder(trackView);
    }

    @Override
    public void onBindViewHolder(@NonNull SessionTracksHolder sessionTracksHolder, int i) {
        ITrack track = trackList.get(i);
        sessionTracksHolder.layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(track instanceof IPlayableTrack) {
                    AndroidMusicSyncFactory.get().getSnapdroidServer().playTrack((IPlayableTrack) track);
                }
            }
        });
        sessionTracksHolder.trackName.setText(track.getName());
    }

    @Override
    public int getItemCount() {
        return trackList.size();
    }

    public void setTrackList(List<ITrack> trackList) {
        this.trackList = trackList;
        notifyDataSetChanged();
    }

    static class SessionTracksHolder extends RecyclerView.ViewHolder {

        ConstraintLayout layout;
        TextView trackName;

        public SessionTracksHolder(@NonNull View itemView) {
            super(itemView);
            trackName = itemView.findViewById(R.id.text_view_session_playlist_track_name);
            layout = itemView.findViewById(R.id.constraint_layout_session_playlist_track_item);
        }
    }

}
