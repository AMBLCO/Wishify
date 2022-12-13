package com.wishify;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;

import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import java.util.ArrayList;

public class FileDiscoveryWorker extends Worker{
    public FileDiscoveryWorker(@NonNull Context context, @NonNull WorkerParameters params)
    {
        super(context, params);
    }

    @NonNull
    @Override
    public Result doWork()
    {
        discoverAudioFiles();

        return Result.success();
    }

    private void discoverAudioFiles()
    {
        //Ne pas oublier d'ajouter l'id de chaque fichier découvert à chaque instance de Song dans la liste
    }

}
