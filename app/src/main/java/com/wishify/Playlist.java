package com.wishify;

import android.content.Context;
import android.util.Log;

import java.io.BufferedWriter;
import java.io.OutputStreamWriter;
import java.util.List;
import java.util.Objects;

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

    public int addSong(Song song)
    {
        // Quick check (could replace with hashmap)
        for (Song inSong : songs)
        {
            if (Objects.equals(inSong.getTitle(), song.getTitle())) // Is duplicate
            {
                return 0;
            }
        }

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
