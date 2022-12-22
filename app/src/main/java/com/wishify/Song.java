package com.wishify;


import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.media.MediaMetadataRetriever;
import android.net.Uri;

public class Song {
    private final String title;
    private final String artist;
    private final String album;
    private final Bitmap bitmap;
    private final int duration; // In seconds
    private final Uri uri;

    public Song(Uri uri, String title, String artist, String album, int duration, Bitmap bitmap)
    {
        this.title = title;
        this.artist = artist;
        this.album = album;
        this.duration = duration;
        this.uri = uri;
        this.bitmap = bitmap;
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

    public Bitmap getBitmap() { return this.bitmap; }

    public Uri getUri() { return this.uri; }

    public int getDuration() {return this.duration; }
}
