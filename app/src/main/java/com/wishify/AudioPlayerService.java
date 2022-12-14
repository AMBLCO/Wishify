package com.wishify;

import static com.wishify.AudioPlayer.changeBottomSheet;
import static com.wishify.AudioPlayer.stopAudio;
import static com.wishify.Globals.repeat;
import static com.wishify.Globals.repeatStartPos;
import static com.wishify.Globals.resetAndGenerateShuffleList;
import static com.wishify.Globals.queue;
import static com.wishify.Globals.queuePos;
import static com.wishify.Globals.shuffle;
import static com.wishify.Globals.shuffleList;
import static com.wishify.Globals.shuffleListPos;
import static com.wishify.MusicControl.runSeekbarUpdate;
import static com.wishify.MusicControl.stopSeekbarUpdate;
import static com.wishify.MusicControl.updateMusicControl;

import android.app.Service;
import android.content.Intent;
import android.media.AudioAttributes;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.IBinder;
import android.os.PowerManager;
import android.util.Log;

import java.io.IOException;

public class AudioPlayerService extends Service implements MediaPlayer.OnPreparedListener, MediaPlayer.OnErrorListener {
    public static MediaPlayer mediaPlayer = new MediaPlayer(); // We want only one MediaPlayer running at a given time.
    private static int mediaPlayerStatus = 0; // Start in STATE_IDLE

    private int resumePosition;

    // Arbitrary constants
    private static final String ACTION_PLAY = "com.wishify.action.PLAY";
    private static final String ACTION_PAUSE = "com.wishify.action.PAUSE";
    private static final String ACTION_RESUME = "com.wishify.action.RESUME";
    private static final String ACTION_PREVIOUS = "com.wishify.action.PREVIOUS";
    private static final String ACTION_NEXT = "com.wishify.action.NEXT";
    private static final String ACTION_SEEK = "com.wishify.action.SEEK";

    private static final int STATE_IDLE = 0;
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

            if (repeat == 2)
            {
                initMediaPlayer();
            }
            else
            {
                if (shuffle)
                {
                    shuffleListPos++;

                    if (repeat == 1 && shuffleListPos == shuffleList.size()) shuffleListPos = 0;

                    if (shuffleListPos < shuffleList.size())
                    {
                        queuePos = shuffleList.get(shuffleListPos);
                        initMediaPlayer();
                    }
                    else
                    {
                        stopAudio();
                    }
                }
                else
                {
                    queuePos++;

                    if (repeat == 1 && queuePos == queue.size()) queuePos = repeatStartPos;

                    if (queuePos < queue.size())
                    {
                        initMediaPlayer();
                    }
                    else
                    {
                        stopAudio();
                    }
                }
            }
        }
    };

    /**
     * Called on start and receives the action from the intent and call the proper method
     * @param intent
     * @param flags
     * @param startId
     * @return
     */
    public int onStartCommand(Intent intent, int flags, int startId) {

        if (intent.getAction().equals(ACTION_PLAY)) playMedia();

        if (intent.getAction().equals(ACTION_PAUSE) && mediaPlayerStatus == STATE_STARTED) pauseMedia();

        if (intent.getAction().equals(ACTION_RESUME) && mediaPlayerStatus == STATE_PAUSED) resumeMedia();

        if (intent.getAction().equals(ACTION_PREVIOUS)) goPrevious();

        if (intent.getAction().equals(ACTION_NEXT)) goNext();

        if (intent.getAction().equals(ACTION_SEEK)) seekTo(intent.getIntExtra("pos",0));

        return START_REDELIVER_INTENT;
    }

    // Binder given to clients
    private final IBinder iBinder = new LocalBinder();

    @Override
    public IBinder onBind(Intent intent) {
        return iBinder;
    }

    /**
     * Called when media player is ready to play
     * @param player
     */
    public void onPrepared(MediaPlayer player) {
        mediaPlayerStatus = STATE_PREPARED;
        try {
            player.start();
            mediaPlayerStatus = STATE_STARTED;
        } catch(Exception e)
        {
            mediaPlayerStatus = STATE_ERROR;
        }
        if (player.isPlaying()) runSeekbarUpdate();
    }


    /**
     * Called when an error occurs with the media player, restart if possible
     * @param mp
     * @param what
     * @param extra
     * @return
     */
    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        Log.e("AUDIO_PLAYER", "Audio player error");
        mediaPlayerStatus = STATE_ERROR;
        mediaPlayer.release();
        mediaPlayer = null;
        try {
            mediaPlayer = new MediaPlayer(); // Recreate MediaPlayer
            mediaPlayerStatus = STATE_IDLE;
            playMedia();
        } catch(Exception e)
        {
            Log.d("AUDIO_PLAYER", "Failed");
        }
        return true;
    }

    /**
     * Destroys the media player
     */
    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mediaPlayer != null) {
            mediaPlayerStatus = STATE_END;
            mediaPlayer.release();
        }
    }

    /**
     * Returns the status of the media player
     * @return
     */
    public static int getMediaPlayerStatus() {
        return mediaPlayerStatus;
    }

    /**
     * Used to bind the media player to the MainActivity
     */
    public class LocalBinder extends Binder {
        public AudioPlayerService getService() {
            return AudioPlayerService.this;
        }
    }

    /**
     * Sets up the media player and plays the song at the current queuePos
     */
    private void playMedia() {
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
                mediaPlayer.setDataSource(getApplicationContext(), queue.get(queuePos).getUri());
                changeBottomSheet(queue.get(queuePos));
                mediaPlayerStatus = STATE_INITIALIZED;
            } catch (IOException e) {
                e.printStackTrace();
                mediaPlayerStatus = STATE_ERROR;
            }

            if (shuffle) resetAndGenerateShuffleList();

            mediaPlayer.prepareAsync(); // prepare async to not block main thread
        }
    }

    /**
     * Pause song
     */
    private void pauseMedia() {
        if (mediaPlayer.isPlaying()) {
            mediaPlayerStatus = STATE_PAUSED;
            mediaPlayer.pause();
            resumePosition = mediaPlayer.getCurrentPosition();
        }
    }

    /**
     * Resume song
     */
    private void resumeMedia() {
        if (!mediaPlayer.isPlaying()) {
            mediaPlayer.seekTo(resumePosition);
            mediaPlayer.start();
            mediaPlayerStatus = STATE_STARTED;
            runSeekbarUpdate();
        }
    }

    /**
     * Play next song
     */
    private void goNext() {
        if (shuffle)
        {
            if (repeat == 1 && shuffleListPos == shuffleList.size() - 1) shuffleListPos = -1;
            if (shuffleListPos < shuffleList.size() - 1)
            {
                Log.d("AUDIO_PLAYER", "Go next");
                stopSeekbarUpdate();
                mediaPlayerStatus = STATE_PLAYBACK_COMPLETED;
                mediaPlayer.reset();
                mediaPlayerStatus = STATE_IDLE;

                shuffleListPos++;
                queuePos = shuffleList.get(shuffleListPos);
                initMediaPlayer();
            }
        }
        else
        {
            if (repeat == 1 && queuePos == queue.size() - 1) queuePos = repeatStartPos - 1;
            if (queuePos < queue.size() - 1)
            {
                Log.d("AUDIO_PLAYER", "Go next");
                stopSeekbarUpdate();
                mediaPlayerStatus = STATE_PLAYBACK_COMPLETED;
                mediaPlayer.reset();
                mediaPlayerStatus = STATE_IDLE;

                queuePos++;
                initMediaPlayer();
            }
        }
    }

    /**
     * Play previous song
     */
    private void goPrevious() {
        if (shuffle)
        {
            if (repeat == 1 && shuffleListPos == 0) shuffleListPos = shuffleList.size();
            if (shuffleListPos > 0)
            {
                Log.d("AUDIO_PLAYER", "Go previous");
                stopSeekbarUpdate();
                mediaPlayerStatus = STATE_PLAYBACK_COMPLETED;
                mediaPlayer.reset();
                mediaPlayerStatus = STATE_IDLE;

                shuffleListPos--;
                queuePos = shuffleList.get(shuffleListPos);
                initMediaPlayer();
            }
        }
        else
        {
            if (repeat == 1 && queuePos == repeatStartPos) queuePos = queue.size();
            if (queuePos > 0)
            {
                Log.d("AUDIO_PLAYER", "Go previous");
                stopSeekbarUpdate();
                mediaPlayerStatus = STATE_PLAYBACK_COMPLETED;
                mediaPlayer.reset();
                mediaPlayerStatus = STATE_IDLE;

                queuePos--;
                initMediaPlayer();
            }
        }
    }

    /**
     * Seek to pos in song currently being played
     * @param position
     */
    private void seekTo(int position) {
        if (mediaPlayer.isPlaying()) {
            mediaPlayerStatus = STATE_PAUSED;
            mediaPlayer.pause();
            mediaPlayer.seekTo(position);
            mediaPlayer.start();
            mediaPlayerStatus = STATE_STARTED;
        }
    }

    private void initMediaPlayer() {
        try {
            Log.d("MEDIA_PLAYER", "INIT");
            mediaPlayerStatus = STATE_INITIALIZED;
            mediaPlayer.setDataSource(getApplicationContext(), queue.get(queuePos).getUri());
            changeBottomSheet(queue.get(queuePos));
            updateMusicControl();
            mediaPlayer.prepareAsync();
        } catch (IOException e) {
            e.printStackTrace();
            mediaPlayerStatus = STATE_ERROR;
        }
    }
}