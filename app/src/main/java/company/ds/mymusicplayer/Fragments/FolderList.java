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
import android.os.Environment;
import android.os.IBinder;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.TimingLogger;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.mpatric.mp3agic.InvalidDataException;
import com.mpatric.mp3agic.UnsupportedTagException;

import org.farng.mp3.TagException;

import company.ds.mymusicplayer.Adapter.SongListAdapter;
import company.ds.mymusicplayer.Object.Playlist;
import company.ds.mymusicplayer.Object.Song;
import jaudiotagger.org.jaudiotagger.audio.exceptions.CannotReadException;
import jaudiotagger.org.jaudiotagger.audio.exceptions.InvalidAudioFrameException;
import jaudiotagger.org.jaudiotagger.audio.exceptions.ReadOnlyFileException;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import company.ds.mymusicplayer.Adapter.FolderListAdapter;
import company.ds.mymusicplayer.Adapter.PagerAdapter;
import company.ds.mymusicplayer.Config;
import company.ds.mymusicplayer.MainActivity;
import company.ds.mymusicplayer.MySqlDatabases.DataSource;
import company.ds.mymusicplayer.Object.Folder;
import company.ds.mymusicplayer.R;
import company.ds.mymusicplayer.Service.MusicService;
import company.ds.mymusicplayer.helper.Utils;

/**
 * Created by DS on 02.04.2016.
 */
public class FolderList extends Fragment {
    static ListView listView;
    static Activity activity;
    static FolderListAdapter adapter;
    SharedPreferences prefs;
    static ArrayList<Folder> folders;
    static ProgressDialog progress;
    private boolean musicBound = false;
    private Intent playIntent;
    private int fragmentNR;
    private Menu mMenu;
    private static TextView empty;

    /**
     * Returns a new instance of this fragment for the given section
     * number.
     */
    public static FolderList newInstance(String text) {
        FolderList fragment = new FolderList();
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
        final DataSource dataSource = new DataSource(activity);
        folders = dataSource.getAllFolders();
        //new FolderList.Loader(false).execute();
        setHasOptionsMenu(true);

        Playlist favPl = dataSource.getFavouritePlaylist();
        folders.add(0, new Folder("Favoriten", Config.FAVOURITES, favPl.getSongsin(), favPl.getMaxDuration()));
        // folders = Utils.GetAndUpdateFolders(Environment.getExternalStorageDirectory(), Environment.getExternalStorageDirectory().getPath(), activity);
        if (folders.size() == 0) {
            //new Loader(true).execute();
            empty.setText("Es wurden noch keine Ordner gefunden. Bitte klicken Sie auf den Reload-Button oben Rechts!");
            empty.setVisibility(View.VISIBLE);
            listView.setVisibility(View.GONE);
        } else {
            //setFavouriteFolder


            //MainActivity.changeFavouriteIcon();

            long actPlayedFolderID = prefs.getLong(Config.PLAYED_PARENT_ID, -1);
            for (int i = 0; i < folders.size(); i++) {
                folders.get(i).setSelectedFolder(false);
            }
            int folderPosn = -1;
            for (int i = 0; i < folders.size(); i++) {
                if (actPlayedFolderID == folders.get(i).getId()) {
                    folders.get(i).setSelectedFolder(true);
                    folderPosn = i;
                }
            }
            adapter = new FolderListAdapter(activity, folders);
            listView.setAdapter(adapter);
            //listView = (ListView)view.findViewById(R.id.listView);

        }

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                File folderFile = new File(folders.get(position).getPath());
                if(folderFile.exists() || folders.get(position).getPath().equals(Config.FAVOURITES)){


                    SharedPreferences.Editor edit = prefs.edit();
                    //Folder Name and Path
                    //set Playlist to false
                    edit.putString(Config.PATH, folders.get(position).getPath());
                    edit.putLong(Config.VISITED_PARENT_ID, folders.get(position).getId());
                    if (folders.get(position).getPath().equals(Config.FAVOURITES)) {
                        edit.putString(Config.SONGLISTTYPE_VISITED, Config.TYPE_FAVOURITE_FOLDER);
                    } else {
                        edit.putString(Config.SONGLISTTYPE_VISITED, Config.TYPE_FOLDER);
                    }
                    edit.commit();

                    for (int i = 0; i < folders.size(); i++) {
                        System.out.println(Config.TAG + " FOLDERS: " + folders.get(i).getPath());
                    }
                    System.out.println(Config.TAG + " POSITION: " + position);
                    System.out.println(Config.TAG + " IS: " + folders.get(position).getPath());
                    System.out.println(Config.TAG + " SAFED: " + String.valueOf(prefs.getString(Config.PATH, "")));



                    MainActivity.setNewFragment(0);
                }
                else {
                    dataSource.deleteFolder(folders.get(position).getPath());
                    folders.remove(position);
                    adapter.notifyDataSetChanged();
                    Toast.makeText(activity, "Ordner " + folders.get(position).getName() + " existiert nicht mehr", Toast.LENGTH_SHORT).show();
                }
            }
        });

        return view;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        //menu.clear();
        //inflater.inflate(R.menu.menu_main, menu);
        mMenu = menu;
        MainActivity.changeFavouriteIcon(mMenu);
        mMenu.findItem(R.id.synch_btn).setVisible(true);


    }


    public static void updateFolderAdapter(ArrayList<Folder> folders, int pos) {
        adapter = new FolderListAdapter(activity, folders);
        listView.setAdapter(adapter);
        adapter.notifyDataSetChanged();
        if (pos != -1) {
            listView.setSelection(pos - 3);
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

    public static class Loader extends AsyncTask<String, String, ArrayList<Folder>> {
        private boolean showLoading;

        public Loader(boolean showLoading) {
            super();
            this.showLoading = showLoading;
        }


        protected void onPreExecute() {
            if (showLoading) {
                progress = new ProgressDialog(activity);
                progress.setMessage("Durchsucht Ordner ...");
                progress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                progress.setIndeterminate(true);
                progress.show();
            }
        }

        @Override
        protected ArrayList<Folder> doInBackground(String... args) {
                folders = Utils.getAndUpdateFolders(Environment.getExternalStorageDirectory(), Environment.getExternalStorageDirectory().getPath(), activity);
                try {
                    folders = Utils.updateFolderDurationAndCount(activity);
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (jaudiotagger.org.jaudiotagger.tag.TagException e) {
                    e.printStackTrace();
                } catch (CannotReadException e) {
                    e.printStackTrace();
                } catch (InvalidAudioFrameException e) {
                    e.printStackTrace();
                } catch (ReadOnlyFileException e) {
                    e.printStackTrace();
                }
            DataSource dataSource = new DataSource(activity);
            Playlist favPl = dataSource.getFavouritePlaylist();
            folders.add(0, new Folder("Favoriten", Config.FAVOURITES, favPl.getSongsin(), favPl.getMaxDuration()));

            return folders;
        }

        protected void onPostExecute(ArrayList data) {
            if (folders.size() > 0) {
                empty.setVisibility(View.GONE);
                listView.setVisibility(View.VISIBLE);

                // save index and top position
                int index = listView.getFirstVisiblePosition();
                View v = listView.getChildAt(0);
                int top = (v == null) ? 0 : (v.getTop() - listView.getPaddingTop());

                adapter = new FolderListAdapter(activity, folders);
                listView.setAdapter(adapter);
                adapter.notifyDataSetChanged();

                //restore position
                listView.setSelectionFromTop(index, top);


                Toast.makeText(activity, "Ordnerliste geupdated!", Toast.LENGTH_SHORT).show();

                if (SongList.getSongList() != null) {
                    SongList.updateAdapter(SongList.getSongList(), -1);
                }
            }
            if (showLoading) {
                progress.dismiss();
            }

        }
    }

}
