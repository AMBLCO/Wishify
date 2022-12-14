package com.wishify;

import static com.wishify.AudioPlayer.goNext;
import static com.wishify.AudioPlayer.goPrevious;
import static com.wishify.AudioPlayer.pauseAudio;
import static com.wishify.AudioPlayer.resumeAudio;
import static com.wishify.AudioPlayer.seekTo;
import static com.wishify.AudioPlayerService.getMediaPlayerStatus;
import static com.wishify.AudioPlayerService.mediaPlayer;
import static com.wishify.Globals.queue;
import static com.wishify.Globals.queuePos;
import static com.wishify.Globals.toggleShuffle;

import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.appcompat.content.res.AppCompatResources;

public class MusicControl {

    //PopupWindow display method

    private ImageButton previousTrack;
    private static ImageButton playpause;
    private ImageButton nextTrack;
    private static ImageButton repeat;
    private static ImageButton shuffle;
    private static ImageView songImage;
    private static TextView songName;
    private static TextView songArtistAndAlbum;
    private static SeekBar seekbar;
    private static TextView seekbarHint;

    private static PopupWindow popupWindow;
    private static Thread thread;

    /**
     * Called when we want the popup to be showed
     * @param view
     */
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
        popupWindow = new PopupWindow(popupView, width, height, focusable);

        //Set the location of the window on the screen
        popupWindow.showAtLocation(view, Gravity.CENTER, 0, 0);

        previousTrack = popupView.findViewById(R.id.controlPreviousTrackButton);
        playpause = popupView.findViewById(R.id.controlPlayPauseButton);
        nextTrack = popupView.findViewById(R.id.controlNextTrackButton);
        repeat = popupView.findViewById(R.id.controlRepeatButton);
        shuffle = popupView.findViewById(R.id.controlShuffleButton);
        songImage = popupView.findViewById(R.id.controlSongImage);
        songName = popupView.findViewById(R.id.controlSongName);
        songArtistAndAlbum = popupView.findViewById(R.id.controlSongArtistAndAlbum);
        seekbar = popupView.findViewById(R.id.controlSeekbar);
        seekbarHint = popupView.findViewById(R.id.controlSeekbarHint);

        updateMusicControl();
        runSeekbarUpdate();

        seekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if ((progress % 60000) / 1000 >= 10) seekbarHint.setText(progress / 60000 + ":" + (progress % 60000) / 1000);
                else seekbarHint.setText(progress / 60000 + ":0" + (progress % 60000) / 1000);

                double percent = progress / (double) seekBar.getMax();
                int offset = seekBar.getThumbOffset();
                seekbarHint.setX(offset + seekBar.getX() + (int) Math.round(percent * (seekBar.getWidth() - 2 * offset)) - Math.round(percent * offset) - Math.round(percent * seekbarHint.getWidth() / 2));

                if (progress > 0 && getMediaPlayerStatus() != 2 && getMediaPlayerStatus() != 3) {
                    seekBar.setProgress(0);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                seekbarHint.setVisibility(View.VISIBLE);
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                if (getMediaPlayerStatus() == 2) {
                    seekTo(seekBar.getProgress());
                }
                seekbarHint.setVisibility(View.INVISIBLE);
            }
        });

        previousTrack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final View view = v;
                v.setEnabled(false);

                goPrevious();

                v.postDelayed(new Runnable(){
                    public void run(){
                        view.setEnabled(true);
                    }
                }, 1000);
            }
        });

        playpause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final View view = v;
                v.setEnabled(false);

                if (getMediaPlayerStatus() != 3) {
                    pauseAudio();
                    playpause.setImageDrawable(AppCompatResources.getDrawable(view.getContext(), R.drawable.ic_play));
                }

                if (getMediaPlayerStatus() == 3) {
                    resumeAudio();
                    playpause.setImageDrawable(AppCompatResources.getDrawable(view.getContext(), R.drawable.ic_pause));
                }

                v.postDelayed(new Runnable(){
                    public void run(){
                        view.setEnabled(true);
                    }
                }, 1000);
            }
        });

        nextTrack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final View view = v;
                v.setEnabled(false);

                goNext();

                v.postDelayed(new Runnable(){
                    public void run(){
                        view.setEnabled(true);
                    }
                }, 1000);
            }
        });

        repeat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final View view = v;
                v.setEnabled(false);

                if (Globals.repeat == 0)
                {
                    Globals.repeat = 1;
                    Globals.repeatStartPos = queuePos;
                    repeat.setImageDrawable(AppCompatResources.getDrawable(repeat.getContext(), R.drawable.ic_repeat_on));
                }
                else if (Globals.repeat == 1)
                {
                    Globals.repeat = 2;
                    repeat.setImageDrawable(AppCompatResources.getDrawable(repeat.getContext(), R.drawable.ic_repeat_one_on));
                }
                else if (Globals.repeat == 2)
                {
                    Globals.repeat = 0;
                    repeat.setImageDrawable(AppCompatResources.getDrawable(repeat.getContext(), R.drawable.ic_repeat_off));
                }

                v.postDelayed(new Runnable(){
                    public void run(){
                        view.setEnabled(true);
                    }
                }, 1000);
            }
        });

        shuffle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final View view = v;
                v.setEnabled(false);

                toggleShuffle();
                if (Globals.shuffle) shuffle.setImageDrawable(AppCompatResources.getDrawable(view.getContext(), R.drawable.ic_shuffle_on));
                else shuffle.setImageDrawable(AppCompatResources.getDrawable(view.getContext(), R.drawable.ic_shuffle_off));

                v.postDelayed(new Runnable(){
                    public void run(){
                        view.setEnabled(true);
                    }
                }, 1000);
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

    /**
     * Called when the popup needs to be updated
     */
    public static void updateMusicControl() {
        songImage.setImageBitmap(queue.get(queuePos).getBitmap());
        songName.setText(queue.get(queuePos).getTitle());
        songArtistAndAlbum.setText(queue.get(queuePos).getArtist() + " - " + queue.get(queuePos).getAlbum());

        if (getMediaPlayerStatus() == 3) { playpause.setImageDrawable(AppCompatResources.getDrawable(playpause.getContext(), R.drawable.ic_play)); }
        if (getMediaPlayerStatus() != 3) { playpause.setImageDrawable(AppCompatResources.getDrawable(playpause.getContext(), R.drawable.ic_pause)); }

        if (Globals.repeat == 0) repeat.setImageDrawable(AppCompatResources.getDrawable(repeat.getContext(), R.drawable.ic_repeat_off));
        else if (Globals.repeat == 1) repeat.setImageDrawable(AppCompatResources.getDrawable(repeat.getContext(), R.drawable.ic_repeat_on));
        else if (Globals.repeat == 2) repeat.setImageDrawable(AppCompatResources.getDrawable(repeat.getContext(), R.drawable.ic_repeat_one_on));
        if (Globals.shuffle) shuffle.setImageDrawable(AppCompatResources.getDrawable(shuffle.getContext(), R.drawable.ic_shuffle_on));
        else shuffle.setImageDrawable(AppCompatResources.getDrawable(shuffle.getContext(), R.drawable.ic_shuffle_off));

        seekbar.setMax(queue.get(queuePos).getDuration());
        seekbar.setProgress(0);
        seekbarHint.setVisibility(View.INVISIBLE);
    }

    /**
     * Called when the seekbar will be updated
     */
    public static void runSeekbarUpdate() {
        if (popupWindow != null)
        {
            thread = new Thread(new Runnable() {
                @Override
                public void run() {
                    int currentPosition = mediaPlayer.getCurrentPosition();
                    int total = mediaPlayer.getDuration();

                    while (mediaPlayer != null && currentPosition < total) {
                        if (getMediaPlayerStatus() == 2)
                        {
                            try {
                                Thread.sleep(100); //Smooth movement
                                currentPosition = mediaPlayer.getCurrentPosition();
                            } catch (Exception e) {
                                return;
                            }
                            seekbar.setProgress(currentPosition);
                        }
                    }
                }
            });
            thread.start();
        }
    }

    /**
     * Called when the seekbar shouldn't get updated
     */
    public static void stopSeekbarUpdate() {
        if (thread != null)
        {
            thread.interrupt();
            thread = null;
        }
    }

    /**
     * Close the popup
     */
    public void closePopup() { if (popupWindow.isShowing()) popupWindow.dismiss(); }
}
