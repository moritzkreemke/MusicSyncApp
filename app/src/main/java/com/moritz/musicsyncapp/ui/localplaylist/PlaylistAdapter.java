package com.moritz.musicsyncapp.ui.localplaylist;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.moritz.musicsyncapp.AndroidMusicSyncFactory;
import com.moritz.musicsyncapp.R;
import com.moritz.musicsyncapp.model.playlist.IPlaylist;
import com.moritz.musicsyncapp.model.track.ITrack;

import java.util.List;

public class PlaylistAdapter extends RecyclerView.Adapter<PlaylistAdapter.PlaylistHolder> {

    private IPlaylist playlist;

    public PlaylistAdapter(Context context) {
        playlist = AndroidMusicSyncFactory.get(context).getPlaylistController().getSystemPlaylists()[0];
    }

    @NonNull
    @Override
    public PlaylistHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View trackView = LayoutInflater.from(parent.getContext()).inflate(R.layout.fragment_track, parent, false);

        return new PlaylistHolder(trackView);
    }

    @Override
    public void onBindViewHolder(@NonNull PlaylistHolder holder, int position) {
        ITrack currentTrack = playlist.getTracks()[position];
        holder.textViewArtistTitle.setText(currentTrack.getArtist());
        holder.textViewSongTile.setText(currentTrack.getName());
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.out.println("jo");
            }
        });
    }

    @Override
    public int getItemCount() {
        return playlist.getTracks().length;
    }

    public void test ()
    {
        notifyDataSetChanged();
    }

    static class PlaylistHolder extends RecyclerView.ViewHolder {

        private final TextView textViewSongTile;
        private final TextView textViewArtistTitle;

        public PlaylistHolder(@NonNull View itemView) {
            super(itemView);
            textViewSongTile = itemView.findViewById(R.id.textView_song_title);
            textViewArtistTitle = itemView.findViewById(R.id.textView_artist_title);
        }
    }

}
