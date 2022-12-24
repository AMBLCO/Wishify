package com.wishify;

import static com.wishify.Globals.addSongsToQueue;
import static com.wishify.Globals.queue;
import static com.wishify.Globals.queuePos;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;


public class PlaylistsAdapter extends RecyclerView.Adapter<PlaylistsAdapter.ViewHolder>{

    private static List<Playlist> playlistList =  new ArrayList<>(Globals.getPlaylists().values()); // Could be null

    public PlaylistsAdapter() {
        Log.d("PLAYLISTS_CONSTRUCTOR", "Playlists is size: " + playlistList.size());
    }

    @NonNull
    @Override
    public PlaylistsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.rec_view_playlist_item, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        if (playlistList.size() != 0) {
            if (playlistList.get(position).getSize() != 0) {
                holder.getPlaylistImageView().setImageBitmap(playlistList.get(position).getSongs().get(0).getBitmap());
            }
            holder.getPlaylistNameView().setText(playlistList.get(position).getName());

        }
    }

    @Override
    public int getItemCount() {
        return playlistList.size();
    }

    public void refresh()
    {
        playlistList = new ArrayList<>(Globals.getPlaylists().values()); // Should implement cache instead of reloading all the time
    }


    public static class ViewHolder extends RecyclerView.ViewHolder {
        private ImageView playlistImageView;
        private TextView playlistNameView;

        public ViewHolder(@NonNull View playlistView) {
            super(playlistView);

            playlistImageView = (ImageView) playlistView.findViewById(R.id.playlistImage);
            playlistNameView = (TextView) playlistView.findViewById(R.id.playlistName);


            playlistView.setOnClickListener(view -> {
                // Open playlist
                //PlaylistAdapter.setPlaylist(playlistList.get(getAdapterPosition()).getName());
                MainActivity.getInstance().handlePlaylistFragment(playlistList.get(getAdapterPosition()).getName());
            });

            playlistView.setOnLongClickListener(view ->
            {
                PopupMenu popupMenu = new PopupMenu(view.getContext(), view, Gravity.END);
                popupMenu.getMenuInflater().inflate(R.menu.playlists_popup_menu, popupMenu.getMenu());
                popupMenu.setOnMenuItemClickListener( item -> {

                    if (item.getItemId() == R.id.deletePlaylist)
                    {
                        // bye bye playlist
                        if (!Globals.deletePlaylist(playlistList.get(getAdapterPosition()).getName()))
                        {
                            Toast.makeText(view.getContext(), "Could not delete playlist!", Toast.LENGTH_LONG).show();
                        }
                    }
                    return false;
                });

                popupMenu.show();
                return true;
            });
        }

        public ImageView getPlaylistImageView() {
            return playlistImageView;
        }

        public void setPlaylistImageView(ImageView playlistImageView) {
            this.playlistImageView = playlistImageView;
        }

        public TextView getPlaylistNameView() {
            return playlistNameView;
        }

        public void setPlaylistNameView(TextView playlistNameView) {
            this.playlistNameView = playlistNameView;
        }
    }


    public List<Playlist> getPlaylistList() {
        return playlistList;
    }

    public void setPlaylistList(List<Playlist> playlistsList) {
        playlistList = playlistsList;
    }
}
