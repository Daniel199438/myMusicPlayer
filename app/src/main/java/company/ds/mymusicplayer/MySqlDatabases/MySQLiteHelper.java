package company.ds.mymusicplayer.MySqlDatabases;

/**
 * Created by DS on 05.04.2016.
 */
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class MySQLiteHelper extends SQLiteOpenHelper {
    public static final String DATABASE_NAME = "data.db";
    private static final int DATABASE_VERSION = 3;


    public static final String TABLE_FOLDERS = "folders";
    public static final String TABLE_FOLDERS_COLUMN_ID = "_id";
    public static final String TABLE_FOLDERS_COLUMN_NAME = "name";
    public static final String TABLE_FOLDERS_COLUMN_PATH = "path";
    public static final String TABLE_FOLDERS_COLUMN_SONGSIN = "songsin";
    public static final String TABLE_FOLDERS_COLUMN_DURATION = "duration";
    public static final String TABLE_FOLDERS_COLUMN_FOLDER_CLICK = "folderclick";

    public static final String TABLE_SONGS = "songs";
    public static final String TABLE_SONGS_COLUMN_ID = "_id";
    public static final String TABLE_SONGS_COLUMN_FILETITLE = "filetitle";
    public static final String TABLE_SONGS_COLUMN_SONGTITLE = "songtitle";
    public static final String TABLE_SONGS_COLUMN_AUTHOR = "author";
    public static final String TABLE_SONGS_COLUMN_PATH = "path";
    public static final String TABLE_SONGS_COLUMN_PATH_FILE = "pathfile";
    public static final String TABLE_SONGS_COLUMN_ALBUM_NAME = "albumname";
    public static final String TABLE_SONGS_COLUMN_ALBUM_PIC = "albumpic";
    public static final String TABLE_SONGS_COLUMN_IS_FAVOURITE = "favourite";
    public static final String TABLE_SONGS_COLUMN_SONG_CLICK = "songclick";
    public static final String TABLE_SONGS_COLUMN_DURATION = "duration";
    public static final String TABLE_SONGS_COLUMN_SONGLYRICS = "songlyrics";

    public static final String TABLE_PLAYLISTS = "playlist";
    public static final String TABLE_PLAYLISTS_COLUMN_ID = "_id";
    public static final String TABLE_PLAYLISTS_COLUMN_NAME = "name";
    public static final String TABLE_PLAYLISTS_COLUMN_SONGSIN = "songsin";
    public static final String TABLE_PLAYLISTS_COLUMN_DURATION = "duration";

    public static final String TABLE_PLAYLISTS_SONGS = "playlist_songs";
    public static final String TABLE_PLAYLISTS_SONGS_COLUMN_ID = "_id";
    public static final String TABLE_PLAYLISTS_SONGS_COLUMN_PLAYLIST_ID = "playlistID";
    public static final String TABLE_PLAYLISTS_SONGS_COLUMN_SONG_ID = "songID";

    public static final String TABLE_STATISTICS = "statistics";
    public static final String TABLE_STATISTICS_COLUMN_ID = "_id";
    public static final String TABLE_STATISTICS_COLUMN_NAME = "name";
    public static final String TABLE_STATISTICS_COLUMN_VALUE = "value";



    // Database creation sql statement
    private static final String CREATE_TABLE_FOLDERS = "create table "
            + TABLE_FOLDERS + "(" + TABLE_FOLDERS_COLUMN_ID
            + " integer primary key autoincrement, " + TABLE_FOLDERS_COLUMN_NAME
            + " text not null, " + TABLE_FOLDERS_COLUMN_PATH + " text not null unique, " + TABLE_FOLDERS_COLUMN_SONGSIN + " integer, " + TABLE_FOLDERS_COLUMN_DURATION + " integer, " + TABLE_FOLDERS_COLUMN_FOLDER_CLICK + " integer " + ");";
    private static final String CREATE_TABLE_SONGS = "create table " + TABLE_SONGS + "("+ TABLE_SONGS_COLUMN_ID + " integer primary key autoincrement, " + TABLE_SONGS_COLUMN_FILETITLE + " text not null, "  + TABLE_SONGS_COLUMN_SONGTITLE +
            " text, " + TABLE_SONGS_COLUMN_AUTHOR  + " text, " + TABLE_SONGS_COLUMN_PATH  + " text not null, " + TABLE_SONGS_COLUMN_PATH_FILE + " text not null unique, " + TABLE_SONGS_COLUMN_ALBUM_NAME + " text, " + TABLE_SONGS_COLUMN_ALBUM_PIC +
            " text, " + TABLE_SONGS_COLUMN_IS_FAVOURITE + " integer, " + TABLE_SONGS_COLUMN_SONG_CLICK + " integer, " + TABLE_SONGS_COLUMN_DURATION + " integer, " + TABLE_SONGS_COLUMN_SONGLYRICS + " text " + ");";
    private static final String CREATE_TABLE_PLAYLISTS = "create table "
            + TABLE_PLAYLISTS + "(" + TABLE_PLAYLISTS_COLUMN_ID
            + " integer primary key autoincrement, " + TABLE_PLAYLISTS_COLUMN_NAME
            + " text not null unique, " + TABLE_PLAYLISTS_COLUMN_SONGSIN + " integer, "
            + TABLE_PLAYLISTS_COLUMN_DURATION + " integer " + ");";
    private static final String CREATE_TABLE_PLAYLISTS_SONGS = "create table "
            + TABLE_PLAYLISTS_SONGS + "(" + TABLE_PLAYLISTS_SONGS_COLUMN_ID
            + " integer primary key autoincrement, " + TABLE_PLAYLISTS_SONGS_COLUMN_PLAYLIST_ID
            + " integer, " + TABLE_PLAYLISTS_SONGS_COLUMN_SONG_ID + " integer " + ");";
    private static final String CREATE_TABLE_STATISTICS = "create table "
            + TABLE_STATISTICS + "(" + TABLE_STATISTICS_COLUMN_ID
            + " integer primary key autoincrement, " + TABLE_STATISTICS_COLUMN_NAME
            + " text not null unique, " + TABLE_STATISTICS_COLUMN_VALUE + " integer " + ");";

    public MySQLiteHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase database) {
        database.execSQL(CREATE_TABLE_FOLDERS);
        database.execSQL(CREATE_TABLE_SONGS);
        database.execSQL(CREATE_TABLE_STATISTICS);
        database.execSQL(CREATE_TABLE_PLAYLISTS);
        database.execSQL(CREATE_TABLE_PLAYLISTS_SONGS);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        switch(oldVersion) {
            case 1:
                //db.execSQL(CREATE_TABLE_EVENTS);

                // we want both updates, so no break statement here...
            case 2:
                db.execSQL("ALTER TABLE " + TABLE_SONGS + " ADD COLUMN " + TABLE_SONGS_COLUMN_SONGLYRICS + " TEXT");

            case 3:

        }
        //onCreate(db);
    }


}
