package company.ds.mymusicplayer.Fragments;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import company.ds.mymusicplayer.Adapter.SelectionListAdapter;
import company.ds.mymusicplayer.Adapter.SongListAdapter;
import company.ds.mymusicplayer.Config;
import company.ds.mymusicplayer.MainActivity;
import company.ds.mymusicplayer.MySqlDatabases.DataSource;
import company.ds.mymusicplayer.Object.Playlist;
import company.ds.mymusicplayer.Object.Song;
import company.ds.mymusicplayer.R;
import company.ds.mymusicplayer.Service.MusicService;
import company.ds.mymusicplayer.helper.Utils;
import jaudiotagger.org.jaudiotagger.audio.exceptions.CannotReadException;
import jaudiotagger.org.jaudiotagger.audio.exceptions.InvalidAudioFrameException;
import jaudiotagger.org.jaudiotagger.audio.exceptions.ReadOnlyFileException;
import jaudiotagger.org.jaudiotagger.tag.TagException;

/**
 * Created by DS on 02.04.2016.
 */
public class SongList extends Fragment {
    private static ListView listView;
    private static SongListAdapter adapterSongList;
    private static SelectionListAdapter adapterSelectionList;
    private static SharedPreferences prefs;
    private static ArrayList<Song> songs;
    private boolean musicBound = false;
    private Intent playIntent;
    private MusicService musicSrv;
    View view;
    static Activity activity;
    static DataSource dataSource;
    String path;
    private Menu mMenu;
    long parentID;
    String songListType;
    private TextView empty;
    public static ArrayList<Long> selectedSongs;
    public static ArrayList<Song> filteredSongs;

    /**
     * Returns a new instance of this fragment for the given section
     * number.
     */
    public static SongList newInstance(String text) {
        SongList fragment = new SongList();
        Bundle args = new Bundle();
        if(text.equals(Config.TYPE_ALLSONGS)){
            args.putInt(Config.ATYPE, 0);
        }
        else {
            args.putInt(Config.ATYPE, 1);
        }
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Bundle args = getArguments();
        activity = getActivity();
        musicSrv = MainActivity.getMusicSrv();
        prefs = activity.getSharedPreferences(Config.SEND, 0);
        setHasOptionsMenu(true);
        int caseT = args.getInt(Config.ATYPE);
        if (caseT == 1) {
            view = inflater.inflate(R.layout.list_fragment, container, false);
            /*
            Toolbar toolbar = (Toolbar) view.findViewById(R.id.toolbar);
            ((AppCompatActivity) activity).setSupportActionBar(toolbar);
            */
            System.out.println("TEST: " + args.toString());
        } else {
            //System.out.println("Hallo: " + );
            view = inflater.inflate(R.layout.list_fragment, container, false);
            SharedPreferences.Editor edit = prefs.edit();
            edit.putString(Config.SONGLISTTYPE_VISITED, Config.TYPE_ALLSONGS);
            edit.commit();

        }

        songListType = prefs.getString(Config.SONGLISTTYPE_VISITED, "");
        listView = (ListView) view.findViewById(R.id.list);
        empty = (TextView) view.findViewById(R.id.empty);
        listView.setSmoothScrollbarEnabled(false);
        listView.setTextFilterEnabled(true);
        dataSource = new DataSource(getActivity());
        songs = new ArrayList<>();
        filteredSongs = new ArrayList<>();

        parentID = prefs.getLong(Config.VISITED_PARENT_ID, -2);

        if (songListType.equals(Config.TYPE_FAVOURITE_PLAYLIST) || songListType.equals(Config.TYPE_FAVOURITE_FOLDER)) {
            songs = dataSource.getAllFavouriteSongs();
        } else if (songListType.equals(Config.TYPE_PLAYLIST) || songListType.equals(Config.TYPE_FOLDER)) {
            if (songListType.equals(Config.TYPE_FOLDER)) {
                new SongUpdater().execute();
                path = prefs.getString(Config.PATH, "");
                if (!path.equals("")) {
                    songs = dataSource.getAllSongsFromFolder(path);
                }


            } else if (songListType.equals(Config.TYPE_PLAYLIST)) {
                if (parentID > -1) {
                    songs = dataSource.getAllSongsFromPlaylist(parentID);
                }
            }
        } else if (songListType.equals(Config.TYPE_ALLSONGS) || songListType.equals(Config.TYPE_ALLSONGS_SELECTION) || songListType.equals(Config.TYPE_ALLSONGS_SELECTION_ADD)) {
            new SongLoader().execute();
        }

        if (songs.size() == 0) {
            if (songListType.equals(Config.TYPE_FAVOURITE_PLAYLIST) || songListType.equals(Config.TYPE_FAVOURITE_FOLDER)) {
                empty.setText("Es wurden noch keine Favoriten hinzugefügt.");
            } else if (songListType.equals(Config.TYPE_PLAYLIST)) {
                empty.setText("In dieser Playlist gibt es noch keine Songs.");
            } else if (songListType.equals(Config.TYPE_ALLSONGS) || songListType.equals(Config.TYPE_ALLSONGS_SELECTION) || songListType.equals(Config.TYPE_ALLSONGS_SELECTION_ADD)) {
                empty.setText("Es wurden noch keine Songs initialisiert.");
            }
            empty.setVisibility(View.VISIBLE);
            listView.setVisibility(View.GONE);
        } else {
            if (songListType.equals(Config.TYPE_ALLSONGS_SELECTION) || songListType.equals(Config.TYPE_ALLSONGS_SELECTION_ADD)) {
                adapterSelectionList = new SelectionListAdapter(this.getActivity(), songs);
                listView.setAdapter(adapterSelectionList);
                adapterSelectionList.notifyDataSetChanged();
            } else {
                adapterSongList = new SongListAdapter(this.getActivity(), songs);
                listView.setAdapter(adapterSongList);
                adapterSongList.notifyDataSetChanged();
            }

        }

        registerForContextMenu(listView);
        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {


                return false;
            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                //close SearchView Menu Item if openend
                MainActivity.hideSearchView();
                    if (filteredSongs.size() > 0) {

                        long filteredSongid = filteredSongs.get(position).getId();
                        System.out.println("FilteredSongSize: " + filteredSongs.size());
                        for (int i = 0; i < songs.size(); i++) {
                            if (songs.get(i).getId() == filteredSongid) {
                                playSong(i);
                                Utils.hideSoftKeyboard(activity);
                                break;
                            }
                        }
                    }
                else {
                    playSong(position);
                }
            }
        });


        return view;
    }

    public void playSong(int position) {
        System.out.println("SONG SIZE: " + songs.size());
        Song s = getDetailInformationOfSong(songs.get(position));

        File songFile = new File(s.getPathFile());
        if(musicSrv != null) {
            if (musicSrv.getList() == null) {
                musicSrv.setList(songs);
            } else {
                if (!musicSrv.getList().equals(songs)) {
                    musicSrv.setList(songs);
                    System.out.println("SONG LIST GEUPDATED");
                }
            }

        }

        SharedPreferences.Editor edit = prefs.edit();
        edit.putLong(Config.PLAYED_SONG_ID, songs.get(position).getId());
        edit.putLong(Config.PLAYED_PARENT_ID, parentID);
        edit.putString(Config.SONGLISTTYPE_PLAYED, songListType);
        edit.commit();
        if (songFile.exists()) {
            //start song
            musicSrv.setSong(position);
            musicSrv.playSong();
        } else {
            if (position == 0 && songs.size() > 1) {
                musicSrv.setShuffle(true);
                if (musicSrv.getShuffle()) {
                    int color = Color.parseColor("#2E9AFE"); //The color u want
                    MainActivity.mShuffleButton.setColorFilter(color);
                }
                edit.putBoolean(Config.SETTING_SHUFFLE, musicSrv.getShuffle());
                edit.commit();
                if (musicSrv.getList() == null) {
                    musicSrv.setList(songs);
                }
                musicSrv.playNext();

            } else {
                if (musicSrv != null) {
                    Toast.makeText(activity, "Song kann nicht abgespielt werden...", Toast.LENGTH_SHORT);
                    dataSource.deleteSong(songs.get(position));
                    songs.remove(songs.get(position));
                    musicSrv.setList(songs);
                    musicSrv.resetMusicPlayer();
                    musicSrv.playNext();
                }
            }
        }
    }


    public static ArrayList<Long> getSelectedSongs() {
        return SelectionListAdapter.getSelectedSongs();
    }

    public static Song getDetailInformationOfSong(Song s) {
        if (s.getPathFile() == "") {
            return dataSource.getSong(s.getId());
        }
        return s;
    }

    public static void clearTextFilter() {
        listView.clearTextFilter();
    }

    public static void filterText(String newText) {
        if (adapterSongList != null) {
            adapterSongList.getFilter().filter(newText);
        }
        if (adapterSelectionList != null) {
            adapterSelectionList.getFilter().filter(newText);
        }
    }

    public static void setFilteredSongs(ArrayList<Song> pfilteredSongs) {
        filteredSongs = pfilteredSongs;
    }

    public static void setFilterText(String newText) {
        listView.setFilterText(newText.toString());
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        //menu.clear();
        //inflater.inflate(R.menu.menu_main, menu);
        mMenu = menu;

        mMenu.findItem(R.id.search_btn).setVisible(true);
        if (songListType.equals(Config.TYPE_PLAYLIST)) {
            mMenu.findItem(R.id.editPL_btn).setVisible(true);
            mMenu.findItem(R.id.addSong_btn).setVisible(true);
        }
        if (songListType.equals(Config.TYPE_ALLSONGS_SELECTION)) {
            mMenu.findItem(R.id.checkPL_btn).setVisible(true);
        } else if (songListType.equals(Config.TYPE_ALLSONGS_SELECTION_ADD)) {
            mMenu.findItem(R.id.finishaddSong_btn).setVisible(true);
        }
        MainActivity.changeFavouriteIcon(mMenu);
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v,
                                    ContextMenu.ContextMenuInfo menuInfo) {
        if (v.getId() == R.id.list) {
            AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) menuInfo;
            menu.setHeaderTitle(getResources().getString(R.string.menu_header));
            String[] menuItems = getResources().getStringArray(R.array.menu_Items);
            for (int i = 0; i < menuItems.length; i++) {
                menu.add(Menu.NONE, i, i, menuItems[i]);
            }
        }
    }


    @Override
    public boolean onContextItemSelected(MenuItem item) {
        final AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        int menuItemIndex = item.getItemId();
        String[] menuItems = getResources().getStringArray(R.array.menu_Items);
        String menuItemName = menuItems[menuItemIndex];
        //delete

        if (menuItemName.equals(menuItems[0])) {
            File song = new File(songs.get(info.position).getPathFile());
            if (!song.exists()) {
                DataSource dataSource = new DataSource(activity);
                //Utils.deleteFile(songs.get(info.position).getPath(), songs.get(info.position).getFileTitle(), activity);
                songs.remove(songs.get(info.position));
                if (musicSrv != null) {
                    if (musicSrv.getActSong().equals(songs.get(info.position))) {
                        musicSrv.resetMusicPlayer();
                        musicSrv.setList(songs);
                        musicSrv.playNext();
                    }
                }
                dataSource.deleteSong(songs.get(info.position));
                adapterSongList.notifyDataSetChanged();
            }
        }
        //rename
        else if (menuItemName.equals(menuItems[1])) {
            final File song = new File(songs.get(info.position).getPathFile());
            if (song != null) {
                final AlertDialog.Builder alert = new AlertDialog.Builder(activity);
                alert.setTitle(getResources().getString(R.string.filename_rename));
                final EditText edittext = new EditText(activity);
                edittext.setText(songs.get(info.position).getFileTitle());

                alert.setView(edittext);
                // alert.setMessage("Message");

                alert.setPositiveButton("Umbenennen", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        //Your action here
                        String newFileName = edittext.getText().toString();
                        File newFile = new File(songs.get(info.position).getPathFile(), newFileName);
                        song.renameTo(newFile);
                        songs.get(info.position).setFileTitle(newFileName);
                        dialog.dismiss();
                    }
                });

                alert.setNegativeButton("Abbrechen",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                dialog.dismiss();

                            }
                        });

                alert.show();
            }
        }
        //favourite
        else if (menuItemName.equals(menuItems[2])) {
            if (songs.get(info.position).getPathFile() != null && !songs.get(info.position).getPathFile().equals("")) {
                Song song = dataSource.getSong(songs.get(info.position).getPathFile());
                song.setIsFavourite(!song.isFavourite());
                dataSource.updateIsFavourite(song);
                Playlist playFav = dataSource.getFavouritePlaylist();
                if(song.isFavourite()){
                    dataSource.addSongToPlaylist(playFav.getId(), song.getId());
                }
                else {
                    dataSource.deleteSongFromPlaylist(playFav.getId(), song.getId());
                }
                songs.get(info.position).setIsFavourite(!songs.get(info.position).isFavourite());
                MainActivity.changeFavouriteIcon();
            }
        }

        return true;
    }

    public static long getdeletedSongID() {
        if (adapterSongList != null) {
            return adapterSongList.getdeletedSongID();
        }
        return 0;
    }

    public static void deleteSong() {
        if (adapterSongList != null) {
            long sID = getdeletedSongID();
            for (int i = 0; i < songs.size(); i++) {
                if (songs.get(i).getId() == sID) {
                    songs.remove(i);
                }
            }

            dataSource.deleteSongFromPlaylist(prefs.getLong(Config.VISITED_PARENT_ID, -1), sID);
        }
        adapterSongList.notifyDataSetChanged();
    }

    public static boolean isDeleteRequired() {
        if (adapterSongList != null) {
            return adapterSongList.isDeleteRequired();
        }
        return false;
    }

    public static void setDeleteRequired(boolean deleteRequired) {
        if (adapterSongList != null) {
            adapterSongList.setisDeleteRequired(deleteRequired);
            adapterSongList.notifyDataSetChanged();
        }
    }

    /*
    //connect to the service
    private ServiceConnection musicConnection = new ServiceConnection(){

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            MusicBinder binder = (MusicBinder)service;
            //get service
            MainActivity.musicSrv = binder.getService();

            if(prefs.getString(Config.ACT_SONG_PATH, "").equals(prefs.getString(Config.ACT_FOLDER_PATH, "")) && !prefs.getString(Config.ACT_SONG_PATH, "").equals("") && !prefs.getString(Config.ACT_FOLDER_PATH, "").equals("")) {
                if(songs != null) {
                    songs.get(MainActivity.musicSrv.getSongPosn()).setSelectedSong(true);
                    if (adapter != null) {
                        adapter.notifyDataSetChanged();
                        MainActivity.changeFavouriteIcon();
                        listView.setSelection(MainActivity.musicSrv.getSongPosn()-3);
                    }
                }
            }

            MainActivity.changeFavouriteIcon();

            //pass list
            musicBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            musicBound = false;
        }
    };
    */


    public static ArrayList<Song> getSongList() {
        return songs;
    }

    public static void updateAdapter(ArrayList<Song> songs, int pos) {
        adapterSongList = new SongListAdapter(activity, songs);
        listView.setAdapter(adapterSongList);
        adapterSongList.notifyDataSetChanged();
        if (pos != -1) {
            listView.setSelection(pos - 3);
        }

    }
    /*
    @Override
    public void onStart() {
        super.onStart();
        if(playIntent==null){
            playIntent = new Intent(getActivity(), MusicService.class);
            getActivity().bindService(playIntent, musicConnection, Context.BIND_AUTO_CREATE);
            getActivity().startService(playIntent);
        }
    }
    */

    class SongUpdater extends AsyncTask<String, String, String> {

        public SongUpdater() {
            super();
        }

        protected void onPreExecute() {
            SharedPreferences.Editor edit = prefs.edit();
            edit.putLong(Config.VISITED_OLD_PARENT_ID, prefs.getLong(Config.VISITED_PARENT_ID, -2));
            edit.commit();
        }


        @Override
        protected String doInBackground(String... args) {
            try {
                path = prefs.getString(Config.PATH, "");
                songs = Utils.updateSongs(path, activity);
            } catch (ReadOnlyFileException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (TagException e) {
                e.printStackTrace();
            } catch (InvalidAudioFrameException e) {
                e.printStackTrace();
            } catch (CannotReadException e) {
                e.printStackTrace();
            }
            return "";
        }

        protected void onPostExecute(String data) {
            /*
            path = prefs.getString(Config.PATH, "");
            if (!path.equals("")) {
                songs = dataSource.getAllSongsFromFolder(path);
            }
            */
            // save index and top position
            SongList test = (SongList) getFragmentManager().findFragmentByTag("SongList");
            System.out.println(Config.TAG + " VIEWED: " + prefs.getLong(Config.VISITED_PARENT_ID, -2));
            if (prefs.getLong(Config.VISITED_PARENT_ID, -2) == prefs.getLong(Config.VISITED_OLD_PARENT_ID, -2)) {
                int index = listView.getFirstVisiblePosition();
                View v = listView.getChildAt(0);
                int top = (v == null) ? 0 : (v.getTop() - listView.getPaddingTop());

                adapterSongList = new SongListAdapter(activity, songs);
                listView.setAdapter(adapterSongList);
                adapterSongList.notifyDataSetChanged();

                //restore position
                listView.setSelectionFromTop(index, top);

                Toast.makeText(activity, "Songs geupdated!", Toast.LENGTH_SHORT).show();
            }



        }
    }



    class SongLoader extends AsyncTask<String, String, ArrayList<Song>> {

        public SongLoader() {
            super();
        }


        @Override
        protected ArrayList<Song> doInBackground(String... args) {
            if (songListType.equals(Config.TYPE_ALLSONGS_SELECTION_ADD)) {
                songs = dataSource.getAllSongsFilteredFromPlaylist(prefs.getLong(Config.VISITED_PARENT_ID, -1));
            } else {
                songs = dataSource.getAllSongs();
            }
            return songs;
        }

        protected void onPostExecute(ArrayList data) {
            if (songs.size() == 0) {
                if (songListType.equals(Config.TYPE_ALLSONGS) || songListType.equals(Config.TYPE_ALLSONGS_SELECTION) || songListType.equals(Config.TYPE_ALLSONGS_SELECTION_ADD)) {
                    empty.setText("Es wurden noch keine Songs initialisiert.");
                }
                empty.setVisibility(View.VISIBLE);
                listView.setVisibility(View.GONE);
            } else {
                listView.setVisibility(View.VISIBLE);
                empty.setVisibility(View.GONE);
                if (songListType.equals(Config.TYPE_ALLSONGS_SELECTION) || songListType.equals(Config.TYPE_ALLSONGS_SELECTION_ADD)) {
                    adapterSelectionList = new SelectionListAdapter(activity, songs);
                    listView.setAdapter(adapterSelectionList);
                    adapterSelectionList.notifyDataSetChanged();
                } else {
                    adapterSongList = new SongListAdapter(activity, songs);
                    listView.setAdapter(adapterSongList);
                    adapterSongList.notifyDataSetChanged();
                }
            }
        }
    }
}



