package com.wishify;

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
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;

import androidx.annotation.Nullable;

import java.io.IOException;
import java.net.URI;

public class AudioPlayerService extends Service implements MediaPlayer.OnPreparedListener, MediaPlayer.OnErrorListener {
    private static final String ACTION_PLAY = "com.wishify.action.PLAY";
    MediaPlayer mediaPlayer = null;
    boolean isPlaying = false;

    public int onStartCommand(Intent intent, int flags, int startId) {

        if (intent.getAction().equals(ACTION_PLAY)) {
            Uri uri = Uri.parse(intent.getExtras().getString("file"));

            mediaPlayer = new MediaPlayer();
            mediaPlayer.setAudioAttributes(
                    new AudioAttributes.Builder()
                            .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                            .setUsage(AudioAttributes.USAGE_MEDIA)
                            .build()
            );
            try {
                mediaPlayer.setDataSource(getApplicationContext(), uri);
            } catch (IOException e) {
                e.printStackTrace();
            }
            mediaPlayer.setOnErrorListener(this); // Set the error listener to this
            mediaPlayer.setOnPreparedListener(this);
            mediaPlayer.prepareAsync(); // prepare async to not block main thread
        }

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
        player.start();
    }

    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        Log.e("AUDIO_PLAYER", "Audio player error");

        mediaPlayer.release();
        mediaPlayer = null;
        return true;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mediaPlayer != null) mediaPlayer.release();
    }

    public class LocalBinder extends Binder {
        public AudioPlayerService getService() {
            return AudioPlayerService.this;
        }
    }
}

//public class AudioPlayerService extends Service implements MediaPlayer.OnCompletionListener,
//        MediaPlayer.OnPreparedListener, MediaPlayer.OnErrorListener, MediaPlayer.OnSeekCompleteListener,
//        MediaPlayer.OnInfoListener, MediaPlayer.OnBufferingUpdateListener, AudioManager.OnAudioFocusChangeListener {
//
//    private MediaPlayer mediaPlayer;
//    //path to the audio file
//    private Uri mediaFile;
//    //Used to pause/resume MediaPlayer
//    private int resumePosition;
//    private AudioManager audioManager;
//    //Handle incoming phone calls
//    private boolean ongoingCall = false;
//    private PhoneStateListener phoneStateListener;
//    private TelephonyManager telephonyManager;
//
//    private void initMediaPlayer() {
//        mediaPlayer = new MediaPlayer();
//        //Set up MediaPlayer event listeners
//        mediaPlayer.setOnCompletionListener(this);
//        mediaPlayer.setOnErrorListener(this);
//        mediaPlayer.setOnPreparedListener(this);
//        mediaPlayer.setOnBufferingUpdateListener(this);
//        mediaPlayer.setOnSeekCompleteListener(this);
//        mediaPlayer.setOnInfoListener(this);
//        //Reset so that the MediaPlayer is not pointing to another data source
//        mediaPlayer.reset();
//
//        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
//        try {
//            // Set the data source to the mediaFile location
//            mediaPlayer.setDataSource(getApplicationContext(), mediaFile);
//        } catch (IOException e) {
//            e.printStackTrace();
//            stopSelf();
//        }
//        mediaPlayer.prepareAsync();
//    }
//
//    private void playMedia() {
//        if (!mediaPlayer.isPlaying()) {
//            mediaPlayer.start();
//        }
//    }
//
//    private void stopMedia() {
//        if (mediaPlayer == null) return;
//        if (mediaPlayer.isPlaying()) {
//            mediaPlayer.stop();
//        }
//    }
//
//    private void pauseMedia() {
//        if (mediaPlayer.isPlaying()) {
//            mediaPlayer.pause();
//            resumePosition = mediaPlayer.getCurrentPosition();
//        }
//    }
//
//    private void resumeMedia() {
//        if (!mediaPlayer.isPlaying()) {
//            mediaPlayer.seekTo(resumePosition);
//            mediaPlayer.start();
//        }
//    }
//
//    @Override
//    public void onCompletion(MediaPlayer mp) {
//        //Invoked when playback of a media source has completed.
//        stopMedia();
//        //stop the service
//        stopSelf();
//    }
//
//    //Handle errors
//    @Override
//    public boolean onError(MediaPlayer mp, int what, int extra) {
//        //Invoked when there has been an error during an asynchronous operation
//        switch (what) {
//            case MediaPlayer.MEDIA_ERROR_NOT_VALID_FOR_PROGRESSIVE_PLAYBACK:
//                Log.d("MediaPlayer Error", "MEDIA ERROR NOT VALID FOR PROGRESSIVE PLAYBACK " + extra);
//                break;
//            case MediaPlayer.MEDIA_ERROR_SERVER_DIED:
//                Log.d("MediaPlayer Error", "MEDIA ERROR SERVER DIED " + extra);
//                break;
//            case MediaPlayer.MEDIA_ERROR_UNKNOWN:
//                Log.d("MediaPlayer Error", "MEDIA ERROR UNKNOWN " + extra);
//                break;
//        }
//        return false;
//    }
//
//    @Override
//    public void onPrepared(MediaPlayer mp) {
//        //Invoked when the media source is ready for playback.
//        playMedia();
//    }
//
//    // Binder given to clients
//    private final IBinder iBinder = new LocalBinder();
//
//    @Override
//    public IBinder onBind(Intent intent) {
//        return iBinder;
//    }
//
//    @Override
//    public void onBufferingUpdate(MediaPlayer mp, int percent) {
//        //Invoked indicating buffering status of
//        //a media resource being streamed over the network.
//    }
//
//    @Override
//    public boolean onInfo(MediaPlayer mp, int what, int extra) {
//        //Invoked to communicate some info.
//        return false;
//    }
//
//    @Override
//    public void onSeekComplete(MediaPlayer mp) {
//        //Invoked indicating the completion of a seek operation.
//    }
//
//    @Override
//    public void onAudioFocusChange(int focusState) {
//        //Invoked when the audio focus of the system is updated.
//        switch (focusState) {
//            case AudioManager.AUDIOFOCUS_GAIN:
//                // resume playback
//                if (mediaPlayer == null) initMediaPlayer();
//                else if (!mediaPlayer.isPlaying()) mediaPlayer.start();
//                mediaPlayer.setVolume(1.0f, 1.0f);
//                break;
//            case AudioManager.AUDIOFOCUS_LOSS:
//                // Lost focus for an unbounded amount of time: stop playback and release media player
//                if (mediaPlayer.isPlaying()) mediaPlayer.stop();
//                mediaPlayer.release();
//                mediaPlayer = null;
//                break;
//            case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT:
//                // Lost focus for a short time, but we have to stop
//                // playback. We don't release the media player because playback
//                // is likely to resume
//                if (mediaPlayer.isPlaying()) mediaPlayer.pause();
//                break;
//            case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK:
//                // Lost focus for a short time, but it's ok to keep playing
//                // at an attenuated level
//                if (mediaPlayer.isPlaying()) mediaPlayer.setVolume(0.1f, 0.1f);
//                break;
//        }
//    }
//
//    private boolean requestAudioFocus() {
//        audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
//        int result = audioManager.requestAudioFocus(this, AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN);
//        if (result == AudioManager.AUDIOFOCUS_REQUEST_GRANTED) {
//            //Focus gained
//            return true;
//        }
//        //Could not gain focus
//        return false;
//    }
//
//    private boolean removeAudioFocus() {
//        return AudioManager.AUDIOFOCUS_REQUEST_GRANTED ==
//                audioManager.abandonAudioFocus(this);
//    }
//
//    public class LocalBinder extends Binder {
//        public AudioPlayerService getService() {
//            return AudioPlayerService.this;
//        }
//    }
//
//    //The system calls this method when an activity, requests the service be started
//    @Override
//    public int onStartCommand(Intent intent, int flags, int startId) {
//        try {
//            //An audio file is passed to the service through putExtra();
//            mediaFile = Uri.parse(intent.getExtras().getString("media"));
//        } catch (NullPointerException e) {
//            stopSelf();
//        }
//
//        //Request audio focus
//        if (!requestAudioFocus()) {
//            //Could not gain focus
//            stopSelf();
//        }
//
//        if (mediaFile != null)
//            initMediaPlayer();
//
//        return super.onStartCommand(intent, flags, startId);
//    }
//
//    @Override
//    public void onDestroy() {
//        super.onDestroy();
//        if (mediaPlayer != null) {
//            stopMedia();
//            mediaPlayer.release();
//        }
//        removeAudioFocus();
//    }
//
//    //Becoming noisy
//    private BroadcastReceiver becomingNoisyReceiver = new BroadcastReceiver() {
//        @Override
//        public void onReceive(Context context, Intent intent) {
//            //pause audio on ACTION_AUDIO_BECOMING_NOISY
//            pauseMedia();
//            //buildNotification(PlaybackStatus.PAUSED);
//        }
//    };
//
//    private void registerBecomingNoisyReceiver() {
//        //register after getting audio focus
//        IntentFilter intentFilter = new IntentFilter(AudioManager.ACTION_AUDIO_BECOMING_NOISY);
//        registerReceiver(becomingNoisyReceiver, intentFilter);
//    }
//
//    //Handle incoming phone calls
//    private void callStateListener() {
//        // Get the telephony manager
//        telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
//        //Starting listening for PhoneState changes
//        phoneStateListener = new PhoneStateListener() {
//            @Override
//            public void onCallStateChanged(int state, String incomingNumber) {
//                switch (state) {
//                    //if at least one call exists or the phone is ringing
//                    //pause the MediaPlayer
//                    case TelephonyManager.CALL_STATE_OFFHOOK:
//                    case TelephonyManager.CALL_STATE_RINGING:
//                        if (mediaPlayer != null) {
//                            pauseMedia();
//                            ongoingCall = true;
//                        }
//                        break;
//                    case TelephonyManager.CALL_STATE_IDLE:
//                        // Phone idle. Start playing.
//                        if (mediaPlayer != null) {
//                            if (ongoingCall) {
//                                ongoingCall = false;
//                                resumeMedia();
//                            }
//                        }
//                        break;
//                }
//            }
//        };
//        // Register the listener with the telephony manager
//        // Listen for changes to the device call state.
//        telephonyManager.listen(phoneStateListener,
//                PhoneStateListener.LISTEN_CALL_STATE);
//    }
//}