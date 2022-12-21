package com.wishify;

import android.media.Image;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class SongsAdapter extends RecyclerView.Adapter<SongsAdapter.ViewHolder>
{
    private MainActivity mainActivity;

    public SongsAdapter(MainActivity mainActivity)
    {
        this.mainActivity = mainActivity;
    }

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



        // TEMP
        holder.getSongImageView().setImageResource(R.drawable.ic_songs);
        holder.getSongNameView().setText(Globals.getSongsList().get(position).getTitle());

        String artNalb = Globals.getSongsList().get(position).getArtist() + " - " + Globals.getSongsList().get(position).getAlbum();

        holder.getSongArtistAndAlbumView().setText(artNalb);
    }

    @Override
    public int getItemCount() {
        return Globals.getSongsList().size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder{
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
                mainActivity.playAudio(Globals.getSongsList().get(getAdapterPosition()).getUri());
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
}
