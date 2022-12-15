package com.wishify;


import android.media.Image;

public class Song {
    private String title;
    private String artist;
    private String album;
    private Image cover;
    private int timeLength;
    private long id;

    public Song(String title, String artist, String album, long id)
    {
        this.title = title;
        this.artist = artist;
        this.album = album;
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public String getAlbum() {
        return album;
    }

    public void setAlbum(String album) {
        this.album = album;
    }

    public Image getCover() {
        return cover;
    }

    public void setCover(Image cover) {
        this.cover = cover;
    }

    public long getId() { return id; }

    public void setId(long id) { this.id = id; }
}
