package company.ds.mymusicplayer.Adapter;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import company.ds.mymusicplayer.Config;
import company.ds.mymusicplayer.Object.Folder;
import company.ds.mymusicplayer.R;
import company.ds.mymusicplayer.helper.Utils;

/**
 * Created by DS on 02.04.2016.
 */
public class FolderListAdapter extends BaseAdapter {
    private ArrayList<Folder> files;
    Context ctx;
    private static LayoutInflater inflater = null;

    public FolderListAdapter(Context context, ArrayList<Folder> files) {
        ctx = context;
        this.files = files;
        inflater = (LayoutInflater) ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }
    @Override
    public int getCount() {
        if (files != null)
            return files.size();
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
    public View getView(int position, View convertView, ViewGroup parent) {

        final ViewHolder holder;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = inflater.inflate(R.layout.folderlist_single_row, null);
            //convertView = LayoutInflater.from(mContext).inflate(R.layout.contact_single_list, parent, false);
            holder.title = (TextView) convertView.findViewById(R.id.direct_title);
            holder.path = (TextView) convertView.findViewById(R.id.direct_path);
            holder.count = (TextView) convertView.findViewById(R.id.direct_count);
            holder.duration = (TextView) convertView.findViewById(R.id.direct_duration);
            holder.contact_profilepic = (ImageView) convertView.findViewById(R.id.direct_cover);

            convertView.setTag(holder);
        } else holder = (ViewHolder) convertView.getTag();

        holder.title.setText(files.get(position).getName());
        holder.path.setText(Utils.showPathOutside(files.get(position).getPath()));
        holder.count.setVisibility(View.VISIBLE);
        holder.duration.setVisibility(View.VISIBLE);
        String countText = "";
        if(files.get(position).getSongsin() == 1){
           countText = String.valueOf(files.get(position).getSongsin() + " Song");
        }
        else if(files.get(position).getSongsin() == -1 || files.get(position).getMaxDuration() == -1){
            countText = "berechnet...";
        }
        else {
            countText = String.valueOf(files.get(position).getSongsin() + " Songs");
        }
        holder.count.setText(countText);

        holder.duration.setText(Utils.stringForTime(files.get(position).getMaxDuration()));

        SharedPreferences prefs = ctx.getSharedPreferences(Config.SEND, 0);
        if(prefs.getString(Config.SONGLISTTYPE_PLAYED, "").equals(Config.TYPE_FOLDER) || prefs.getString(Config.SONGLISTTYPE_PLAYED, "").equals(Config.TYPE_FAVOURITE_FOLDER)){

            if (prefs.getLong(Config.PLAYED_PARENT_ID, -2) == files.get(position).getId()) {
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


        return convertView;
    }


    private class ViewHolder {
        TextView title;
        TextView path;
        TextView count;
        TextView duration;
        ImageView contact_profilepic;
    }



}

