package com.wishify;

import android.content.Context;
import android.net.Uri;
import android.util.Log;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

public class Globals {
    private static Context appContext;

    private static List<Song> songsList = new ArrayList<>();

    // TODO TEST THIS
    private static HashMap<Uri, Song> songsMap = new HashMap<>();

    public static List<Song> queue = new ArrayList<>();
    public static int queuePos;
    public static int repeat;
    public static int repeatStartPos;
    public static boolean shuffle;
    public static ArrayList<Integer> shuffleList = new ArrayList<>();
    public static int shuffleListPos;
    private static List<Song> filteredList = new ArrayList<>();

    private static final HashMap<String, Playlist> playlists = new HashMap<>();

    public static Context getAppContext() {
        return appContext;
    }

    public static void setAppContext(Context appContext) {
        Globals.appContext = appContext;
    }

    public static void addSongsToQueue(List<Song> songs) {
        queue.clear();
        queue.addAll(songs);
    }

    public static List<Song> getSongsList() {
        return songsList;
    }

    // DO NOT USE UNLESS IN FileDiscoveryService
    public static void setSongsList(List<Song> songs) {
        songsList = songs;
        SongsFragment.adapter.setSongList(songsList);
        SongsFragment.adapter.notifyDataSetChanged(); // OUCH
        Log.d("GLOBALS", "Size of songsList is now " + songsList.size());
    }

    public static List<Song> getFilteredList() {
        return filteredList;
    }

    public static void addToFilteredList(Song song) {
        filteredList.add(song);
    }

    public static void clearFilteredList()
    {
        filteredList.clear();
    }

    public static int addPlaylist(Playlist playlist)
    {
        try {
            // Create file
            writePlaylistFile(playlist);
            playlists.put(playlist.getName(), playlist);
            PlaylistsFragment.adapter.notifyDataSetChanged();
            return 1;
        }
        catch(Exception e)
        {
            Log.e("PLAYLIST_CREATE", e.toString());
            Log.e("PLAYLIST_CREATE", "Failed to create playlist!");
            return 0; // FAILURE
        }

    }

    private static void writePlaylistFile(Playlist playlist) throws IOException {
        try(BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(appContext.openFileOutput(playlist.getName() + ".txt", Context.MODE_APPEND))))
        {
            // File has been created
            for (Song song : playlist.getSongs())
            {
                writer.write(song.getUri().toString());
                writer.newLine();
            }
            Log.d("PLAYLISTS_CREATE", "Created playlist");
        }
    }

    public static Playlist getPlaylist(String nameOfPlaylist) { return playlists.get(nameOfPlaylist); }

    public static void toggleShuffle() {
        if (shuffle)
        {
            shuffle = false;
        }
        else
        {
            shuffle = true;
            resetAndGenerateShuffleList();
        }
    }

    public static void resetAndGenerateShuffleList() {
        shuffleList.clear();

        int min = queuePos + 1;
        int max = queue.size() - 1;
        for (int i = min; i <= max; i++) {
            shuffleList.add(i);
        }
        Collections.shuffle(shuffleList);

        shuffleListPos = -1;
    }

    public static HashMap<Uri, Song> getSongsMap() {
        return songsMap;
    }

    // CAREFUL
    public static void setSongsMap(HashMap<Uri, Song> songsMap) {
        Globals.songsMap = songsMap;
    }

    public static void addSongToMap(Song song)
    {
        songsMap.put(song.getUri(), song);
    }


    public static HashMap<String, Playlist> getPlaylists() {
        return playlists;
    }

    public static boolean deletePlaylist(String playlistName)
    {
        File dir = appContext.getFilesDir();
        File file = new File(dir, playlistName + ".txt");
        if(file.delete())
        {
            // Cleanup
            playlists.remove(playlistName);
            return true;
        }
        else{
            Log.e("PLAYLIST_DELETE", "Could not delete playlist!");
            return false;
        }
    }

}
