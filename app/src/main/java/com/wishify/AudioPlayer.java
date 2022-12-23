package com.wishify;

import static android.app.PendingIntent.getActivity;

import static java.lang.String.valueOf;

public class AudioPlayer {

    private static MainActivity mainActivity = MainActivity.getInstance();

    public static void playAudio(Song song) { mainActivity.playAudio(song); }

    public static void changeBottomSheet(Song song) { mainActivity.changeBottomSheetSong(song); }

    public static void pauseAudio() { mainActivity.pauseAudio(); }

    public static void resumeAudio() { mainActivity.resumeAudio(); }

    public static void stopAudio() { mainActivity.stopAudio(); }
}
