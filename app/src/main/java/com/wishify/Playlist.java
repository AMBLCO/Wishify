package com.wishify;

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

    public void addSong(Song song){ songs.add(song); }

    public String getName() {
        return this.name;
    }
}
