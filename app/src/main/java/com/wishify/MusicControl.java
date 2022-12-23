package com.wishify;

import static com.wishify.AudioPlayer.goNext;
import static com.wishify.AudioPlayer.goPrevious;
import static com.wishify.AudioPlayer.pauseAudio;
import static com.wishify.AudioPlayer.resumeAudio;
import static com.wishify.AudioPlayerService.getMediaPlayerStatus;
import static com.wishify.Globals.queue;
import static com.wishify.Globals.queuePos;

import android.graphics.drawable.Drawable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.content.res.AppCompatResources;

public class MusicControl {

    //PopupWindow display method

    private ImageButton previousTrack;
    private ImageButton playpause;
    private ImageButton nextTrack;
    private static ImageView songImage;
    private static TextView songName;
    private static TextView songArtistAndAlbum;

    public void showPopupWindow(final View view) {
        //Create a View object yourself through inflater
        LayoutInflater inflater = (LayoutInflater) view.getContext().getSystemService(view.getContext().LAYOUT_INFLATER_SERVICE);
        View popupView = inflater.inflate(R.layout.music_control, null);

        //Specify the length and width through constants
        int width = LinearLayout.LayoutParams.MATCH_PARENT;
        int height = LinearLayout.LayoutParams.MATCH_PARENT;

        //Make Inactive Items Outside Of PopupWindow
        boolean focusable = true;

        //Create a window with our parameters
        final PopupWindow popupWindow = new PopupWindow(popupView, width, height, focusable);

        //Set the location of the window on the screen
        popupWindow.showAtLocation(view, Gravity.CENTER, 0, 0);

        previousTrack = popupView.findViewById(R.id.controlPreviousTrackButton);
        playpause = popupView.findViewById(R.id.controlPlayPauseButton);
        nextTrack = popupView.findViewById(R.id.controlNextTrackButton);
        songImage = popupView.findViewById(R.id.controlSongImage);
        songName = popupView.findViewById(R.id.controlSongName);
        songArtistAndAlbum = popupView.findViewById(R.id.controlSongArtistAndAlbum);

        updateMusicControlSong();

        previousTrack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goPrevious();
            }
        });

        playpause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (getMediaPlayerStatus() != 3) {
                    pauseAudio();
                    playpause.setImageDrawable(AppCompatResources.getDrawable(view.getContext(), R.drawable.ic_play));
                }

                if (getMediaPlayerStatus() == 3) {
                    resumeAudio();
                    playpause.setImageDrawable(AppCompatResources.getDrawable(view.getContext(), R.drawable.ic_pause));
                }
            }
        });

        nextTrack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goNext();
            }
        });

        //Handler for clicking on the inactive zone of the window
        popupView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                //Close the window when clicked
                popupWindow.dismiss();
                return true;
            }
        });
    }

    public static void updateMusicControlSong() {
        songImage.setImageBitmap(queue.get(queuePos).getBitmap());
        songName.setText(queue.get(queuePos).getTitle());
        songArtistAndAlbum.setText(queue.get(queuePos).getArtist() + " - " + queue.get(queuePos).getAlbum());
    }
}
