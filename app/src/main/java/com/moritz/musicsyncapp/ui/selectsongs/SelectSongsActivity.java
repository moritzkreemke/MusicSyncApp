package com.moritz.musicsyncapp.ui.selectsongs;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NavUtils;
import androidx.recyclerview.selection.ItemDetailsLookup;
import androidx.recyclerview.selection.ItemKeyProvider;
import androidx.recyclerview.selection.Selection;
import androidx.recyclerview.selection.SelectionTracker;
import androidx.recyclerview.selection.StorageStrategy;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.os.Parcel;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;

import com.moritz.musicsyncapp.AndroidMusicSyncFactory;
import com.moritz.musicsyncapp.R;

public class SelectSongsActivity extends AppCompatActivity {

    public static final int SUCCESS_CODE = 1;
    public static final int ABORT_CODE = 2;
    public static final String SELECT_SONGS_RESULT = "com.moritz.musicsyncapp.ui.selectsongs.SelectSongsActivity.RESULT";

    private SelectSongsAdapter selectSongsAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_songs);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        RecyclerView songsRV = findViewById(R.id.recylcer_view_select_songs);
        songsRV.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        selectSongsAdapter = new SelectSongsAdapter(AndroidMusicSyncFactory.get().getPlaylistController().getSystemPlaylists()[0]);
        songsRV.setAdapter(selectSongsAdapter);

        Button cancelBtn = findViewById(R.id.btn_select_songs_cancel);
        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setResult(ABORT_CODE);
                onBackPressed();
            }
        });

        Button saveBtn = findViewById(R.id.btn_select_songs_save);
        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onSave();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.select_songs_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()) {
            case android.R.id.home:
                setResult(ABORT_CODE);
                onBackPressed();
                return true;
            case R.id.select_songs_save:
                onSave();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void onSave () {
        Intent intent = new Intent();
        intent.putExtra(SELECT_SONGS_RESULT, selectSongsAdapter.getSelectedTracks());
        setResult(SUCCESS_CODE, intent);
        super.onBackPressed();
    }


}