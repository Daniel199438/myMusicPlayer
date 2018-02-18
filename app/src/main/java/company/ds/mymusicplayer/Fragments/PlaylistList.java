package company.ds.mymusicplayer.Fragments;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;

import company.ds.mymusicplayer.Adapter.FolderListAdapter;
import company.ds.mymusicplayer.Adapter.PlaylistListAdapter;
import company.ds.mymusicplayer.Adapter.SelectionListAdapter;
import company.ds.mymusicplayer.Adapter.SongListAdapter;
import company.ds.mymusicplayer.Config;
import company.ds.mymusicplayer.MainActivity;
import company.ds.mymusicplayer.MySqlDatabases.DataSource;
import company.ds.mymusicplayer.Object.Folder;
import company.ds.mymusicplayer.Object.Playlist;
import company.ds.mymusicplayer.Object.Song;
import company.ds.mymusicplayer.R;
import company.ds.mymusicplayer.Service.MusicService;
import company.ds.mymusicplayer.helper.Utils;

/**
 * Created by DS on 02.04.2016.
 */
public class PlaylistList extends Fragment {
    static ListView listView;
    static Activity activity;
    static PlaylistListAdapter adapter;
    SharedPreferences prefs;
    static ArrayList<Playlist> playlists;
    static ProgressDialog progress;
    private boolean musicBound = false;
    private Intent playIntent;
    private int fragmentNR;
    private Menu mMenu;
    private static DataSource dataSource;
    private TextView empty;

    /**
     * Returns a new instance of this fragment for the given section
     * number.
     */
    public static PlaylistList newInstance(String text) {
        PlaylistList fragment = new PlaylistList();
        Bundle args = new Bundle();
        //args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.list_fragment, container, false);
        activity = getActivity();
        listView = (ListView) view.findViewById(R.id.list);
        empty = (TextView) view.findViewById(R.id.empty);
        prefs = activity.getSharedPreferences(Config.SEND, 0);
        dataSource = new DataSource(activity);
        playlists = dataSource.getAllPlaylists();
        setHasOptionsMenu(true);

        // folders = Utils.GetAndUpdateFolders(Environment.getExternalStorageDirectory(), Environment.getExternalStorageDirectory().getPath(), activity);
        if (playlists.size() == 0) {
            empty.setText("Es wurden noch keine Playlists erstellt.");
            empty.setVisibility(View.VISIBLE);
            listView.setVisibility(View.GONE);
        }
        else {
            //setFavouritePlaylist
            for (int i = 0; i < playlists.size(); i++){
                if (playlists.get(i).getName().equals("Favoriten")){
                    Playlist favPl = playlists.get(i);
                    playlists.remove(i);
                    playlists.add(0, favPl);
                    break;
                }
            }



            //playlists.add(0, MainActivity.updateFavouritePlaylist());
            //MainActivity.changeFavouriteIcon();
            long actPlayedFolderID = prefs.getLong(Config.PLAYED_PARENT_ID, -1);
            for (int i = 0; i < playlists.size(); i++) {
                playlists.get(i).setselectedPlaylist(false);
            }
            for (int i = 0; i < playlists.size(); i++) {
                if (actPlayedFolderID == playlists.get(i).getId()) {
                    playlists.get(i).setselectedPlaylist(true);
                }
            }
            adapter = new PlaylistListAdapter(activity, playlists);
            listView.setAdapter(adapter);

            //listView = (ListView)view.findViewById(R.id.listView);

        }

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                SharedPreferences.Editor edit = prefs.edit();
                edit.putLong(Config.VISITED_PARENT_ID, playlists.get(position).getId());
                if (playlists.get(position).getId() == -1) {
                    edit.putString(Config.SONGLISTTYPE_VISITED, Config.TYPE_FAVOURITE_PLAYLIST);
                } else {
                    edit.putString(Config.SONGLISTTYPE_VISITED, Config.TYPE_PLAYLIST);
                }

                edit.commit();

                MainActivity.setNewFragment(0);
            }
        });
        //MainActivity.changeFavouriteIcon();
        return view;
    }

    public static void disablePlaylistSelector() {
        for (Playlist playlist : playlists) {
            playlist.setselectedPlaylist(false);
        }
        adapter.notifyDataSetChanged();
    }

    public static long getdeletedPlaylistID() {
        if (adapter != null) {
            return adapter.getdeletedPlaylistID();
        }
        return 0;
    }

    public static void deletePlaylist() {
        if (adapter != null) {
            long pID = getdeletedPlaylistID();
            for (int i = 0; i < playlists.size(); i++) {
                if (playlists.get(i).getId() == pID) {
                    playlists.remove(i);
                }
            }
            dataSource.deletePlaylist(pID);
        }
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        //menu.clear();
        //inflater.inflate(R.menu.menu_main, menu);
        mMenu = menu;
        mMenu.findItem(R.id.createPL_btn).setVisible(true);
        mMenu.findItem(R.id.deletePL_btn).setVisible(true);
        //mMenu.findItem(R.id.favourite_btn).setVisible(false);

        MainActivity.changeFavouriteIcon(mMenu);
    }

    public static boolean isDeleteRequired() {
        if (adapter != null) {
            return adapter.isDeleteRequired();
        }
        return false;
    }

    public static void setDeleteRequired(boolean deleteRequired) {
        if (adapter != null) {
            adapter.setisDeleteRequired(deleteRequired);
            adapter.notifyDataSetChanged();
        }
    }

    /*
    //connect to the service
    private ServiceConnection musicConnection = new ServiceConnection(){

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            MusicService.MusicBinder binder = (MusicService.MusicBinder)service;
            //get service
            musicSrv = binder.getService();


            MainActivity.changeFavouriteIcon();
            //pass list
            musicBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            musicBound = false;
        }
    };

    @Override
    public void onStart() {
        super.onStart();
        if(playIntent==null){
            playIntent = new Intent(activity, MusicService.class);
            activity.bindService(playIntent, musicConnection, Context.BIND_AUTO_CREATE);
            activity.startService(playIntent);
        }
    }
    */

}
