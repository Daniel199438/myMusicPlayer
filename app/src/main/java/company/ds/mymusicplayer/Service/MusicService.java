package company.ds.mymusicplayer.Service;

import android.annotation.TargetApi;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.Build;
import android.os.IBinder;
import android.os.PowerManager;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.RemoteViews;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

import company.ds.mymusicplayer.Config;
import company.ds.mymusicplayer.Fragments.SongList;
import company.ds.mymusicplayer.MainActivity;
import company.ds.mymusicplayer.Object.Song;
import company.ds.mymusicplayer.R;
import company.ds.mymusicplayer.helper.Utils;

import static company.ds.mymusicplayer.Fragments.SongListDetail.buildAndRefreshLRCView;

/**
 * Created by DS on 02.04.2016.
 */
public class MusicService extends Service implements
        MediaPlayer.OnPreparedListener, MediaPlayer.OnErrorListener,
        MediaPlayer.OnCompletionListener {
    //media player
    private MediaPlayer player;
    //song list
    private ArrayList<Song> songs;

    private static final int NOTIFY_ID = 1;
    //current position
    private int songPosn;
    private SharedPreferences prefs;
    private boolean shuffle = true;
    private int repeat = 0; //0 = repeat all, 1 = repeat one
    private Random rand;
    private ArrayList<Integer> playedPlayList;
    private ArrayList<Integer> randomList;
    private int actPlaylistPosn;
    public static NotificationManager mNotificationManager;

    public IBinder musicBind = new MusicBinder();

    public void onCreate() {
        //create the service
        super.onCreate();
        //initialize position
        songPosn = 0;
        actPlaylistPosn = 0;
        rand = new Random();
        playedPlayList = new ArrayList<>();
        randomList = new ArrayList<>();
        prefs = getSharedPreferences(Config.SEND, 0);
        shuffle = prefs.getBoolean(Config.SETTING_SHUFFLE, false);
        repeat = prefs.getInt(Config.SETTING_REPEAT, 0);


        initMusicPlayer();
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        String action = intent.getAction();
        if (action != "") {
            if (Config.ACTION_NEXT.equals(action)) {
                playNext();
            } else if (Config.ACTION_PREVIOUS.equals(action)) {
                playPrev();
            } else if (Config.ACTION_PLAY.equals(action)) {
                if (player.isPlaying()) {
                    pausePlayer();
                } else {
                    go();
                }
                buildAndUpdateNotification();
            } else if (Config.ACTION_REMOVE.equals(action)) {
                mNotificationManager.cancelAll();
                MainActivity.closeApplication();
            }
            MainActivity.updatePausePlay();
        }

        return START_STICKY;
    }

    public void setShuffle(boolean shuffle) {
        this.shuffle = shuffle;
        if (shuffle) {
            if (!randomList.contains(songPosn)) {
                randomList.add(songPosn);
            }
        } else {
            randomList = new ArrayList<>();
        }
    }


    @Override
    public void onCompletion(MediaPlayer mp) {
        MainActivity.updateStatistics(getActSong());
        //check if playback has reached the end of a track
        mp.stop();
        //
        if (player.getCurrentPosition() > 0 && songs.size() > 1) {
            mp.reset();
            playNext();
        }
    }

    public void repeatSong() {
        seek(0);
    }

    public String getPathFilefromActSong() {
        if (songs != null) {
            return songs.get(songPosn).getPathFile();
        }
        return null;
    }


    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        mp.reset();
        return false;
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public void onPrepared(MediaPlayer mp) {
        mp.start();

        buildAndUpdateNotification();


        MainActivity.resetPlayer();
        MainActivity.initPlayer();
        MainActivity.updatePausePlay();
    }

    public void buildAndUpdateNotification() {

        //Intent if notification will be clicked
        Intent notIntent = new Intent(this, MainActivity.class);
        notIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent pendInt = PendingIntent.getActivity(this, 0,
                notIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        //Intent if back button will be clicked
        Intent back_intent = new Intent(this, MusicService.class);
        back_intent.setAction(Config.ACTION_PREVIOUS);
        PendingIntent back_pendInt = PendingIntent.getService(this, 0,
                back_intent, 0);

        //Intent if play/pause button will be clicked
        Intent play_intent = new Intent(this, MusicService.class);
        play_intent.setAction(Config.ACTION_PLAY);
        PendingIntent play_pendInt = PendingIntent.getService(this, 0,
                play_intent, 0);

        //Intent if next button will be clicked
        Intent next_intent = new Intent(this, MusicService.class);
        next_intent.setAction(Config.ACTION_NEXT);
        PendingIntent next_pendInt = PendingIntent.getService(this, 0,
                next_intent, 0);

        //Intent if remove button will be clicked
        Intent remove_intent = new Intent(this, MusicService.class);
        remove_intent.setAction(Config.ACTION_REMOVE);
        PendingIntent remove_pendInt = PendingIntent.getService(this, 0,
                remove_intent, 0);

        Bitmap largeIcon = BitmapFactory.decodeResource(getResources(), R.drawable.note);

        RemoteViews remoteViews = new RemoteViews(getPackageName(),
                R.layout.notification_layout_big);
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(
                this).setSmallIcon(R.mipmap.ic_launcher).setContent(
                remoteViews);
        mBuilder.setContentIntent(pendInt);
        mBuilder.setOngoing(true);
        mBuilder.setPriority(Notification.PRIORITY_MAX);
        mBuilder.setTicker(songs.get(songPosn).getSongTitle() != null && songs.get(songPosn).getAuthor() != null ? songs.get(songPosn).getAuthor() + " - " + songs.get(songPosn).getSongTitle() : songs.get(songPosn).getFileTitle());
        remoteViews.setTextViewText(R.id.not_title, songs.get(songPosn).getSongTitle() != null ? songs.get(songPosn).getSongTitle() : songs.get(songPosn).getFileTitle());
        remoteViews.setTextViewText(R.id.not_author, songs.get(songPosn).getAuthor() != null ? songs.get(songPosn).getAuthor() : "<unknown>");
        remoteViews.setTextViewText(R.id.not_album, songs.get(songPosn).getAlbumname() != null ? songs.get(songPosn).getAlbumname() : "<unknown>");
        remoteViews.setImageViewBitmap(R.id.not_album_pic, songs.get(songPosn).getAlbumPic() != null ? Utils.convertBytetoBitmap(songs.get(songPosn).getAlbumPic()) : Bitmap.createScaledBitmap(largeIcon, 128, 128, false));
        remoteViews.setImageViewResource(R.id.not_pause, isPng() ? R.drawable.ic_action_pause : R.drawable.ic_action_play);
        remoteViews.setOnClickPendingIntent(R.id.not_back, back_pendInt);
        remoteViews.setOnClickPendingIntent(R.id.not_pause, play_pendInt);
        remoteViews.setOnClickPendingIntent(R.id.not_next, next_pendInt);
        remoteViews.setOnClickPendingIntent(R.id.not_remove, remove_pendInt);
        mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        // mId allows you to update the notification later on.
        mNotificationManager.notify(Config.NOTIFICATION_ID, mBuilder.build());

    }


    public void resetMusicPlayer() {
        if (player.isPlaying()) {
            player.stop();
        }
        player.release();
        player = null;
        initMusicPlayer();
    }

    public void initMusicPlayer() {
        //create player
        player = new MediaPlayer();
        //set player properties
        player.setWakeMode(getApplicationContext(),
                PowerManager.PARTIAL_WAKE_LOCK);
        player.setAudioStreamType(AudioManager.STREAM_MUSIC);
        player.setOnPreparedListener(this);
        player.setOnCompletionListener(this);
        player.setOnErrorListener(this);
        player.setLooping(repeat == 1 ? true : false);
    }

    public void setSong(int songIndex) {
        songPosn = songIndex;
        if (!randomList.contains(songPosn)) {
            randomList.add(songPosn);
        }
    }

    public Song getActSong() {
        if(songs != null){
            return songs.get(songPosn);
        }
        else {
            return null;
        }

    }


    public void setList(ArrayList<Song> theSongs) {
        songs = theSongs;
        playedPlayList = new ArrayList<>();
        randomList = new ArrayList<>();
        actPlaylistPosn = 0;
    }

    //binder
    public class MusicBinder extends Binder {
        public MusicService getService() {
            return MusicService.this;
        }
    }

    //activity will bind to service
    @Override
    public IBinder onBind(Intent intent) {
        return musicBind;
    }

    //release resources when unbind
    @Override
    public boolean onUnbind(Intent intent) {
        player.stop();
        player.release();
        return false;
    }

    public int getPosn() {
        if (player != null) {
            return player.getCurrentPosition();
        }
        return 0;
    }

    public int getDur() {
        if (player != null) {
            return player.getDuration();
        }
        return 0;
    }

    public int getRepeat() {
        return repeat;
    }

    public ArrayList<Song> getList() {
        return songs;
    }

    public void setRepeat(int repeat) {
        this.repeat = repeat;
        player.setLooping(repeat == 1 ? true : false);
    }

    public boolean getShuffle() {
        return shuffle;
    }

    public boolean isPng() {
        return player.isPlaying();
    }

    public void pausePlayer() {
        player.pause();
        MainActivity.updatePausePlay();
        buildAndUpdateNotification();
    }

    public void seek(int posn) {
        player.seekTo(posn);
    }

    public void go() {
        player.start();
        MainActivity.updatePausePlay();
        buildAndUpdateNotification();
    }

    public void playPrev() {
        if (playedPlayList.size() == 0 && actPlaylistPosn == 0) {
            songPosn--;
            if (songPosn <= 1) songPosn = songs.size() - 1;
            playSong();
        } else {
            if (actPlaylistPosn > 0) {
                actPlaylistPosn--;
            }
            songPosn = playedPlayList.get(actPlaylistPosn);
            playSong();
        }

    }

    public void playNext() {
        if (songs != null) {
            if (songs.size() > 2) {
                if (shuffle) {
                    int newSong = songPosn;
                    if (randomList.size() >= songs.size()) {
                        randomList = new ArrayList<>();
                        randomList.add(songPosn);
                    }
                    if (!randomList.contains(0)) {
                        randomList.add(0);
                    }
                    if (randomList.size() > 0) {
                        while (randomList.contains(newSong)) {
                            newSong = rand.nextInt(songs.size());
                        }
                    } else {
                        newSong = rand.nextInt(songs.size());
                    }
                    songPosn = newSong;
                    if (!randomList.contains(songPosn)) {
                        randomList.add(songPosn);
                    }
                } else {
                    songPosn++;
                    if (songPosn >= songs.size()) songPosn = 1;
                }
            } else {
                songPosn = 1;

            }
            playSong();
        }
    }

    public int getSongPosn() {
        return songPosn;
    }

    public void setSleepTimer(int hour, int minute) {

    }


    public void setSongPosn(int songPosn) {
        this.songPosn = songPosn;
    }

    public void playSong() {
        //resetMusicPlayer();
        //play a song
        player.reset();

        player.setLooping(repeat == 1 ? true : false);
        //get song
        Song playSong = songs.get(songPosn);
        playSong = SongList.getDetailInformationOfSong(playSong);

        //get id
        for (int i = 0; i < songs.size(); i++) {
            songs.get(i).setSelectedSong(false);
        }

        if (playedPlayList.size() == 0) {
            playedPlayList.add(songPosn);
        } else {
            if (playedPlayList.get(actPlaylistPosn) != songPosn) {
                playedPlayList.add(songPosn);
                actPlaylistPosn++;
            }
        }


        songs.get(songPosn).setSelectedSong(true);
        SharedPreferences.Editor edit = prefs.edit();
        edit.putLong(Config.PLAYED_SONG_ID, playSong.getId());
        edit.commit();


        MainActivity.changeFavouriteIcon();

        if (((prefs.getLong(Config.VISITED_PARENT_ID, -2) == prefs.getLong(Config.PLAYED_PARENT_ID, -2)) &&
                (prefs.getString(Config.SONGLISTTYPE_VISITED, "").equals(prefs.getString(Config.SONGLISTTYPE_PLAYED, "")))) ||
                prefs.getString(Config.SONGLISTTYPE_PLAYED, "").equals(Config.TYPE_ALLSONGS))
        {
            SongList.updateAdapter(songs, songPosn);
            System.out.println("ADAPTER GEUPDATET");
        }

        try

        {
            player.setDataSource(playSong.getPathFile());
        } catch (
                Exception e)

        {
            Log.e("MUSIC SERVICE", "Error setting data source", e);
        }
        try

        {
            player.prepare();
        } catch (
                IOException e)

        {
            e.printStackTrace();
        }

        //refresh Lyrics of Song
        buildAndRefreshLRCView();
    }

    @Override
    public void onDestroy() {
        stopForeground(true);
    }
}

