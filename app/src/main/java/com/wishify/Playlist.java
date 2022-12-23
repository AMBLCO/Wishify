package com.wishify;

import android.content.Context;
import android.net.Uri;
import android.util.Log;

import java.io.BufferedWriter;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

public class Playlist {
    private final String name;
    private final HashMap<Uri, Song> songs;

    public Playlist(String name, HashMap<Uri, Song> songs)
    {
        this.name = name;
        this.songs = songs;
    }

    public List<Song> getSongs()
    {
        return new ArrayList<Song>(this.songs.values());
    }

    public int addSong(Song song)
    {



        try {
            // Create file
            writeSongToPlaylistFile(song);
            songs.put(song.getUri(), song);
            //PlaylistsFragment.adapter.notifyDataSetChanged();
            return 1;
        }
        catch(Exception e)
        {
            Log.e("PLAYLIST_ADD", e.toString());
            return 0; // FAILURE
        }
    }

    private void writeSongToPlaylistFile(Song song) {

        try(BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(Globals.getAppContext().openFileOutput(name + ".txt", Context.MODE_APPEND))))
        {
            writer.write(song.getUri().toString());
            writer.newLine();
            Log.d("PLAYLIST_ADD", "Added song to playlist");
        }
        catch(Exception e)
        {
            Log.e("PLAYLIST_ADD", "Could not add song to playlist!");
        }
    }

    public String getName() {
        return this.name;
    }

    public int getSize() {
        return songs.size();
    }
}
