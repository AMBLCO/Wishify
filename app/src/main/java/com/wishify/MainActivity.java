package com.wishify;

import static com.wishify.AudioPlayerService.getMediaPlayerStatus;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.appcompat.widget.SearchView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentManager;

import android.Manifest;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

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

    private final MusicControl popUpClass = new MusicControl();

    private FragmentManager fragMana;

    private ImageButton playpause;
    private ImageView musicImage;
    private TextView songName;
    private TextView songArtistAndAlbum;

    private AudioPlayerService player;
    boolean serviceBound = false;

    // Get instances of fragments
    private ArtistsFragment artistsFragment;
    private AlbumsFragment albumsFragment;
    private SongsFragment songsFragment;
    private PlaylistsFragment playlistsFragment;
    private PlaylistFragment playlistFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Globals.setAppContext(getApplicationContext());

        // Get layout elements
        BottomNavigationView navigView = findViewById(R.id.bottom_navigation);
        fragMana = getSupportFragmentManager();

        mBottomSheet = findViewById(R.id.bottom_sheet);
        mBottomSheet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                popUpClass.showPopupWindow(view);
            }
        });

        mBottomSheetBehavior = BottomSheetBehavior.from(mBottomSheet);
        mBottomSheetBehavior.setDraggable(false);

        weakReference = new WeakReference<>(MainActivity.this);

        playpause = findViewById(R.id.playerPlayPauseButton);
        playpause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (getMediaPlayerStatus() == 2) pauseAudio();

                if (getMediaPlayerStatus() == 3) resumeAudio();
            }
        });
        musicImage = findViewById(R.id.playerSongImage);
        songName = findViewById(R.id.playerSongName);
        songArtistAndAlbum = findViewById(R.id.playerSongArtistAndAlbum);

        songName.setSelected(true);
        songArtistAndAlbum.setSelected(true);

        artistsFragment = new ArtistsFragment();
        albumsFragment = new AlbumsFragment();
        songsFragment = new SongsFragment();
        playlistsFragment = new PlaylistsFragment();
        playlistFragment = new PlaylistFragment();

        fragMana.beginTransaction()
                .add(R.id.mainFragmentContainerView, artistsFragment, "artists")
                .add(R.id.mainFragmentContainerView, albumsFragment, "albums")
                .add(R.id.mainFragmentContainerView, songsFragment, "songs")
                .add(R.id.mainFragmentContainerView, playlistsFragment, "playlists")
                .add(R.id.mainFragmentContainerView, playlistFragment, "playlist")
                .hide(artistsFragment)
                .hide(albumsFragment)
                .hide(playlistsFragment)
                .hide(playlistFragment)
                .show(songsFragment)
                .commit();

        // On commence sur la page des chansons
        navigView.setSelectedItemId(R.id.songs);

        navigView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener(){

            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                if (item.getItemId() == R.id.artists)
                {
                    // On doit changer de Fragment si et seulement si le fragment visible n'est pas celui d??sir??
                    if (artistsFragment.isHidden())
                    {
                        fragMana.beginTransaction()
                                .show(artistsFragment)
                                .hide(albumsFragment)
                                .hide(playlistsFragment)
                                .hide(songsFragment)
                                .hide(playlistFragment)
                                .commit();

                    }
                    return true;
                }
                else if (item.getItemId() == R.id.albums)
                {
                    // On doit changer de Fragment si et seulement si le fragment visible n'est pas celui d??sir??
                    if (albumsFragment.isHidden())
                    {
                        fragMana.beginTransaction()
                                .hide(artistsFragment)
                                .show(albumsFragment)
                                .hide(playlistsFragment)
                                .hide(songsFragment)
                                .hide(playlistFragment)
                                .commit();
                    }
                    return true;
                }
                else if (item.getItemId() == R.id.songs)
                {
                    // On doit changer de Fragment si et seulement si le fragment visible n'est pas celui d??sir??
                    if (songsFragment.isHidden())
                    {
                        fragMana.beginTransaction()
                                .hide(artistsFragment)
                                .hide(albumsFragment)
                                .hide(playlistsFragment)
                                .show(songsFragment)
                                .hide(playlistFragment)
                                .commit();
                    }
                    return true;
                }
                else if (item.getItemId() == R.id.playlists)
                {
                    playlistsFragment.refresh();
                    // On doit changer de Fragment si et seulement si le fragment visible n'est pas celui d??sir??
                    if (playlistsFragment.isHidden())
                    {
                        fragMana.beginTransaction()
                                .hide(artistsFragment)
                                .hide(albumsFragment)
                                .show(playlistsFragment)
                                .hide(songsFragment)
                                .hide(playlistFragment)
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
                filter(newText);
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


    /**
     * Deploys async file discovery
     */
    public void crawlAudioFiles()
    {
        // Deploy async crawler
        Intent serviceIntent = new Intent(this, FileDiscoveryService.class);
        ContextCompat.startForegroundService(this, serviceIntent);
    }

    /**
     * Checks for needed permissions
     * @param perm wanted permission
     * @param reqCode arbitrary code
     */
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

    //Binding this Client to the AudioPlayer Service
    private ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            // We've bound to LocalService, cast the IBinder and get LocalService instance
            AudioPlayerService.LocalBinder binder = (AudioPlayerService.LocalBinder) service;
            player = binder.getService();
            serviceBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            serviceBound = false;
        }
    };

    /**
     * Plays music
     * @param song to be played
     */
    public void playAudio(Song song) {
        audioPlayerServiceIntent.setAction("com.wishify.action.PLAY");
        startService(audioPlayerServiceIntent);

        //Check if service is active
        if (!serviceBound) {
            bindService(audioPlayerServiceIntent, serviceConnection, Context.BIND_AUTO_CREATE);
        }
    }

    /**
     * Manages sound menu
     * @param song that is being played
     */
    public void showAndUpdateBottomSheet(Song song) {
        if (mBottomSheetBehavior.getState() != BottomSheetBehavior.STATE_EXPANDED) mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
        playpause.setImageDrawable(AppCompatResources.getDrawable(getApplicationContext(), R.drawable.ic_pause));

        musicImage.setImageBitmap(song.getBitmap());
        songName.setText(song.getTitle());
        songArtistAndAlbum.setText(song.getArtist() + " - " + song.getAlbum());
    }

    public void pauseAudio() {
        playpause.setImageDrawable(AppCompatResources.getDrawable(getApplicationContext(), R.drawable.ic_play));
        audioPlayerServiceIntent.setAction("com.wishify.action.PAUSE");
        startService(audioPlayerServiceIntent);
    }

    public void resumeAudio() {
        playpause.setImageDrawable(AppCompatResources.getDrawable(getApplicationContext(), R.drawable.ic_pause));
        audioPlayerServiceIntent.setAction("com.wishify.action.RESUME");
        startService(audioPlayerServiceIntent);
    }

    public void stopAudio() {
        mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
        popUpClass.closePopup();
    }

    /**
     * Next song
     */
    public void goNext() {
        audioPlayerServiceIntent.setAction("com.wishify.action.NEXT");
        startService(audioPlayerServiceIntent);
    }

    /**
     * Previous song
     */
    public void goPrevious() {
        audioPlayerServiceIntent.setAction("com.wishify.action.PREVIOUS");
        startService(audioPlayerServiceIntent);
    }

    /**
     * Go forward or backwards in a song
     * @param pos wanted time
     */
    public void seekTo(int pos) {
        audioPlayerServiceIntent.putExtra("pos", pos);
        audioPlayerServiceIntent.setAction("com.wishify.action.SEEK");
        startService(audioPlayerServiceIntent);
    }

    /**
     * Used for search feature
     * @param text
     */
    public void filter(String text)
    {
        Globals.clearFilteredList();
        if(text.isEmpty())
        {
            songsFragment.adapter.filterList(Globals.getSongsList());
        }
        else
        {
            for(Song song : Globals.getSongsList())
            {
                if(song.getTitle().toLowerCase().contains(text.toLowerCase()))
                {
                    Globals.addToFilteredList(song);
                }
            }

            if(Globals.getFilteredList().isEmpty())
            {
                Toast.makeText(this, "No song found", Toast.LENGTH_SHORT).show();
            }
            else
            {
                songsFragment.adapter.filterList(Globals.getFilteredList());
            }
        }
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        savedInstanceState.putBoolean("ServiceState", serviceBound);
        super.onSaveInstanceState(savedInstanceState);
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        serviceBound = savedInstanceState.getBoolean("ServiceState");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (serviceBound) {
            unbindService(serviceConnection);
            //service is active
            player.stopSelf();
        }
    }

    /**
     * Displays wanted playlist when clicking a playlist
     * @param wantedPlaylist playlist to be displayed
     */
    public void handlePlaylistFragment(String wantedPlaylist)
    {
        playlistFragment.setPlaylist(Globals.getPlaylist(wantedPlaylist));
        // Must hide playlists Fragment
        fragMana.beginTransaction()
                .hide(playlistsFragment)
                .show(playlistFragment)
                .commit();
        playlistFragment.refresh();
    }

}