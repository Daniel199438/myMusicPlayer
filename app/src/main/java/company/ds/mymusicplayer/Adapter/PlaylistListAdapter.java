package company.ds.mymusicplayer.Adapter;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import company.ds.mymusicplayer.Config;
import company.ds.mymusicplayer.MainActivity;
import company.ds.mymusicplayer.Object.Folder;
import company.ds.mymusicplayer.Object.Playlist;
import company.ds.mymusicplayer.R;
import company.ds.mymusicplayer.helper.Utils;

/**
 * Created by DS on 02.04.2016.
 */
public class PlaylistListAdapter extends BaseAdapter {
    private ArrayList<Playlist> playlists;
    Context ctx;
    private static LayoutInflater inflater = null;
    private boolean isDeleteRequired;
    private long deletedPlaylistID;

    public PlaylistListAdapter(Context context, ArrayList<Playlist> playlists) {
        ctx = context;
        this.playlists = playlists;
        inflater = (LayoutInflater) ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }
    @Override
    public int getCount() {
        if (playlists != null)
            return playlists.size();
        else
            return 0;
    }

    @Override
    public Object getItem(int position) {

        return null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        final ViewHolder holder;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = inflater.inflate(R.layout.folderlist_single_row, null);
            //convertView = LayoutInflater.from(mContext).inflate(R.layout.contact_single_list, parent, false);
            holder.title = (TextView) convertView.findViewById(R.id.direct_title);
            holder.path = (TextView) convertView.findViewById(R.id.direct_path);
            holder.count = (TextView) convertView.findViewById(R.id.direct_count);
            holder.duration = (TextView) convertView.findViewById(R.id.direct_duration);
            holder.icon = (ImageView) convertView.findViewById(R.id.direct_cover);
            holder.delete = (ImageButton) convertView.findViewById(R.id.delete_btn);

            convertView.setTag(holder);
        } else holder = (ViewHolder) convertView.getTag();

        holder.title.setText(playlists.get(position).getName());
        holder.count.setVisibility(View.VISIBLE);
        holder.duration.setVisibility(View.VISIBLE);
        String countText = "";
        if(playlists.get(position).getSongsin() == 1){
           countText = String.valueOf(playlists.get(position).getSongsin() + " Song");
        }
        else if(playlists.get(position).getSongsin() == -1 || playlists.get(position).getMaxDuration() == -1){
            countText = "berechnet...";
        }
        else {
            countText = String.valueOf(playlists.get(position).getSongsin() + " Songs");
        }
        holder.count.setText(countText);
        holder.path.setText("Playlist");
        holder.icon.setImageResource(R.drawable.ic_format_list_bulleted_48pt_2x);
        holder.duration.setText(Utils.stringForTime(playlists.get(position).getMaxDuration()));
        SharedPreferences prefs = ctx.getSharedPreferences(Config.SEND, 0);
        if(prefs.getString(Config.SONGLISTTYPE_PLAYED, "").equals(Config.TYPE_PLAYLIST) || prefs.getString(Config.SONGLISTTYPE_PLAYED, "").equals(Config.TYPE_FAVOURITE_PLAYLIST)){

            if (prefs.getLong(Config.PLAYED_PARENT_ID, -2) == playlists.get(position).getId()) {
                //System.out.println(files.get(position).isSelectedFolder());
                convertView.setBackgroundColor(Color.LTGRAY);
                holder.title.setSelected(true);
                holder.path.setSelected(true);
                convertView.setSelected(true);
            }
            else {
                convertView.setBackgroundColor(Color.WHITE);
                holder.title.setSelected(false);
                holder.path.setSelected(false);
                convertView.setSelected(false);
            }
        }
        if(isDeleteRequired){
            holder.delete.setVisibility(View.VISIBLE);
        }else{
            holder.delete.setVisibility(View.GONE);
        }


        holder.delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                deletedPlaylistID = playlists.get(position).getId();
                MainActivity.createAlertDialog(1);
            }
        });

        return convertView;
    }

    public long getdeletedPlaylistID(){
        return deletedPlaylistID;
    }


    public boolean isDeleteRequired(){
        return isDeleteRequired;
    }

    public void setisDeleteRequired(boolean isDeleteRequired){
        this.isDeleteRequired = isDeleteRequired;
    }

    private class ViewHolder {
        TextView title;
        TextView path;
        TextView count;
        TextView duration;
        ImageView icon;
        ImageButton delete;
    }



}

