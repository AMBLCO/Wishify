package com.wishify;

import static com.wishify.Globals.addSongsToQueue;
import static com.wishify.Globals.queue;
import static com.wishify.Globals.queuePos;

import android.app.AlertDialog;
import android.content.DialogInterface;
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

    private static List<Playlist> playlistList = (List<Playlist>) Globals.getPlaylists().values();

    public PlaylistsAdapter() {}

    @NonNull
    @Override
    public PlaylistsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.rec_view_song_item, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.getPlaylistImageView().setImageBitmap(playlistList.get(position).getSongs().get(0).getBitmap());
        holder.getPlaylistNameView().setText(playlistList.get(position).getSongs().get(0).getTitle());
    }

    @Override
    public int getItemCount() {
        return playlistList.size();
    }


    public static class ViewHolder extends RecyclerView.ViewHolder {
        private ImageView playlistImageView;
        private TextView playlistNameView;

        public ViewHolder(@NonNull View playlistView) {
            super(playlistView);

            playlistView.setOnClickListener(view -> {

            });

            playlistView.setOnLongClickListener(view ->
            {
                //Log.d("LONG_CLICK", "Registered long click");
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
