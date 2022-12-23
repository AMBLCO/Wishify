package com.wishify;

import android.content.Context;
import android.util.Log;

import java.io.BufferedWriter;
import java.io.OutputStreamWriter;
import java.util.List;

public class Playlist {
    private final String name;
    private final List<Song> songs;

    public Playlist(String name, List<Song> songs)
    {
        this.name = name;
        this.songs = songs;
    }

    public List<Song> getSongs()
    {
        return this.songs;
    }

    public int addSong(Song song){

        try {
            // Create file
            writeSongToPlaylistFile(song);
            songs.add(song);
            PlaylistsFragment.adapter.notifyDataSetChanged();
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
}
