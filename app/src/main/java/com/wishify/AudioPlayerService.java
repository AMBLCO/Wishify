package com.wishify;

import static com.wishify.AudioPlayer.changeBottomSheet;
import static com.wishify.AudioPlayer.stopAudio;
import static com.wishify.Globals.queue;
import static com.wishify.Globals.queuePos;
import static com.wishify.MusicControl.updateMusicControlSong;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Binder;
import android.os.IBinder;
import android.os.PowerManager;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;

import androidx.annotation.Nullable;

import java.io.IOException;
import java.net.URI;

public class AudioPlayerService extends Service implements MediaPlayer.OnPreparedListener, MediaPlayer.OnErrorListener {
    private static MediaPlayer mediaPlayer = new MediaPlayer(); // We want only one MediaPlayer running at a given time.
    private static int mediaPlayerStatus = 0; // Start in STATE_IDLE

    private int resumePosition;

    // Arbitrary constants
    private static final String ACTION_PLAY = "com.wishify.action.PLAY"; // When unpausing
    private static final String ACTION_FORCE_PLAY = "com.wishify.action.FORCE_PLAY"; // When replacing currently running song with another
    private static final String ACTION_PAUSE = "com.wishify.action.PAUSE";
    private static final String ACTION_RESUME = "com.wishify.action.RESUME";
    private static final String ACTION_PREVIOUS = "com.wishify.action.PREVIOUS";
    private static final String ACTION_NEXT = "com.wishify.action.NEXT";

    private static final int STATE_IDLE = 0; // When constructor is done
    private static final int STATE_PREPARED = 1;
    private static final int STATE_STARTED = 2;
    private static final int STATE_PAUSED = 3;
    private static final int STATE_PLAYBACK_COMPLETED = 4;
    private static final int STATE_STOPPED = 5;
    private static final int STATE_ERROR = 6;
    private static final int STATE_END = 7; // When released
    private static final int STATE_INITIALIZED = 8;

    // This is a callback
    private final MediaPlayer.OnCompletionListener mediaPlayerOnCompletionListener = new MediaPlayer.OnCompletionListener()
    {
        @Override
        public void onCompletion(MediaPlayer mp) {
            // Actions to be done when song is finished playing
            Log.d("AUDIO_PLAYER", "Done playing song");
            mediaPlayerStatus = STATE_PLAYBACK_COMPLETED;
            mediaPlayer.reset();
            mediaPlayerStatus = STATE_IDLE;

            queuePos++;
            if (queuePos != queue.size())
            {
                try {
                    mediaPlayer.setDataSource(getApplicationContext(), queue.get(queuePos).getUri());
                    changeBottomSheet(queue.get(queuePos));
                    updateMusicControlSong();
                    mediaPlayerStatus = STATE_INITIALIZED;
                    mediaPlayer.prepareAsync();
                } catch (IOException e) {
                    e.printStackTrace();
                    mediaPlayerStatus = STATE_ERROR;
                }
            }
            else {
                stopAudio();
            }
        }
    };


    public int onStartCommand(Intent intent, int flags, int startId) {

        if (intent.getAction().equals(ACTION_FORCE_PLAY))
        {
            // If a song is currently playing, stop it
            if (mediaPlayerStatus == STATE_STARTED || mediaPlayerStatus == STATE_PAUSED)
            {
                mediaPlayer.reset();
                mediaPlayerStatus = STATE_IDLE;
            }

            if (mediaPlayerStatus == STATE_IDLE) {

                mediaPlayer.setAudioAttributes(
                        new AudioAttributes.Builder()
                                .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                                .setUsage(AudioAttributes.USAGE_MEDIA)
                                .build()
                );

                mediaPlayer.setWakeMode(getApplicationContext(), PowerManager.PARTIAL_WAKE_LOCK);

                // Assign callbacks
                mediaPlayer.setOnCompletionListener(mediaPlayerOnCompletionListener);
                mediaPlayer.setOnErrorListener(this); // Set the error listener to this
                mediaPlayer.setOnPreparedListener(this);
            }

            if (mediaPlayerStatus == STATE_IDLE) {
                try {
                    mediaPlayer.setDataSource(getApplicationContext(), Uri.parse(intent.getExtras().getString("file")));
                    mediaPlayerStatus = STATE_INITIALIZED;
                } catch (IOException e) {
                    e.printStackTrace();
                    mediaPlayerStatus = STATE_ERROR;
                }

                mediaPlayer.prepareAsync(); // prepare async to not block main thread
            }
        }

        if (intent.getAction().equals(ACTION_PAUSE) && mediaPlayerStatus == STATE_STARTED) pauseMedia();

        if (intent.getAction().equals(ACTION_RESUME) && mediaPlayerStatus == STATE_PAUSED) resumeMedia();

        if (intent.getAction().equals(ACTION_PREVIOUS)) goPrevious();

        if (intent.getAction().equals(ACTION_NEXT)) goNext();

        return START_REDELIVER_INTENT;
    }

    // Binder given to clients
    private final IBinder iBinder = new LocalBinder();

    @Override
    public IBinder onBind(Intent intent) {
        return iBinder;
    }

    /** Called when MediaPlayer is ready */
    public void onPrepared(MediaPlayer player) {
        mediaPlayerStatus = STATE_PREPARED;
        try {
            player.start();
            mediaPlayerStatus = STATE_STARTED;
        } catch(Exception e)
        {
            mediaPlayerStatus = STATE_ERROR;
        }
    }


    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        Log.e("AUDIO_PLAYER", "Audio player error");
        mediaPlayerStatus = STATE_ERROR;
        mediaPlayer.release();
        mediaPlayer = null;
        try {
            mediaPlayer = new MediaPlayer(); // Recreate MediaPlayer
            mediaPlayerStatus = STATE_IDLE;
        } catch(Exception e)
        {

        }
        return true;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        // We should probably not release MediaPlayer because it is frequently used
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayerStatus = STATE_END;
        }

    }

    public static int getMediaPlayerStatus() {
        return mediaPlayerStatus;
    }

    public class LocalBinder extends Binder {
        public AudioPlayerService getService() {
            return AudioPlayerService.this;
        }
    }

    private void pauseMedia() {
        if (mediaPlayer.isPlaying()) {
            mediaPlayer.pause();
            resumePosition = mediaPlayer.getCurrentPosition();
            mediaPlayerStatus = STATE_PAUSED;
        }
    }

    private void resumeMedia() {
        if (!mediaPlayer.isPlaying()) {
            mediaPlayer.seekTo(resumePosition);
            mediaPlayer.start();
            mediaPlayerStatus = STATE_STARTED;
        }
    }

    private void goNext() {
        if (queuePos != queue.size() - 1)
        {
            Log.d("AUDIO_PLAYER", "Go next");
            mediaPlayerStatus = STATE_PLAYBACK_COMPLETED;
            mediaPlayer.reset();
            mediaPlayerStatus = STATE_IDLE;

            queuePos++;

            try {
                mediaPlayer.setDataSource(getApplicationContext(), queue.get(queuePos).getUri());
                changeBottomSheet(queue.get(queuePos));
                updateMusicControlSong();
                mediaPlayerStatus = STATE_INITIALIZED;
                mediaPlayer.prepareAsync();
            } catch (IOException e) {
                e.printStackTrace();
                mediaPlayerStatus = STATE_ERROR;
            }
        }
    }

    private void goPrevious() {
        if (queuePos != 0)
        {
            Log.d("AUDIO_PLAYER", "Go previous");
            mediaPlayerStatus = STATE_PLAYBACK_COMPLETED;
            mediaPlayer.reset();
            mediaPlayerStatus = STATE_IDLE;

            queuePos--;

            try {
                mediaPlayer.setDataSource(getApplicationContext(), queue.get(queuePos).getUri());
                changeBottomSheet(queue.get(queuePos));
                updateMusicControlSong();
                mediaPlayerStatus = STATE_INITIALIZED;
                mediaPlayer.prepareAsync();
            } catch (IOException e) {
                e.printStackTrace();
                mediaPlayerStatus = STATE_ERROR;
            }
        }
    }
}