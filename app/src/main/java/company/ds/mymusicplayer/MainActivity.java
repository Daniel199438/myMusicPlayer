package company.ds.mymusicplayer;

import android.app.Activity;
import android.app.SearchManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.design.widget.TabLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.mpatric.mp3agic.InvalidDataException;
import com.mpatric.mp3agic.Mp3File;
import com.mpatric.mp3agic.UnsupportedTagException;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import company.ds.mymusicplayer.Adapter.SongListAdapter;
import company.ds.mymusicplayer.Fragments.FolderList;
import company.ds.mymusicplayer.Fragments.FragmentMain;
import company.ds.mymusicplayer.Fragments.FragmentMainSongList;
import company.ds.mymusicplayer.Fragments.PlaylistList;
import company.ds.mymusicplayer.Fragments.SongList;
import company.ds.mymusicplayer.MySqlDatabases.DataSource;
import company.ds.mymusicplayer.Object.Folder;
import company.ds.mymusicplayer.Object.Playlist;
import company.ds.mymusicplayer.Object.Song;
import company.ds.mymusicplayer.Service.MusicService;
import company.ds.mymusicplayer.helper.Utils;

import static company.ds.mymusicplayer.Fragments.SongListDetail.buildAndRefreshLRCView;

public class MainActivity extends AppCompatActivity {
    ListView listView;
    MediaPlayer mPlayer;
    static SharedPreferences prefs;
    public static MusicService musicSrv;
    private boolean musicBound = false;
    private Intent playIntent;
    static boolean mDragging = false;
    static ImageButton mBackButton;
    static ImageButton mNextButton;
    static ImageButton mPauseButton;
    static ImageButton mRepeatButton;
    public static ImageButton mShuffleButton;
    static SeekBar line;
    static ProgressBar mProgress;
    static TextView mCurrentTime, mEndTime;
    static LinearLayout player_container;
    private static final int SHOW_PROGRESS = 2;
    static MessageHandler mHandler;
    //Drawer Menu Navigation
    private ListView mDrawerList;
    private ArrayAdapter<String> mAdapter;
    private ActionBarDrawerToggle mDrawerToggle;
    private DrawerLayout mDrawerLayout;
    private String mActivityTitle;
    private static DataSource dataSource;
    public static Menu mMenu;
    public static Activity activity = null;
    private HeadSetReceiver myReceiver;
    private static FragmentTransaction ft;
    private static FragmentManager fragmentManager;
    private final static String TAG = "DashBoardActivity";
    private static AlertDialog alert;
    private static Playlist createdPlaylist;
    private static SearchView searchView;
    private static MenuItem searchbtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        fragmentManager = getSupportFragmentManager();
        /*
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        */
        prefs = getSharedPreferences(Config.SEND, 0);
        //mDrawerList = (ListView)findViewById(R.id.navList);
        dataSource = new DataSource(this);
        dataSource.open();
        activity = this;
        myReceiver = new HeadSetReceiver();
        IntentFilter filter = new IntentFilter(Intent.ACTION_HEADSET_PLUG);
        registerReceiver(myReceiver, filter);
        //addDrawerItems();
        //Update Folders at first Start of Application
        //On First Time: Create Favourite Playlist
            if(dataSource.getFavouritePlaylist() == null) {
                Playlist favPlayl = dataSource.createPlaylist("Favoriten", 0, 0);
                ArrayList<Song> favS = dataSource.getAllFavouriteSongs();
                for (int i = 0; i < favS.size(); i++) {
                    dataSource.addSongToPlaylist(favPlayl.getId(), favS.get(i).getId());
                }
            }

        new FolderList.Loader(false).execute();
        //Utils.getAndUpdateFolders(Environment.getExternalStorageDirectory(), Environment.getExternalStorageDirectory().getPath(), activity);
        //new FolderList.Loader(false).execute();

        mBackButton = (ImageButton) findViewById(R.id.prev);
        mNextButton = (ImageButton) findViewById(R.id.next);
        mPauseButton = (ImageButton) findViewById(R.id.pause);
        mRepeatButton = (ImageButton) findViewById(R.id.repeat);
        mShuffleButton = (ImageButton) findViewById(R.id.shuffle);

        //Loads the saved Settings
        if (prefs.getInt(Config.SETTING_REPEAT, 0) == 0) {
            mRepeatButton.setImageResource(R.drawable.ic_action_repeat);
        } else {
            mRepeatButton.setImageResource(R.drawable.ic_action_repeat_one);
        }
        int color = Color.parseColor("#2E9AFE"); //The color u want
        mRepeatButton.setColorFilter(color);

        if (prefs.getBoolean(Config.SETTING_SHUFFLE, false)) {
            mShuffleButton.setColorFilter(color);
        } else {
            mShuffleButton.clearColorFilter();
        }

        mEndTime = (TextView) findViewById(R.id.time);
        mCurrentTime = (TextView) findViewById(R.id.time_current);
        mProgress = (SeekBar) findViewById(R.id.mediacontroller_progress);
        if (mProgress != null) {
            if (mProgress instanceof SeekBar) {
                line = (SeekBar) mProgress;
            }
            mProgress.setMax(1000);
        }
        player_container = (LinearLayout) findViewById(R.id.player_container);
        mHandler = new MessageHandler();

        mRepeatButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (musicSrv == null) {
                    return;
                }

                musicSrv.setRepeat(musicSrv.getRepeat() == 0 ? 1 : 0);

                if (musicSrv.getRepeat() == 0) {
                    mRepeatButton.setImageResource(R.drawable.ic_action_repeat);
                } else {
                    mRepeatButton.setImageResource(R.drawable.ic_action_repeat_one);
                }

                int color = Color.parseColor("#2E9AFE"); //The color u want
                mRepeatButton.setColorFilter(color);

                SharedPreferences.Editor edit = prefs.edit();
                edit.putInt(Config.SETTING_REPEAT, musicSrv.getRepeat());
                edit.commit();

            }
        });

        mShuffleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (musicSrv == null) {
                    return;
                }

                musicSrv.setShuffle(!musicSrv.getShuffle());

                if (musicSrv.getShuffle()) {
                    int color = Color.parseColor("#2E9AFE"); //The color u want
                    mShuffleButton.setColorFilter(color);
                } else {
                    mShuffleButton.clearColorFilter();
                }
                SharedPreferences.Editor edit = prefs.edit();
                edit.putBoolean(Config.SETTING_SHUFFLE, musicSrv.getShuffle());
                edit.commit();

            }
        });


        mPauseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (musicSrv == null) {
                    return;
                }

                if (musicSrv.isPng()) {
                    musicSrv.pausePlayer();
                } else {
                    musicSrv.go();
                    if (musicSrv != null && mHandler != null) {
                        mHandler.sendEmptyMessage(SHOW_PROGRESS);
                    }
                }
            }
        });

        mNextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                musicSrv.playNext();
            }
        });
        mBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (musicSrv.getPosn() > 10000) {
                    musicSrv.repeatSong();
                } else {
                    musicSrv.playPrev();
                }
            }
        });

        line.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            public void onStartTrackingTouch(SeekBar bar) {

                mDragging = true;

                // By removing these pending progress messages we make sure
                // that a) we won't update the progress while the user adjusts
                // the seekbar and b) once the user is done dragging the thumb
                // we will post one of these messages to the queue again and
                // this ensures that there will be exactly one message queued up.
                mHandler.removeMessages(SHOW_PROGRESS);
            }

            public void onProgressChanged(SeekBar bar, int progress, boolean fromuser) {
                if (musicSrv == null) {
                    return;
                }
                if (!fromuser) {
                    // We're not interested in programmatically generated changes to
                    // the progress bar's position.
                    return;
                }

                long duration = musicSrv.getDur();
                long newposition = (duration * progress) / 1000L;
                musicSrv.seek((int) newposition);
                if (mCurrentTime != null)
                    mCurrentTime.setText(Utils.stringForTime((int) newposition));
            }

            public void onStopTrackingTouch(SeekBar bar) {
                mDragging = false;
                setProgress();
                updatePausePlay();

                // Ensure that progress is properly updated in the future,
                // the call to show() does not guarantee this because it is a
                // no-op if we are already showing.
                mHandler.sendEmptyMessage(SHOW_PROGRESS);
            }
        });

        MainActivity.setNewFragment(1);

        /*
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        */
    }

    public static Activity getActivity() {
        return activity;
    }

    public static void setNewFragment(int c) {
        Fragment reg;
        String tag = "";
        switch (c) {
            case 0:
                reg = new FragmentMainSongList();
                tag = "songlist";
                break;
            case 1:
                reg = new FragmentMain();
                tag = "fragmentmain";
                break;
            default:
                reg = new FragmentMain();
                tag = "fragmentmain";
        }
        ft = fragmentManager.beginTransaction();
        ft.replace(R.id.content_frame, reg, tag);
        ft.addToBackStack(null);

        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        ft.commit();
    }

    public void onBackPressed() {
        FragmentMain myFragment = (FragmentMain) fragmentManager.findFragmentByTag("fragmentmain");
        FragmentMainSongList fragmentSong = (FragmentMainSongList) fragmentManager.findFragmentByTag("songlist");
        if (myFragment != null && myFragment.isVisible()) {
            //if myFragment is visible when the back button is pressed...
            moveTaskToBack(true);
        } else if (fragmentSong != null && fragmentSong.isVisible()) {
            String songListType = prefs.getString(Config.SONGLISTTYPE_VISITED, "");
            if (songListType.equals(Config.TYPE_ALLSONGS_SELECTION_ADD)) {
                SharedPreferences.Editor edit1 = prefs.edit();
                edit1.putString(Config.SONGLISTTYPE_VISITED, Config.TYPE_PLAYLIST);
                edit1.commit();
                setNewFragment(0);
            } else if (SongList.isDeleteRequired()) {
                SongList.setDeleteRequired(!SongList.isDeleteRequired());
            } else {
                setNewFragment(1);
            }
        } else {
            if (fragmentManager.getBackStackEntryCount() > 0) {
                Log.i("MainActivity", "popping backstack");
                fragmentManager.popBackStack();
            } else {
                Log.i("MainActivity", "nothing on backstack, calling super");
                super.onBackPressed();
            }
        }
    }

    public static void closeApplication() {
        activity.finish();
    }

    private class HeadSetReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(Intent.ACTION_HEADSET_PLUG)) {
                int state = intent.getIntExtra("state", -1);
                switch (state) {
                    case 0:
                        //Unplugged
                        if (musicSrv != null && prefs.getBoolean(Config.HEADSET_IS_PLUGGED_IN, false) == true) {
                            if (musicSrv.isPng()) {
                                musicSrv.pausePlayer();
                            }
                        }
                        SharedPreferences.Editor edit = prefs.edit();
                        edit.putBoolean(Config.HEADSET_IS_PLUGGED_IN, false);
                        edit.commit();
                        break;
                    case 1:
                        //plugged
                        SharedPreferences.Editor edit1 = prefs.edit();
                        edit1.putBoolean(Config.HEADSET_IS_PLUGGED_IN, true);
                        edit1.commit();
                        break;
                }
            }
        }
    }


    //connect to the service
    private ServiceConnection musicConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            MusicService.MusicBinder binder = (MusicService.MusicBinder) service;
            //get service
            musicSrv = binder.getService();
            //pass list
            musicBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            musicBound = false;
        }
    };


    public static void resetPlayer() {
        mHandler.removeMessages(SHOW_PROGRESS);
        mHandler = null;
    }

    public static void initPlayer() {
        if (player_container.getVisibility() == View.GONE) {
            player_container.setVisibility(View.VISIBLE);
        }
        //Updates the Progress of the Song
        if (mHandler == null) {
            mHandler = new MessageHandler();
        }
        mHandler.sendEmptyMessage(SHOW_PROGRESS);
    }


    public static void updatePausePlay() {
        if (mPauseButton == null || musicSrv == null) {
            return;
        }

        if (musicSrv.isPng()) {
            mPauseButton.setImageResource(R.drawable.ic_action_pause);
        } else {
            mPauseButton.setImageResource(R.drawable.ic_action_play);
        }
    }

    public static void updateStatistics(Song song) {
        Utils.incrementStatistics(song, activity);
    }

    public static Menu getMenu() {
        return mMenu;
    }

    public static MusicService getMusicSrv() {
        return musicSrv;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        mMenu = menu;
        mMenu.findItem(R.id.favourite_btn).setVisible(false);
        mMenu.findItem(R.id.createPL_btn).setVisible(false);
        mMenu.findItem(R.id.deletePL_btn).setVisible(false);
        mMenu.findItem(R.id.checkPL_btn).setVisible(false);
        mMenu.findItem(R.id.editPL_btn).setVisible(false);
        mMenu.findItem(R.id.synch_btn).setVisible(false);
        mMenu.findItem(R.id.search_btn).setVisible(false);
        mMenu.findItem(R.id.addSong_btn).setVisible(false);
        mMenu.findItem(R.id.finishaddSong_btn).setVisible(false);

        SearchManager searchManager = (SearchManager)
                getSystemService(Context.SEARCH_SERVICE);
        MenuItem searchbtn = mMenu.findItem(R.id.search_btn);
        searchView = (SearchView) searchbtn.getActionView();

        searchView.setSearchableInfo(searchManager.
                getSearchableInfo(getComponentName()));
        searchView.setSubmitButtonEnabled(true);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                /*
                if (TextUtils.isEmpty(newText)) {
                    SongList.clearTextFilter();
                } else {
                    SongList.filterText(newText);
                }
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        SongList.clearTextFilter();
                    }
                }, 2000);
                */
                SongList.filterText(newText);

                return true;
            }
        });

        //


        return true;
    }

    public static void hideSearchView() {
        if (searchView != null) {
            if(searchbtn != null){
                searchbtn.collapseActionView();
            }
                searchView.onActionViewCollapsed();
                searchView.setQuery("", false);
        }
    }

    public static void changeFavouriteIcon() {
        if (mMenu != null && musicSrv != null && musicSrv.getPathFilefromActSong() != null && !musicSrv.getPathFilefromActSong().equals("")) {
            Song song = dataSource.getSong(musicSrv.getPathFilefromActSong());
            mMenu.findItem(R.id.favourite_btn).setVisible(true);
            mMenu.findItem(R.id.favourite_btn).setIcon(song.isFavourite() ? R.drawable.ic_action_important : R.drawable.ic_action_not_important);
        }
    }

    public static void changeFavouriteIcon(Menu mMenu) {
        if (mMenu != null && musicSrv != null && musicSrv.getPathFilefromActSong() != null && !musicSrv.getPathFilefromActSong().equals("")) {
            Song song = dataSource.getSong(musicSrv.getPathFilefromActSong());
            mMenu.findItem(R.id.favourite_btn).setVisible(true);
            mMenu.findItem(R.id.favourite_btn).setIcon(song.isFavourite() ? R.drawable.ic_action_important : R.drawable.ic_action_not_important);
        }
    }



    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        //menu item selected
        switch (item.getItemId()) {
            case R.id.favourite_btn:
                if (mMenu != null && musicSrv.getPathFilefromActSong() != null && !musicSrv.getPathFilefromActSong().equals("")) {
                    Song song = dataSource.getSong(musicSrv.getPathFilefromActSong());
                    song.setIsFavourite(!song.isFavourite());
                    dataSource.updateIsFavourite(song);
                    Playlist playFav = dataSource.getFavouritePlaylist();
                    if(song.isFavourite()){
                        dataSource.addSongToPlaylist(playFav.getId(), song.getId());
                    }
                    else {
                        dataSource.deleteSongFromPlaylist(playFav.getId(), song.getId());
                    }
                    mMenu.findItem(R.id.favourite_btn).setIcon(song.isFavourite() ? R.drawable.ic_action_important : R.drawable.ic_action_not_important);
                }
                return true;

            case R.id.synch_btn:
                if (mMenu != null) {
                    new FolderList.Loader(true).execute();
                }
                return true;

            case R.id.createPL_btn:
                //create Playlist
                createAlertDialog(0);
                return true;
            case R.id.deletePL_btn:
                //delete Playlist
                PlaylistList.setDeleteRequired(!PlaylistList.isDeleteRequired());
                return true;
            case R.id.editPL_btn:
                //edit Playlist
                SongList.setDeleteRequired(!SongList.isDeleteRequired());
                return true;
            case R.id.checkPL_btn:
                //admit changes of Playlist
                if (createdPlaylist != null) {
                    Utils.addSongsToPlaylist(activity, createdPlaylist, SongList.getSelectedSongs());
                    MainActivity.setNewFragment(1);
                }
                return true;
            case R.id.search_btn:
                //filter songs/search for song
                return true;
            case R.id.addSong_btn:
                SharedPreferences.Editor edit = prefs.edit();
                edit.putString(Config.SONGLISTTYPE_VISITED, Config.TYPE_ALLSONGS_SELECTION_ADD);
                edit.commit();
                MainActivity.setNewFragment(0);
                //createdPlaylist = prefs.getLong(Config.VISITED_PARENT_ID, -1);
                return true;
            case R.id.finishaddSong_btn:
                createdPlaylist = dataSource.getPlaylist(prefs.getLong(Config.VISITED_PARENT_ID, -1));
                Utils.addSongsToPlaylist(activity, createdPlaylist, SongList.getSelectedSongs());
                SharedPreferences.Editor edit1 = prefs.edit();
                edit1.putString(Config.SONGLISTTYPE_VISITED, Config.TYPE_PLAYLIST);
                edit1.commit();
                MainActivity.setNewFragment(0);
                return true;
            /*
            case R.id.action_shuffle:
                musicSrv.setShuffle();
                break;
            case R.id.action_end:
                stopService(playIntent);
                musicSrv=null;
                System.exit(0);
                break;
                */
        }

        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public static void createAlertDialog(int type) {
        switch (type) {
            case 0:
                final EditText et = new EditText(activity);
                final AlertDialog.Builder builder = new AlertDialog.Builder(activity);
                builder.setView(et)
                        .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                String input = et.getText().toString();
                                ArrayList<Playlist> playlists = dataSource.getAllPlaylists();
                                boolean nameEqual = false;
                                for (int i = 0; i < playlists.size(); i++) {
                                    if (input.equals(playlists.get(i).getName())) {
                                        nameEqual = true;
                                    }
                                }
                                if(nameEqual){
                                    Toast.makeText(activity, "Playlist Name schon vergeben!", Toast.LENGTH_LONG).show();
                                }
                                else {
                                        createdPlaylist = dataSource.createPlaylist(input, 0, 0);

                                        SharedPreferences.Editor edit = prefs.edit();
                                        edit.putString(Config.SONGLISTTYPE_VISITED, Config.TYPE_ALLSONGS_SELECTION);
                                        edit.commit();
                                        MainActivity.setNewFragment(0);
                                        Utils.hideSoftKeyboard(activity);
                                    }

                            }
                        });

                builder.setNegativeButton("Abbrechen", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        alert.dismiss();
                    }
                });
                alert = builder.create();
                alert.show();
                break;
            case 1:
                final AlertDialog.Builder builder2 = new AlertDialog.Builder(activity);
                builder2.setTitle("Playlist löschen");
                builder2.setMessage("Wollen Sie die Playlist wirklich löschen ?");
                builder2.setPositiveButton("Ja", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        PlaylistList.deletePlaylist();
                    }
                });

                builder2.setNegativeButton("Abbrechen", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        alert.dismiss();
                    }
                });
                alert = builder2.create();
                alert.show();
                break;
            case 2:
                final AlertDialog.Builder builder3 = new AlertDialog.Builder(activity);
                builder3.setTitle("Song von Playlist entfernen");
                builder3.setMessage("Wollen Sie den Song wirklich von der Playlist entfernen ?");
                builder3.setPositiveButton("Ja", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        SongList.deleteSong();
                    }
                });

                builder3.setNegativeButton("Abbrechen", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        alert.dismiss();
                    }
                });
                alert = builder3.create();
                alert.show();
                break;
        }

    }

    @Override
    public void onStart() {
        super.onStart();
        if (playIntent == null) {
            playIntent = new Intent(this, MusicService.class);
            bindService(playIntent, musicConnection, Context.BIND_AUTO_CREATE);
            startService(playIntent);
        }
    }

    @Override
    public void onDestroy() {
        stopService(playIntent);
        musicSrv = null;
        super.onDestroy();
    }

    private static int setProgress() {
        if (musicSrv == null || mDragging) {
            return 0;
        }

        int position = musicSrv.getPosn();
        int duration = musicSrv.getDur();
        if (mProgress != null) {
            if (duration > 0) {
                // use long to avoid overflow
                long pos = 1000L * position / duration;
                mProgress.setProgress((int) pos);
            }
        }

        if (mEndTime != null)
            mEndTime.setText(Utils.stringForTime(duration));
        if (mCurrentTime != null)
            mCurrentTime.setText(Utils.stringForTime(position));

        return position;
    }


    private static class MessageHandler extends Handler {
        int oldPos = 0;

        MessageHandler() {
        }

        @Override
        public void handleMessage(Message msg) {
            if (musicSrv == null) {
                return;
            }

            int pos;

            switch (msg.what) {
                case SHOW_PROGRESS:
                    pos = setProgress();
                    if (!mDragging && musicSrv.isPng()) {
                        //Update Duration for Statistics
                        dataSource.updateSumDuration(pos - oldPos);
                        oldPos = pos;
                        if (pos > 10000) {
                            mBackButton.setImageResource(R.drawable.ic_action_replay);
                        } else {
                            mBackButton.setImageResource(R.drawable.ic_action_previous);
                        }
                        msg = obtainMessage(SHOW_PROGRESS);
                        sendMessageDelayed(msg, 1000 - (pos % 1000));
                    }
                    break;
            }
        }

    }


}
