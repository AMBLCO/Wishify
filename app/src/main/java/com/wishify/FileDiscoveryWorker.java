package com.wishify;

import android.content.Context;

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

    }

}
