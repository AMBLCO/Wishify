package com.wishify;

import static com.wishify.Globals.addSongsToQueue;
import static com.wishify.Globals.queue;
import static com.wishify.Globals.queuePos;
import static java.lang.String.valueOf;

import android.media.Image;
import android.media.MediaMetadataRetriever;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class SongsAdapter extends RecyclerView.Adapter<SongsAdapter.ViewHolder>
{
    private static List<Song> songList = Globals.getSongsList();

    public SongsAdapter() {}

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.rec_view_song_item, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        // Fetch from Audio File Discovery Service

        holder.getSongImageView().setImageBitmap(songList.get(position).getBitmap());
        holder.getSongNameView().setText(songList.get(position).getTitle());

        String artNalb = songList.get(position).getArtist() + " - " + songList.get(position).getAlbum();

        holder.getSongArtistAndAlbumView().setText(artNalb);
    }

    @Override
    public int getItemCount() {
        return songList.size();
    }

    public void filterList(List<Song> filterlist) {
        songList = filterlist;
        notifyDataSetChanged();
    }


    public static class ViewHolder extends RecyclerView.ViewHolder{
        private ImageView songImageView;
        private TextView songNameView;
        private TextView songArtistAndAlbumView;

        public ViewHolder(View songView)
        {
            super(songView);
            songImageView = (ImageView) songView.findViewById(R.id.songImage);
            songNameView = (TextView) songView.findViewById(R.id.songName);
            songArtistAndAlbumView = (TextView) songView.findViewById(R.id.songArtistAndAlbum);
            songView.setOnClickListener(view -> {
                AudioPlayer.playAudio(songList.get(getAdapterPosition()));
                addSongsToQueue(songList);
                queuePos = getAdapterPosition();
            });

            songView.setOnLongClickListener(view ->
            {
                Log.d("LONG_CLICK", "Registered long click"); // Works
                PopupMenu popupMenu = new PopupMenu(view.getContext(), view, Gravity.END);
                popupMenu.getMenuInflater().inflate(R.menu.songs_context_menu, popupMenu.getMenu());
                popupMenu.setOnMenuItemClickListener( item -> {

                    if (item.getItemId() == R.id.addToPlaylist)
                    {
                        popupMenu.dismiss();
                        PopupMenu playlistMenu = new PopupMenu(view.getContext(), view, Gravity.END);
                        Menu menu = playlistMenu.getMenu();
                        playlistMenu.getMenuInflater().inflate(R.menu.songs_playlists_menu, menu);
                        menu.add("Test Playlist1");
                        menu.add("Test Playlist2");
                        playlistMenu.show();

                    }

                    if (item.getItemId() == R.id.append)
                    {
                        queue.add(songList.get(getAdapterPosition()));
                    }

                    if (item.getItemId() == R.id.playNext)
                    {
                        queue.add(queuePos + 1, songList.get(getAdapterPosition()));
                    }
                    return false;
                });
                popupMenu.show();
                return true;
            });
        }



        public ImageView getSongImageView() {
            return songImageView;
        }

        public void setSongImageView(ImageView songImageView) {
            this.songImageView = songImageView;
        }

        public TextView getSongNameView() {
            return songNameView;
        }

        public void setSongNameView(TextView songNameView) {
            this.songNameView = songNameView;
        }

        public TextView getSongArtistAndAlbumView() {
            return songArtistAndAlbumView;
        }

        public void setSongArtistAndAlbumView(TextView songArtistAndAlbumView) {
            this.songArtistAndAlbumView = songArtistAndAlbumView;
        }
    }

    public List<Song> getSongList() {
        return songList;
    }

    public void setSongList(List<Song> songList) {
        this.songList = songList;
    }
}