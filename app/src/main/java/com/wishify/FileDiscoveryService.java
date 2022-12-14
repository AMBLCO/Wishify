package com.wishify;

import static android.os.Environment.DIRECTORY_MUSIC;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.IBinder;
import android.provider.MediaStore;
import android.util.Log;
import android.webkit.MimeTypeMap;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.annotations.NonNull;
import io.reactivex.rxjava3.core.Maybe;
import io.reactivex.rxjava3.core.MaybeObserver;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class FileDiscoveryService extends Service {
    public static final String CHANNEL_ID = "FileDiscoveryService";
    public static final int FILEDISCOVERY_NOTIFICATION_ID = 10;
    private static final MediaMetadataRetriever mmr = new MediaMetadataRetriever();

    /**
     * Recursive find audio files
     * @param appContext Application Context
     * @param filePath Path to file
     * @param songList List of songs
     */
    private static void findAudioRecursive(Context appContext, File filePath, List<Song> songList)
    {
        File[] files = filePath.listFiles();

        if (files != null)
        {
            for (File file : files)
            {
                if (file.isDirectory()) {
                    findAudioRecursive(appContext, file, songList); // This hurts
                }
                else
                {
                    try {
                        mmr.setDataSource(appContext, Uri.fromFile(file));
                    }
                    catch (Exception e)
                    {
                        Log.e("FILES", "File probably does not have readable mp3 tags");
                        continue;
                    }

                    if (Objects.equals(mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_MIMETYPE), "audio/mpeg")) {
                        try {
                            String title = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE);
                            String artist = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ARTIST);
                            String album = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ALBUM);
                            int duration = Integer.parseInt(mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION));

                            byte[] songImage = mmr.getEmbeddedPicture();
                            Bitmap bitmap = BitmapFactory.decodeResource(appContext.getResources(), R.drawable.ic_songs);
                            if (songImage != null) {
                                bitmap = BitmapFactory.decodeByteArray(songImage, 0, songImage.length);
                            }
                            songList.add(new Song(Uri.fromFile(file), title, artist, album, duration, bitmap));
                            Globals.addSongToMap(new Song(Uri.fromFile(file), title, artist, album, duration, bitmap));


                        } catch (Exception e) {
                            Log.e("FILES", "Exception thrown during mmr.extractMetadata()");
                        }
                    }
                }
            }
        }
    }


    /**
     * Initiates recursive search
     * @param appContext Application context
     * @return List of songs
     */
    private static List<Song> findAudioFilesFromNavig(Context appContext)
    {
        List<Song> songList = new ArrayList<>();

        File filePath = Environment.getExternalStoragePublicDirectory(DIRECTORY_MUSIC);
        File[] files = filePath.listFiles();

        if (files != null) {
            findAudioRecursive(appContext, filePath, songList);
        }
        else {
            Log.e("FILES", "files is nullptr");
        }

        return songList;
    }


    /**
     * Manages work
     * @param appContext Application context
     */
    private void work(Context appContext)
    {
        Maybe<List<Song>> maybe = createMaybe(appContext);

        maybe.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
            .subscribe(new MaybeObserver<List<Song>>()
            {
                @Override
                public void onSubscribe(@NonNull Disposable d)
                {
                }

                @Override
                public void onSuccess(@NonNull List<Song> songs)
                {
                    // Send result to main SongList
                    Globals.setSongsList(songs);
                    stopSelf(); // Stop service
                }

                @Override
                public void onError(@NonNull Throwable e)
                {
                    Log.e("FILE_DISCOVERY", "onError() called with error" + e.toString());
                    stopSelf(); // Stop service
                }

                @Override
                public void onComplete()
                {
                    stopSelf(); // Stop service
                }
            });

    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        createNotificationChannel();
        Intent notificationIntent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this,
                0, notificationIntent, PendingIntent.FLAG_IMMUTABLE);
        Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("Wishify File Discovery")
                .setContentText("Discovering audio files in progress")
                .setSmallIcon(R.drawable.ic_songs)
                .setContentIntent(pendingIntent)
                .build();
        startForeground(FILEDISCOVERY_NOTIFICATION_ID, notification);

        work(getApplicationContext());
        return START_NOT_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    /**
     * Creates notifications
     */
    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel serviceChannel = new NotificationChannel(
                    CHANNEL_ID,
                    "Wishify FileDiscovery Channel",
                    NotificationManager.IMPORTANCE_DEFAULT
            );
            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(serviceChannel);
        }
    }

    private static Maybe<List<Song>> createMaybe(Context appContext)
    {
        return Maybe.create(emitter -> {

           List<Song> audioFiles = findAudioFilesFromNavig(appContext);

           if (!audioFiles.isEmpty())
           {
               // Start filling playlists before calling on success
               findAndPopulatePlaylists(appContext);

               emitter.onSuccess(audioFiles);
           }
           else
           {
               emitter.onComplete();
           }
        });
    }

    /**
     * Handles reading playlist files
     * @param appContext Application context
     */
    private static void findAndPopulatePlaylists(Context appContext)
    {
        // Get playlists from file
        File[] files = appContext.getFilesDir().listFiles();
        if (files != null) {

            for (File file : files)
            {
                String playlistName = file.getName().substring(0, file.getName().length() - 4); // Remove .txt the wish.com way
                HashMap<Uri, Song> list = new HashMap<>();
                // We must fetch its songs
                try (BufferedReader playlistReader = new BufferedReader(new InputStreamReader(appContext.openFileInput(playlistName + ".txt")))) {
                    String songUri;
                    do {
                        songUri = playlistReader.readLine();
                        if (songUri != null) {
                            Song song = Globals.getSongsMap().get(Uri.parse(songUri));
                            if (song != null) {
                                list.put(song.getUri(), song);
                            }
                        }
                    } while(songUri != null);
                } catch (Exception e) {
                    Log.e("PLAYLISTS_FETCHER", "Exception thrown while reading playlist's songs " + e.toString());
                }
                Globals.addPlaylist(new Playlist(playlistName, list));
            }

        }
    }
}