package com.moritz.musicsyncapp.ui.selectsongs;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.moritz.musicsyncapp.R;
import com.moritz.musicsyncapp.model.playlist.IPlaylist;
import com.moritz.musicsyncapp.model.track.ITrack;

import java.util.ArrayList;
import java.util.List;


public class SelectSongsAdapter extends RecyclerView.Adapter<SelectSongsAdapter.SongsHolder>{


    private IPlaylist playlist;
    private List<ITrack> trackList = new ArrayList<>();

    public SelectSongsAdapter(IPlaylist playlist) {
        this.playlist = playlist;
    }

    @NonNull
    @Override
    public SongsHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View trackView = LayoutInflater.from(parent.getContext()).inflate(R.layout.fragment_select_track_item, parent, false);

        return new SelectSongsAdapter.SongsHolder(trackView);
    }

    @Override
    public void onBindViewHolder(@NonNull SongsHolder songsHolder, int i) {
        ITrack currentTrack = playlist.getTracks()[i];
        songsHolder.layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(songsHolder.changeState()) {
                    trackList.add(currentTrack);
                } else {
                    trackList.remove(currentTrack);
                }
            }
        });

        songsHolder.textViewSongTile.setText(currentTrack.getName());

    }

    @Override
    public int getItemCount() {
        return playlist.getTracks().length;
    }

    public String[] getSelectedTracks () {
        String[] result = new String[trackList.size()];
        for (int i = 0; i < trackList.size(); i++) {
            result[i] = trackList.get(i).getUri();
        }
        return result;
    }

    static class SongsHolder extends RecyclerView.ViewHolder {

        private boolean selected = false;
        private final ConstraintLayout layout;
        private final TextView textViewSongTile;
        private final ImageView imageViewSelected;


        public SongsHolder(@NonNull View itemView) {
            super(itemView);
            layout = itemView.findViewById(R.id.constraint_layout_select_track_item);
            textViewSongTile = itemView.findViewById(R.id.text_view_select_track_song_name);
            imageViewSelected = itemView.findViewById(R.id.image_view_track_selected);
            imageViewSelected.setVisibility(View.INVISIBLE);
        }

        private boolean changeState () {
            if(selected) {
                selected = false;
                imageViewSelected.setVisibility(View.INVISIBLE);
            } else {
                selected = true;
                imageViewSelected.setVisibility(View.VISIBLE);
            }
            return selected;
        }
    }

}
