package com.wishify;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


public class SongsFragment extends Fragment {
    private RecyclerView recView;
    private View view;

    public SongsFragment()
    {
        super(R.layout.fragment_songs);
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState)
    {
        this.view = view;
        recView = view.findViewById(R.id.songs_rec_view);

    }
}