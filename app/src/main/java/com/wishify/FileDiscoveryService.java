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

import java.io.File;
import java.io.FilenameFilter;
import java.nio.file.Files;
import java.util.ArrayList;
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
    //private static ContentResolver contentResolver; // To be init
    //private static MimeTypeMap mime; // To be init
    private static final MediaMetadataRetriever mmr = new MediaMetadataRetriever();

    /*
    // Main work function called by work()
    private static List<Song> findAudioFiles(Context appContext)
    {
        List<Song> songList = new ArrayList<>();

        Uri collection;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            collection = MediaStore.Audio.Media.getContentUri(MediaStore.VOLUME_EXTERNAL);
        } else {
            collection = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        }

        Log.d("FILE_DISCOVERY", "collection uri is: " + collection.toString());
        // content://media/external/audio/media

        String[] projection = new String[] {
                //MediaStore.Audio.Media.IS_MUSIC,
                MediaStore.Audio.Media._ID,
                MediaStore.Audio.Media.TITLE,
                MediaStore.Audio.Media.ARTIST,
                MediaStore.Audio.Media.ALBUM,
                MediaStore.Audio.Media.DURATION,
                MediaStore.Audio.Media.SIZE
        };
        String selection = MediaStore.Audio.Media.IS_MUSIC + " != 0"; // WHERE IS_MUSIC != 0 -> IT IS A MUSIC FILE
        String[] selectionArgs = new String[]
                {
                //String.valueOf(TimeUnit.MILLISECONDS.convert(5, TimeUnit.MINUTES));
                };

        String sortOrder = MediaStore.Audio.Media.TITLE + " ASC";

        try (Cursor cursor = appContext.getContentResolver().query(
                collection,
                projection,
                selection,
                null,
                sortOrder
        )) {
            // Cache column indices.
            int idColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media._ID);
            int titleColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE);
            int artistColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST);
            int albumColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM);
            int durationColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION);
            //int sizeColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.SIZE);

            //cursor.moveToFirst();

            while (cursor.moveToNext()) {
                // Get values of columns for a given song.
                long id = cursor.getLong(idColumn);
                String title = cursor.getString(titleColumn);
                String artist = cursor.getString(artistColumn);
                String album = cursor.getString(albumColumn);
                int duration = cursor.getInt(durationColumn);
                //int size = cursor.getInt(sizeColumn);

                Uri contentUri = ContentUris.withAppendedId(
                        MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, id);

                // Stores column values and the contentUri in a local object
                // that represents the media file.
                // get bitmap
                MediaMetadataRetriever mmr = new MediaMetadataRetriever();
                mmr.setDataSource(appContext, contentUri);
                byte[] data = mmr.getEmbeddedPicture();
                Bitmap bitmap = BitmapFactory.decodeResource(appContext.getResources(), R.drawable.ic_songs);
                if (data != null)
                {
                    bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
                }
                songList.add(new Song(contentUri, title, artist, album, duration, bitmap));
            }
        } catch (Exception e)
        {
            Log.e("FILE_DISCOVERY", "EXCEPTION IN try(cursor)");
        }

        return songList;
    }
    */

    // God help us
    private static void findAudioRecursive(Context appContext, File filePath, List<Song> songList)
    {
        Log.d("FILES", "songList is of size " + songList.size());
        File[] files = filePath.listFiles();

        if (files != null)
        {
            for (File file : files)
            {
                Log.d("FILES", "File Uri: " + Uri.fromFile(file));
                //Log.d("FILES", "File path: " + file.getPath());
                if (file.isDirectory()) {
                    Log.d("FILES", "File is directory");
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
                    }

                    if (Objects.equals(mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_MIMETYPE), "audio/mpeg")) {
                        try {
                            String title = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE);
                            if (title == null) Log.e("FILES", "Title is null!");
                            String artist = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ARTIST);
                            if (artist == null) Log.e("FILES", "Artist is null!");
                            String album = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ALBUM);
                            if (album == null) Log.e("FILES", "Album is null!");
                            int duration = Integer.parseInt(mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)) / 1000; // Extracted is in ms, we want seconds
                            if (duration == 0) Log.e("FILES", "Duration is 0!");

                            byte[] songImage = mmr.getEmbeddedPicture();
                            Bitmap bitmap = BitmapFactory.decodeResource(appContext.getResources(), R.drawable.ic_songs);
                            if (songImage != null) {
                                bitmap = BitmapFactory.decodeByteArray(songImage, 0, songImage.length);
                            }
                            Log.d("FILES", "Adding song to list");
                            songList.add(new Song(Uri.fromFile(file), title, artist, album, duration, bitmap));

                        } catch (Exception e) {
                            Log.e("FILES", "Exception thrown during mmr.extractMetadata()");
                        }
                    }
                }
            }
        }
    }


    private static List<Song> findAudioFilesFromNavig(Context appContext)
    {
        //contentResolver = appContext.getContentResolver();
        //mime = MimeTypeMap.getSingleton();

        List<Uri> uriList = new ArrayList<>();
        List<Song> songList = new ArrayList<>();

        File filePath = Environment.getExternalStoragePublicDirectory(DIRECTORY_MUSIC);
        Log.d("FILES", "Path : " + filePath.getPath());
        File[] files = filePath.listFiles();

        if (files != null) {
            findAudioRecursive(appContext, filePath, songList);
        }
        else {
            Log.e("FILES", "files is nullptr");
        }


        // parse all uris MergeCursor
        Log.d("FILES", "Acquired all songs Uri : " + uriList.size());




        return songList;
    }



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
                    Log.d("FILE_DISCOVERY", "onSuccess() called with size " + songs.size());
                    Globals.addToSongsList(songs);
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
                    Log.d("FILE_DISCOVERY", "onComplete() called");
                    stopSelf(); // Stop service
                }
            });

    }

    public FileDiscoveryService()
    {

    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        //String input = intent.getStringExtra("inputExtra");
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

        // WORK HERE
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

           //List<Song> audioFiles2 = findAudioFiles(appContext);
           List<Song> audioFiles = findAudioFilesFromNavig(appContext);

           Log.d("FILE_DISCOVERY", "Found " + audioFiles.size() + " songs");
           if (!audioFiles.isEmpty())
           {
               emitter.onSuccess(audioFiles);
           }
           else
           {
               emitter.onComplete();
           }
        });
    }
}