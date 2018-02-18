package company.ds.mymusicplayer.Adapter;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import company.ds.mymusicplayer.Config;
import company.ds.mymusicplayer.Fragments.SongList;
import company.ds.mymusicplayer.MainActivity;
import company.ds.mymusicplayer.Object.Song;
import company.ds.mymusicplayer.R;
import company.ds.mymusicplayer.helper.Utils;

/**
 * Created by DS on 02.04.2016.
 */
public class SongListAdapter extends BaseAdapter implements Filterable {
    private ArrayList<Song> songs;
    Context ctx;
    private static LayoutInflater inflater = null;
    private boolean isDeleteRequired;
    private long deletedSongID;
    private ArrayList<Song> filteredSongs;
    private SongFilter songFilter;

    public SongListAdapter(Context context, ArrayList<Song> songs) {
        ctx = context;
        this.songs = songs;
        this.filteredSongs = songs;
        Song s = new Song("Zufall", "Zufall");
        if (!this.filteredSongs.contains(s)) {
            this.filteredSongs.add(0, s);
        }
        inflater = (LayoutInflater) ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        getFilter();
    }

    public int getCount() {
        if (filteredSongs != null)
            return filteredSongs.size();
        else
            return 0;
    }


    public Object getItem(int position) {

        return filteredSongs.get(position);
    }


    public long getItemId(int position) {
        return position;
    }


    public View getView(final int position, View convertView, ViewGroup parent) {

        final ViewHolder holder;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = inflater.inflate(R.layout.folderlist_single_row, null);
            holder.title = (TextView) convertView.findViewById(R.id.direct_title);
            holder.path = (TextView) convertView.findViewById(R.id.direct_path);
            holder.contact_profilepic = (ImageView) convertView.findViewById(R.id.direct_cover);
            holder.delete = (ImageButton) convertView.findViewById(R.id.delete_btn);
            //holder.loader = new MyImageLoader(holder.contact_profilepic, files.get(position).getAlbumPic());
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        if (filteredSongs.size() > 0) {

            if (filteredSongs.get(position).getFileTitle().equals("Zufall") && filteredSongs.get(position).getPath().equals("Zufall")) {
                holder.title.setText("Zufallswiedergabe");
                holder.path.setText(Utils.showPathOutside(filteredSongs.get(position).getPath()));
                holder.contact_profilepic.setImageResource(R.drawable.ic_action_shuffle_black_2);
                convertView.setBackgroundColor(Color.WHITE);
            } else {
                holder.title.setText(filteredSongs.get(position).getFileTitle());
                holder.path.setText(Utils.showPathOutside(filteredSongs.get(position).getPath()));
                /*
                if (files.get(position).getAlbumPic() != null && !files.get(position).getAlbumPic().equals("")) {
                    if(holder.loader == null){
                        //holder.loader.execute();
                    }

                } else {
                    holder.contact_profilepic.setImageResource(R.drawable.note);
                }
                */
                SharedPreferences prefs = ctx.getSharedPreferences(Config.SEND, 0);
                if (!prefs.getString(Config.SONGLISTTYPE_VISITED, "").equals(Config.TYPE_ALLSONGS) && filteredSongs.get(position).getAlbumPic() != null && !filteredSongs.get(position).getAlbumPic().equals("")) {
                    holder.contact_profilepic.setImageBitmap(Utils.convertBytetoBitmap(filteredSongs.get(position).getAlbumPic()));
                } else {
                    holder.contact_profilepic.setImageResource(R.drawable.note);
                }
                holder.title.setMaxWidth(Integer.MAX_VALUE);
                holder.path.setMaxWidth(Integer.MAX_VALUE);

                if (prefs.getLong(Config.VISITED_PARENT_ID, -2) == prefs.getLong(Config.PLAYED_PARENT_ID, -2)) {
                    if (prefs.getLong(Config.PLAYED_SONG_ID, -2) == filteredSongs.get(position).getId()) {
                        convertView.setBackgroundColor(Color.LTGRAY);
                        holder.title.setSelected(true);
                        holder.path.setSelected(true);
                    } else {
                        convertView.setBackgroundColor(Color.WHITE);
                        holder.title.setSelected(false);
                        holder.path.setSelected(false);
                    }
                }
            }

        } else {
            holder.title.setVisibility(View.GONE);
            holder.path.setVisibility(View.GONE);
            holder.contact_profilepic.setVisibility(View.GONE);
            //holder.empty.setVisibility(View.VISIBLE);
        }


        if (isDeleteRequired && position > 0) {
            holder.delete.setVisibility(View.VISIBLE);
        } else {
            holder.delete.setVisibility(View.GONE);
        }

        holder.delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                deletedSongID = filteredSongs.get(position).getId();
                MainActivity.createAlertDialog(2);
            }
        });

        return convertView;
    }


    public long getdeletedSongID() {
        return deletedSongID;
    }


    public boolean isDeleteRequired() {
        return isDeleteRequired;
    }

    public void setisDeleteRequired(boolean isdeleteRequired) {
        isDeleteRequired = isdeleteRequired;
    }

    @Override
    public Filter getFilter() {
        if (songFilter == null) {
            songFilter = new SongFilter();
        }

        return songFilter;
    }


    private class SongFilter extends Filter {
        boolean isMatch = false;

        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            FilterResults filterResults = new FilterResults();
            if (constraint != null && constraint.length() > 0) {
                ArrayList<Song> tempList = new ArrayList();
                // search content in friend list
                for (Song song : songs) {
                    if (song.getFileTitle() != null) {
                        if (!song.getFileTitle().equals("")) {
                            if (song.getFileTitle().toLowerCase().contains(constraint.toString().toLowerCase())) {
                                isMatch = true;
                            }
                        }
                    }
                    if (song.getSongTitle() != null) {
                        if (!song.getSongTitle().equals("")) {
                            if (song.getSongTitle().toLowerCase().contains(constraint.toString().toLowerCase())) {
                                isMatch = true;
                            }
                        }
                    }
                    if (song.getAuthor() != null) {
                        if (!song.getAuthor().equals("")) {
                            if (song.getAuthor().toLowerCase().contains(constraint.toString().toLowerCase())) {
                                isMatch = true;
                            }
                        }
                    }
                    if (song.getAlbumname() != null) {
                        if (!song.getAlbumname().equals("")) {
                            if (song.getAlbumname().toLowerCase().contains(constraint.toString().toLowerCase())) {
                                isMatch = true;
                            }
                        }
                    }
                    if (song.getPath() != null) {
                        if (!song.getPath().equals("")) {
                            if (song.getPath().toLowerCase().contains(constraint.toString().toLowerCase())) {
                                isMatch = true;
                            }
                        }
                    }
                    if (isMatch) {
                        tempList.add(song);
                    }
                    isMatch = false;
                }

                filterResults.count = tempList.size();
                filterResults.values = tempList;
            } else {
                filterResults.count = songs.size();
                filterResults.values = songs;
            }

            return filterResults;
        }

        @Override
        protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
            filteredSongs = (ArrayList<Song>) filterResults.values;
            notifyDataSetChanged();
            SongList.setFilteredSongs(filteredSongs);
        }
    }

    private class ViewHolder {
        TextView title;
        TextView path;
        ImageView contact_profilepic;
        ImageButton delete;
    }
}
