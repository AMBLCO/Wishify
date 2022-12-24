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

    /**
     *
     * @param uri L'adresse Uri de la chanson dans l'appareil
     * @param title Le titre de la chanson
     * @param artist Le nom de l'artiste
     * @param album Le nom de l'album dans lequel la chanson se trouve
     * @param duration La durée de la chanson en ms (millisecondes)
     * @param bitmap Le bitmap de l'image associée à la chanson
     */
    public Song(Uri uri, String title, String artist, String album, int duration, Bitmap bitmap)
    {
        this.title = title;
        this.artist = artist;
        this.album = album;
        this.duration = duration;
        this.uri = uri;
        this.bitmap = bitmap;
    }

    /**
     *
     * @return Le titre de la chanson
     */
    public String getTitle() {
        return title;
    }

    /**
     *
     * @return Le nom de l'artiste
     */
    public String getArtist() {
        return artist;
    }

    /**
     *
     * @return Le nom de l'album
     */
    public String getAlbum() {
        return album;
    }

    /**
     *
     * @return Le Bitmap de l'image de la chanson
     */
    public Bitmap getBitmap() { return this.bitmap; }

    /**
     *
     * @return Le Uri de la chanson
     */
    public Uri getUri() { return this.uri; }

    /**
     *
     * @return La durée de la chanson en ms (millisecondes)
     */
    public int getDuration() {return this.duration; }
}
