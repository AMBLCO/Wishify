package com.wishify;

import android.util.Log;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Globals {
    private static List<Song> songsList = new ArrayList<>();
    public static List<Song> queue = new ArrayList<>();
    public static int queuePos;
    public static boolean repeat;
    public static boolean shuffle;
    public static ArrayList<Integer> shuffleList = new ArrayList<>();
    public static int shuffleListPos;
    private static List<Song> filteredList = new ArrayList<>();

    private static List<Playlist> playlists = new ArrayList<>();

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

    public static void addPlaylist(Playlist playlist)
    {
        playlists.add(playlist);
    }

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
}
