package com.wishify;
import static com.wishify.Globals.addSongsToQueue;
import static com.wishify.Globals.queue;
import static com.wishify.Globals.queuePos;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.net.Uri;
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

import java.util.HashMap;
import java.util.List;

public class PlaylistAdapter extends RecyclerView.Adapter<PlaylistAdapter.ViewHolder>{

    private static Playlist playlist;
    private static List<Song> songList = playlist.getSongs();
    @NonNull
    @Override
    public PlaylistAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.rec_view_song_item, parent, false);

        return new PlaylistAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PlaylistAdapter.ViewHolder holder, int position) {
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

    public void filterList(List<Song> filterList) {
        songList = filterList;
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
                //Log.d("LONG_CLICK", "Registered long click");
                PopupMenu popupMenu = new PopupMenu(view.getContext(), view, Gravity.END);
                popupMenu.getMenuInflater().inflate(R.menu.songs_context_menu, popupMenu.getMenu());
                popupMenu.setOnMenuItemClickListener( item -> {

                    if (item.getItemId() == R.id.addToPlaylist)
                    {
                        popupMenu.dismiss();
                        PopupMenu playlistMenu = new PopupMenu(view.getContext(), view, Gravity.END);
                        Menu menu = playlistMenu.getMenu();
                        playlistMenu.getMenuInflater().inflate(R.menu.songs_playlists_menu, menu);
                        for (String playlist : Globals.getPlaylists().keySet()) {
                            menu.add(playlist);
                        }
                        playlistMenu.setOnMenuItemClickListener(playlistItem ->{
                            if (playlistItem.getItemId() == R.id.newPlaylist)
                            {
                                // We create a new playlist
                                // We ask for playlist name using dialog
                                AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
                                LayoutInflater li = LayoutInflater.from(view.getContext());
                                View dialogView = li.inflate(R.layout.alert_dialog_new_playlist, null);
                                builder.setTitle("Name of new playlist");
                                builder.setView(dialogView);
                                builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        // Add playlist to playlists
                                        final EditText nameOfPlaylist = (EditText) dialogView.findViewById(R.id.nameOfPlaylist);
                                        String playlistName = nameOfPlaylist.getText().toString();
                                        if (Globals.getPlaylists().get(playlistName) == null) {

                                            if (!playlistName.equals("")) {
                                                if (Globals.addPlaylist(new Playlist(playlistName, new HashMap<Uri, Song>())) == 1) { // 1 Means success
                                                    Toast.makeText(view.getContext(), "Added " + playlistName + " to playlists", Toast.LENGTH_LONG).show();
                                                } else {
                                                    Toast.makeText(view.getContext(), "Could not create playlist!", Toast.LENGTH_LONG).show();
                                                }
                                            }
                                        }
                                        else{
                                            Toast.makeText(view.getContext(), "Playlist already exists!", Toast.LENGTH_LONG).show();
                                        }
                                    }
                                });
                                AlertDialog dialog = builder.create();
                                dialog.show();
                            }
                            else
                            {
                                CharSequence chosenItem = playlistItem.getTitle();
                                // add to this playlist
                                if (Globals.getPlaylist(chosenItem.toString()) != null)
                                {
                                    if(Globals.getPlaylist(chosenItem.toString()).addSong(songList.get(getAdapterPosition())) == 1)
                                    {
                                        Toast.makeText(view.getContext(), "Added " + songList.get(getAdapterPosition()).getTitle() + " to playlist!", Toast.LENGTH_SHORT).show();
                                    }
                                    else{
                                        Toast.makeText(view.getContext(), "Could not add " + songList.get(getAdapterPosition()).getTitle() + " to playlist!", Toast.LENGTH_LONG).show();
                                    }
                                }
                            }
                            return false;
                        });
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
    public void setPlaylist(String playlistName)
    {
        playlist = Globals.getPlaylist(playlistName);
        songList = playlist.getSongs();
    }
}
