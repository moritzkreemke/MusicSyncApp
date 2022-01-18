package com.moritz.musicsyncapp;

import android.os.Bundle;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.moritz.musicsyncapp.databinding.ActivityMainBinding;
import com.moritz.musicsyncapp.model.session.ISession;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;

    private BottomNavigationView navView;
    private PropertyChangeListener sessionChangeListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        navView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
               R.id.pair_devices, R.id.session_playlist, R.id.local_playlist)
                .build();

        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_activity_main);
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(binding.navView, navController);

        sessionChangeListener = evt -> {
            ISession session = (ISession) evt.getNewValue();
            setSessionPlaylistOptionAvailable(session.exits());
        };
        setSessionPlaylistOptionAvailable(AndroidMusicSyncFactory.get().getSessionController().getSession().exits());
        AndroidMusicSyncFactory.get().getSessionController().addSessionChangeListener(sessionChangeListener);

    }

    private void setSessionPlaylistOptionAvailable (boolean available)
    {
        runOnUiThread(() -> navView.getMenu().findItem(R.id.session_playlist).setEnabled(available));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        AndroidMusicSyncFactory.get().getSessionController().removeSessionChangeListener(sessionChangeListener);
    }
}

