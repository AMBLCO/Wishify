package com.wishify;

import android.util.Log;

import java.util.ArrayList;
import java.util.List;

public class Globals
{
    private static List<Song> songsList = new ArrayList<>();
    public static List<Song> queue = new ArrayList<>();

    private static List<Playlist> playlists = new ArrayList<>();

    public static void addSongsToQueue(List<Song> songs) {
        queue.clear();
        queue.addAll(songs);
    }

    public static List<Song> getSongsList()
    {
        return songsList;
    }

    // DO NOT USE UNLESS IN FileDiscoveryService
    public static void setSongsList(List<Song> songs)
    {
        songsList = songs;
        SongsFragment.adapter.setSongList(songsList);
        SongsFragment.adapter.notifyDataSetChanged(); // OUCH
        Log.d("GLOBALS", "Size of songsList is now " + songsList.size());
    }

    public static void addPlaylist(Playlist playlist)
    {
        playlists.add(playlist);
    }

}
