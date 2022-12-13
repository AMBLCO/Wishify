package com.wishify;

import static android.graphics.drawable.ClipDrawable.HORIZONTAL;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


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
        SongsAdapter adapter = new SongsAdapter();
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
        recView.setLayoutManager(layoutManager);
        recView.setItemAnimator(new DefaultItemAnimator());
        recView.setAdapter(adapter);
        DividerItemDecoration decoration = new DividerItemDecoration(requireContext(), HORIZONTAL);
        recView.addItemDecoration(decoration);
    }
}