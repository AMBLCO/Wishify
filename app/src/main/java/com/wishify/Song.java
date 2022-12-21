package com.wishify;


import android.media.Image;
import android.net.Uri;

public class Song {
    private String title;
    private String artist;
    private String album;
    private Image cover;
    private int duration;
    private Uri uri;

    public Song(Uri uri, String title, String artist, String album, int duration)
    {
        this.title = title;
        this.artist = artist;
        this.album = album;
        this.duration = duration;
        this.uri = uri;
    }

    public String getTitle() {
        return title;
    }

    public String getArtist() {
        return artist;
    }

    public String getAlbum() {
        return album;
    }

    public Image getCover() {
        return cover;
    }

    public Uri getUri() { return this.uri; }

}
