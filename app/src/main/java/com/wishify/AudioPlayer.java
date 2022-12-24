package com.wishify;

import static android.app.PendingIntent.getActivity;

import static java.lang.String.valueOf;

public class AudioPlayer {

    private static MainActivity mainActivity = MainActivity.getInstance();

    /**
     * Static method to play audio from MainActivity
     * @param song
     */
    public static void playAudio(Song song) { mainActivity.playAudio(song); }

    /**
     * Static method to change the bottom sheet from MainActivity
     * @param song
     */
    public static void changeBottomSheet(Song song) { mainActivity.showAndUpdateBottomSheet(song); }

    /**
     * Static method to pause audio from MainActivity
     */
    public static void pauseAudio() { mainActivity.pauseAudio(); }

    /**
     * Static method to resume audio from MainActivity
     */
    public static void resumeAudio() { mainActivity.resumeAudio(); }

    /**
     * Static method to stop audio from MainActivity
     */
    public static void stopAudio() { mainActivity.stopAudio(); }

    /**
     * Static method to go to the next track from MainActivity
     */
    public static void goNext() { mainActivity.goNext(); }

    /**
     * Static method to play to the previous track from MainActivity
     */
    public static void goPrevious() { mainActivity.goPrevious(); }

    /**
     * Static method to seek to pos from MainActivity
     * @param pos
     */
    public static void seekTo(int pos) { mainActivity.seekTo(pos);}
}
