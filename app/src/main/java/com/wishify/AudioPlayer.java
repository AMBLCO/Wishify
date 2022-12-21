package com.wishify;

import static android.app.PendingIntent.getActivity;

import static java.lang.String.valueOf;

import android.widget.Toast;

import com.google.android.material.bottomsheet.BottomSheetBehavior;

public class AudioPlayer {

    private static MainActivity mainActivity = MainActivity.getInstance();

    public static void playAudio(Song song) {
        mainActivity.playAudio(song);
    }

    public static void stopAudio() {
        mainActivity.stopAudio();
    }
}
