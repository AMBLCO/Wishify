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

public class PlaylistFragment extends Fragment {
    private RecyclerView recView;

    public static PlaylistAdapter adapter = new PlaylistAdapter();;
    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState)
    {
        recView = view.findViewById(R.id.playlists_rec_view);
        //adapter = new PlaylistAdapter();
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
        recView.setLayoutManager(layoutManager);
        recView.setItemAnimator(new DefaultItemAnimator());
        recView.setAdapter(adapter);
        DividerItemDecoration decoration = new DividerItemDecoration(requireContext(), HORIZONTAL);
        recView.addItemDecoration(decoration);
    }

    public void refresh()
    {
        adapter.notifyDataSetChanged();
    }

    public void setPlaylist(Playlist playlist)
    {
        adapter.setPlaylist(playlist);
    }
}
