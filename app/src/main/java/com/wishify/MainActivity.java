package com.wishify;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentContainerView;
import androidx.fragment.app.FragmentManager;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkRequest;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageButton;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.navigation.NavigationBarView;

import java.lang.ref.WeakReference;

public class MainActivity extends AppCompatActivity {

    Intent audioPlayerServiceIntent;
    public static WeakReference<MainActivity> weakReference;

    // Arbitrary Constants
    private static final int READ_MEDIA_AUDIO_CODE = 10;
    private static final int READ_EXTERNAL_STORAGE_CODE = 11;

    private BottomSheetBehavior mBottomSheetBehavior;
    private FrameLayout mBottomSheet;
    private FragmentManager fragMana;


    // Get instances of fragments
    private ArtistsFragment artistsFragment;
    private AlbumsFragment albumsFragment;
    private SongsFragment songsFragment;
    private PlaylistsFragment playlistsFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Get layout elements
        BottomNavigationView navigView = findViewById(R.id.bottom_navigation);
        fragMana = getSupportFragmentManager();

        mBottomSheet = findViewById(R.id.bottom_sheet);
        mBottomSheet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //add popup menu with more options like seeking or skipping the track
            }
        });

        mBottomSheetBehavior = BottomSheetBehavior.from(mBottomSheet);
        mBottomSheetBehavior.setDraggable(false);

        weakReference = new WeakReference<>(MainActivity.this);

        artistsFragment = new ArtistsFragment();
        albumsFragment = new AlbumsFragment();
        songsFragment = new SongsFragment();
        playlistsFragment = new PlaylistsFragment();

        // Variables


        fragMana.beginTransaction()
                .add(R.id.mainFragmentContainerView, artistsFragment, "artists")
                .add(R.id.mainFragmentContainerView, albumsFragment, "albums")
                .add(R.id.mainFragmentContainerView, songsFragment, "songs")
                .add(R.id.mainFragmentContainerView, playlistsFragment, "playlists")
                .hide(artistsFragment)
                .hide(albumsFragment)
                .hide(playlistsFragment)
                .show(songsFragment)
                .commit();

        // On commence sur la page des chansons
        navigView.setSelectedItemId(R.id.songs);

        navigView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener(){

            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                if (item.getItemId() == R.id.artists)
                {
                    // On doit changer de Fragment si et seulement si le fragment visible n'est pas celui désiré
                    if (artistsFragment.isHidden())
                    {
                        fragMana.beginTransaction()
                                .show(artistsFragment)
                                .hide(albumsFragment)
                                .hide(playlistsFragment)
                                .hide(songsFragment)
                                .commit();

                    }
                    return true;
                }
                else if (item.getItemId() == R.id.albums)
                {
                    // On doit changer de Fragment si et seulement si le fragment visible n'est pas celui désiré
                    if (albumsFragment.isHidden())
                    {
                        fragMana.beginTransaction()
                                .hide(artistsFragment)
                                .show(albumsFragment)
                                .hide(playlistsFragment)
                                .hide(songsFragment)
                                .commit();

                    }
                    return true;
                }
                else if (item.getItemId() == R.id.songs)
                {
                    // On doit changer de Fragment si et seulement si le fragment visible n'est pas celui désiré
                    if (songsFragment.isHidden())
                    {
                        fragMana.beginTransaction()
                                .hide(artistsFragment)
                                .hide(albumsFragment)
                                .hide(playlistsFragment)
                                .show(songsFragment)
                                .commit();

                        //SongsFragment songsFragment = ((SongsFragment) getSupportFragmentManager().findFragmentById(R.id.mainFragmentContainerView));
                        //if (songsFragment != null) {
                        //songsFragment.setmMainActivity(MainActivity.this);
                        //}
                    }
                    return true;
                }
                else if (item.getItemId() == R.id.playlists)
                {
                    // On doit changer de Fragment si et seulement si le fragment visible n'est pas celui désiré
                    if (playlistsFragment.isHidden())
                    {
                        fragMana.beginTransaction()
                                .hide(artistsFragment)
                                .hide(albumsFragment)
                                .show(playlistsFragment)
                                .hide(songsFragment)
                                .commit();

                    }
                    return true;
                }
                else { return false; }
            }
        });


        // Request permission and crawl for audio files
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) // SDK 33
        {
            checkPerm(Manifest.permission.READ_MEDIA_AUDIO, READ_MEDIA_AUDIO_CODE);
        }
        else
        {
            checkPerm(Manifest.permission.READ_EXTERNAL_STORAGE, READ_EXTERNAL_STORAGE_CODE);
        }


        audioPlayerServiceIntent = new Intent(this, AudioPlayerService.class);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_top, menu);
        MenuItem searchBar = menu.findItem(R.id.app_bar_search);
        SearchView searchView = (SearchView) searchBar.getActionView();
        searchView.setMaxWidth(Integer.MAX_VALUE);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (!songsFragment.isHidden()) {
                    songsFragment.adapter.getFilter().filter(newText);
                }
                return false;
            }
        });
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.app_bar_settings){
            startActivity(new Intent(this, SettingsActivity.class));
        }

        return super.onOptionsItemSelected(item);
    }


    public void crawlAudioFiles()
    {
        // Deploy FileDiscoveryWorker crawler
        Intent serviceIntent = new Intent(this, FileDiscoveryService.class);
        ContextCompat.startForegroundService(this, serviceIntent);
    }

    // Permission manager
    public void checkPerm(String perm, int reqCode)
    {
        if (reqCode == READ_EXTERNAL_STORAGE_CODE) {
            if (ContextCompat.checkSelfPermission(MainActivity.this, perm) == PackageManager.PERMISSION_DENIED) {
                // Ask for perm
                ActivityCompat.requestPermissions(MainActivity.this, new String[]{perm}, reqCode);
            } else {
                // We already have perms
                // Start crawling
                crawlAudioFiles();
            }
        }
        else if (reqCode == READ_MEDIA_AUDIO_CODE)
        {
            if (ContextCompat.checkSelfPermission(MainActivity.this, perm) == PackageManager.PERMISSION_DENIED) {
                // Ask for perm
                ActivityCompat.requestPermissions(MainActivity.this, new String[]{perm}, reqCode);
            } else {
                // We already have perms
                // Start crawling
                crawlAudioFiles();
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int reqCode, @NonNull String[] perms, @NonNull int[] grantResults) {

        super.onRequestPermissionsResult(reqCode, perms, grantResults);

        if (reqCode == READ_EXTERNAL_STORAGE_CODE)
        {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
            {
                // We got perm granted
                // Start crawling
                crawlAudioFiles();
            }
            else
            {
                // We did not get perm granted
                Log.e("PERMISSIONS", "Permission READ_EXTERNAL_STORAGE was denied!");
            }
        }
        else if (reqCode == READ_MEDIA_AUDIO_CODE)
        {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
            {
                // We got perm granted
                // Start crawling
                crawlAudioFiles();
            }
            else{
                // We did not get perm granted
                Log.e("PERMISSIONS", "Permission READ_MEDIA_AUDIO was denied!");
            }
        }
    }

    public static MainActivity getInstance() {
        return weakReference.get();
    }

    public void playAudio(Song song) {
        if (mBottomSheetBehavior.getState() != BottomSheetBehavior.STATE_EXPANDED) mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);

        audioPlayerServiceIntent.putExtra("file", song.getUri().toString());
        audioPlayerServiceIntent.setAction("com.wishify.action.FORCE_PLAY"); // Lorsqu'on doit potentiellement stop une chanson en cours et la remplacer
        startService(audioPlayerServiceIntent);
    }

    public void stopAudio() {
        stopService(audioPlayerServiceIntent);
    }




}