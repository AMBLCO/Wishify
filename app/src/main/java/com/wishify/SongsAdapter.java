package com.wishify;

import static java.lang.String.valueOf;

import android.media.Image;
import android.media.MediaMetadataRetriever;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
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

public class SongsAdapter extends RecyclerView.Adapter<SongsAdapter.ViewHolder> implements Filterable
{
    private List<Song> songList = Globals.getSongsList();
    private List<Song> songListFilter = Globals.getSongsList();

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

    @Override
    public Filter getFilter() {
        Filter filter = new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {

                FilterResults filterResults = new FilterResults();
                if (charSequence == null || charSequence.length() == 0) {
                    filterResults.values = songListFilter;
                    filterResults.count = songListFilter.size();
                }
                else {
                    String searchStr = charSequence.toString().toLowerCase();
                    List<Song> songs = new ArrayList<>();
                    for (Song song : songListFilter){
                        if(song.getTitle().toLowerCase().contains(searchStr))
                        {
                            songs.add(song);
                        }
                    }

                    filterResults.values = songs;
                    filterResults.count = songs.size();
                }
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                songList = ((List<Song>) filterResults.values);
                //notifyDataSetChanged();
            }
        };
        return filter;
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
                AudioPlayer.playAudio(Globals.getSongsList().get(getAdapterPosition()));
            });

            songView.setOnLongClickListener(view ->
            {
                Log.d("LONG_CLICK", "Registered long click"); // Works
                PopupMenu popupMenu = new PopupMenu(view.getContext(), view, Gravity.END);
                popupMenu.getMenuInflater().inflate(R.menu.songs_context_menu, popupMenu.getMenu());
                popupMenu.setOnMenuItemClickListener( item -> {
                    Toast.makeText(view.getContext(), "Clicked popup", Toast.LENGTH_SHORT).show();
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