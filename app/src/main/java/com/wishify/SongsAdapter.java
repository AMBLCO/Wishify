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
    private final ArrayList<Song> testDataSet = new ArrayList<>();
    private MainActivity mainActivity;


    public SongsAdapter(MainActivity mainActivity)
    {
        testDataSet.add(new Song("Name1", "Artist1", "Album1", 0));
        testDataSet.add(new Song("Name2", "Artist2", "Album2", 0));
        testDataSet.add(new Song("Name3", "Artist3", "Album3", 0));
        testDataSet.add(new Song("Name4", "Artist4", "Album4", 0));
        testDataSet.add(new Song("Name5", "Artist5", "Album5", 0));
        testDataSet.add(new Song("Name6", "Artist6", "Album6", 0));
        testDataSet.add(new Song("Name7", "Artist7", "Album7", 0));
        testDataSet.add(new Song("Name8", "Artist8", "Album8", 0));
        testDataSet.add(new Song("Name9", "Artist9", "Album9", 0));
        testDataSet.add(new Song("Name10", "Artist10", "Album10", 0));
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
        holder.getSongNameView().setText(testDataSet.get(position).getTitle());

        String artNalb = testDataSet.get(position).getArtist() + " - " + testDataSet.get(position).getAlbum();

        holder.getSongArtistAndAlbumView().setText(artNalb);
    }

    @Override
    public int getItemCount() {
        return testDataSet.size();
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
                mainActivity.playAudio(testDataSet.get(getAdapterPosition()).getId());
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
