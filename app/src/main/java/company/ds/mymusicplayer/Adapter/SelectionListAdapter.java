package company.ds.mymusicplayer.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.Filter;
import android.widget.Filterable;

import java.util.ArrayList;

import company.ds.mymusicplayer.Object.Song;
import company.ds.mymusicplayer.R;

/**
 * Created by DS on 02.04.2016.
 */
public class SelectionListAdapter extends BaseAdapter implements Filterable {
    private ArrayList<Song> songs;
    Context ctx;
    private static LayoutInflater inflater = null;
    private static ArrayList<Long> selectedSongs;
    private ArrayList<Song> filteredSongs;
    private SongFilter songFilter;

    public SelectionListAdapter(Context context, ArrayList<Song> songs) {
        ctx = context;
        this.songs = songs;
        inflater = (LayoutInflater) ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        selectedSongs = new ArrayList<>();
        filteredSongs = songs;
    }

    public int getCount() {
        if (filteredSongs != null)
            return filteredSongs.size();
        else
            return 0;
    }


    public Object getItem(int position) {

        return null;
    }


    public long getItemId(int position) {
        return position;
    }


    public View getView(int position, View convertView, ViewGroup parent) {

        final ViewHolder holder;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = inflater.inflate(R.layout.selectionlist_single_row, null);
            holder.checkSong = (CheckBox) convertView.findViewById(R.id.check_Song);

            convertView.setTag(holder);
            convertView.setTag(R.id.check_Song, holder.checkSong);
            holder.checkSong
                    .setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                        @Override
                        public void onCheckedChanged(CompoundButton vw,
                                                     boolean isChecked) {
                            int getPosition = (Integer) vw.getTag();
                            filteredSongs.get(getPosition).setSelectedSong(vw.isChecked());
                            if(filteredSongs.get(getPosition).isSelectedSong()){
                                selectedSongs.add(filteredSongs.get(getPosition).getId());
                            }
                            else {
                                selectedSongs.add(Long.valueOf(filteredSongs.get(getPosition).getId()));
                            }



                        }
                    });

        } else holder = (ViewHolder) convertView.getTag();

        holder.checkSong.setTag(position);

        if(filteredSongs.size() > 0) {
            holder.checkSong.setText(filteredSongs.get(position).getFileTitle());
        }
        holder.checkSong.setChecked(filteredSongs.get(position).isSelectedSong());
        return convertView;
    }

    public static ArrayList<Long> getSelectedSongs(){
        return selectedSongs;
    }

    @Override
    public Filter getFilter() {
        if (songFilter == null) {
            songFilter = new SongFilter();
        }

        return songFilter;
    }

    private class ViewHolder {
        CheckBox checkSong;
    }

    private class SongFilter extends Filter {

        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            FilterResults filterResults = new FilterResults();
            if (constraint != null && constraint.length() > 0) {
                ArrayList<Song> tempList = new ArrayList();
                boolean isMatch = false;
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
        }
    }

}
