package com.wishify;

import android.util.Log;

import java.util.ArrayList;
import java.util.List;

public class Globals
{
    private static List<Song> songsList = new ArrayList<Song>();

    public static void addToSongsList(List<Song> songs)
    {
        songsList.addAll(songs);
        SongsFragment.adapter.notifyDataSetChanged(); // OUCH
        Log.d("GLOBALS", "Size of songsList is now " + songsList.size());
    }

    public static List<Song> getSongsList()
    {
        return songsList;
    }

}
