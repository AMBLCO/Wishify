package com.wishify;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentContainerView;
import androidx.fragment.app.FragmentManager;

import android.os.Bundle;
import android.view.MenuItem;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Get layout elements
        BottomNavigationView navigView = findViewById(R.id.bottom_navigation);

        // Variables
        FragmentManager fragMana = getSupportFragmentManager();

        // On commence sur la page des chansons
        navigView.setSelectedItemId(R.id.songs);

        navigView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener(){

            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                if (item.getItemId() == R.id.artists)
                {
                    // On doit changer de Fragment si et seulement si le fragment visible n'est pas celui désiré
                    if (!(fragMana.findFragmentById(R.id.mainFragmentContainerView) instanceof ArtistsFragment))
                    {
                        fragMana.beginTransaction()
                                .setReorderingAllowed(true)
                                .replace(R.id.mainFragmentContainerView, ArtistsFragment.class, null)
                                .commit();

                    }
                    return true;
                }
                else if (item.getItemId() == R.id.albums)
                {
                    // On doit changer de Fragment si et seulement si le fragment visible n'est pas celui désiré
                    if (!(fragMana.findFragmentById(R.id.mainFragmentContainerView) instanceof AlbumsFragment))
                    {
                        fragMana.beginTransaction()
                                .setReorderingAllowed(true)
                                .replace(R.id.mainFragmentContainerView, AlbumsFragment.class, null)
                                .commit();

                    }
                    return true;
                }
                else if (item.getItemId() == R.id.songs)
                {
                    // On doit changer de Fragment si et seulement si le fragment visible n'est pas celui désiré
                    if (!(fragMana.findFragmentById(R.id.mainFragmentContainerView) instanceof SongsFragment))
                    {
                        fragMana.beginTransaction()
                                .setReorderingAllowed(true)
                                .replace(R.id.mainFragmentContainerView, SongsFragment.class, null)
                                .commit();

                    }
                    return true;
                }
                else if (item.getItemId() == R.id.playlists)
                {
                    // On doit changer de Fragment si et seulement si le fragment visible n'est pas celui désiré
                    if (!(fragMana.findFragmentById(R.id.mainFragmentContainerView) instanceof PlaylistsFragment))
                    {
                        fragMana.beginTransaction()
                                .setReorderingAllowed(true)
                                .replace(R.id.mainFragmentContainerView, PlaylistsFragment.class, null)
                                .commit();

                    }
                    return true;
                }
                else { return false; }
            }
        });
    }




}