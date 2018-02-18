package company.ds.mymusicplayer.MySqlDatabases;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;

import company.ds.mymusicplayer.Config;
import company.ds.mymusicplayer.Object.Folder;
import company.ds.mymusicplayer.Object.Playlist;
import company.ds.mymusicplayer.Object.Song;

/**
 * Created by DS on 05.04.2016.
 */
public class DataSource {
    // Database fields
    private static SQLiteDatabase database;
    private static MySQLiteHelper dbHelper;
    private static String[] allColumnsFOLDER = {
            MySQLiteHelper.TABLE_FOLDERS_COLUMN_ID,
            MySQLiteHelper.TABLE_FOLDERS_COLUMN_NAME,
            MySQLiteHelper.TABLE_FOLDERS_COLUMN_PATH,
            MySQLiteHelper.TABLE_FOLDERS_COLUMN_SONGSIN,
            MySQLiteHelper.TABLE_FOLDERS_COLUMN_DURATION,
            MySQLiteHelper.TABLE_FOLDERS_COLUMN_FOLDER_CLICK};

    private static String[] allColumnsSONGS = {
            MySQLiteHelper.TABLE_SONGS_COLUMN_ID,
            MySQLiteHelper.TABLE_SONGS_COLUMN_FILETITLE,
            MySQLiteHelper.TABLE_SONGS_COLUMN_SONGTITLE,
            MySQLiteHelper.TABLE_SONGS_COLUMN_AUTHOR,
            MySQLiteHelper.TABLE_SONGS_COLUMN_PATH,
            MySQLiteHelper.TABLE_SONGS_COLUMN_PATH_FILE,
            MySQLiteHelper.TABLE_SONGS_COLUMN_ALBUM_NAME,
            MySQLiteHelper.TABLE_SONGS_COLUMN_ALBUM_PIC,
            MySQLiteHelper.TABLE_SONGS_COLUMN_IS_FAVOURITE,
            MySQLiteHelper.TABLE_SONGS_COLUMN_SONG_CLICK,
            MySQLiteHelper.TABLE_SONGS_COLUMN_DURATION,
            MySQLiteHelper.TABLE_SONGS_COLUMN_SONGLYRICS};

    private static String[] allColumnsSTATISTICS = {
            MySQLiteHelper.TABLE_STATISTICS_COLUMN_ID,
            MySQLiteHelper.TABLE_STATISTICS_COLUMN_NAME,
            MySQLiteHelper.TABLE_STATISTICS_COLUMN_VALUE};

    private static String[] allColumnsPLAYLISTS = {
            MySQLiteHelper.TABLE_PLAYLISTS_COLUMN_ID,
            MySQLiteHelper.TABLE_PLAYLISTS_COLUMN_NAME,
            MySQLiteHelper.TABLE_PLAYLISTS_COLUMN_SONGSIN,
            MySQLiteHelper.TABLE_PLAYLISTS_COLUMN_DURATION};

    private static String[] allColumnsPLAYLIST_SONGS = {
            MySQLiteHelper.TABLE_PLAYLISTS_SONGS_COLUMN_ID,
            MySQLiteHelper.TABLE_PLAYLISTS_SONGS_COLUMN_PLAYLIST_ID,
            MySQLiteHelper.TABLE_PLAYLISTS_SONGS_COLUMN_SONG_ID};

    public DataSource(Context context) {
        dbHelper = new MySQLiteHelper(context);
    }

    public void open() throws SQLException {
        database = dbHelper.getWritableDatabase();
    }

    public void close() {
        dbHelper.close();
    }

    /*********CREATE DATA*********/

    public Folder createFolder(String name, String path, int songsin, int maxduration) {
        ContentValues values = new ContentValues();
        values.put(MySQLiteHelper.TABLE_FOLDERS_COLUMN_NAME, name);
        values.put(MySQLiteHelper.TABLE_FOLDERS_COLUMN_PATH, path);
        values.put(MySQLiteHelper.TABLE_FOLDERS_COLUMN_SONGSIN, songsin);
        values.put(MySQLiteHelper.TABLE_FOLDERS_COLUMN_DURATION, maxduration);
        values.put(MySQLiteHelper.TABLE_FOLDERS_COLUMN_FOLDER_CLICK, 0);
        long id = database.insertOrThrow(MySQLiteHelper.TABLE_FOLDERS, null,
                values);
        return new Folder(name, path, songsin, maxduration, id);
    }

    public Playlist createPlaylist(String name, int songsin, int maxDuration) {
        ContentValues values = new ContentValues();
        values.put(MySQLiteHelper.TABLE_PLAYLISTS_COLUMN_NAME, name);
        values.put(MySQLiteHelper.TABLE_PLAYLISTS_COLUMN_SONGSIN, songsin);
        values.put(MySQLiteHelper.TABLE_PLAYLISTS_COLUMN_DURATION, maxDuration);
        long id = database.insertOrThrow(MySQLiteHelper.TABLE_PLAYLISTS, null,
                values);
        return new Playlist(name, songsin, maxDuration, id);
    }

    public Song storeSong(Song song) {
        ContentValues values = new ContentValues();
        values.put(MySQLiteHelper.TABLE_SONGS_COLUMN_FILETITLE, song.getFileTitle());
        values.put(MySQLiteHelper.TABLE_SONGS_COLUMN_SONGTITLE, song.getSongTitle());
        values.put(MySQLiteHelper.TABLE_SONGS_COLUMN_AUTHOR, song.getAuthor());
        values.put(MySQLiteHelper.TABLE_SONGS_COLUMN_PATH, song.getPath());
        values.put(MySQLiteHelper.TABLE_SONGS_COLUMN_PATH_FILE, song.getPathFile());
        values.put(MySQLiteHelper.TABLE_SONGS_COLUMN_ALBUM_NAME, song.getAlbumname());
        values.put(MySQLiteHelper.TABLE_SONGS_COLUMN_ALBUM_PIC, song.getAlbumPic());
        values.put(MySQLiteHelper.TABLE_SONGS_COLUMN_IS_FAVOURITE, song.isFavourite() ? 1 : 0);
        values.put(MySQLiteHelper.TABLE_SONGS_COLUMN_SONG_CLICK, 0);
        values.put(MySQLiteHelper.TABLE_SONGS_COLUMN_SONGLYRICS, song.getLyrics());
        values.put(MySQLiteHelper.TABLE_SONGS_COLUMN_DURATION, song.getDuration());
        long id = database.insertOrThrow(MySQLiteHelper.TABLE_SONGS, null,
                values);
        song.setId(id);
        return song;
    }

    /*********CREATE DATA*********/

    /*********DELETE DATA*********/

    public void deleteSong(String pathFile) {
       pathFile = DatabaseUtils.sqlEscapeString(pathFile);
        database.delete(MySQLiteHelper.TABLE_SONGS, MySQLiteHelper.TABLE_SONGS_COLUMN_PATH_FILE
                + "=?", new String[]{pathFile});
    }

    public void deleteSong(Song song) {
        database.delete(MySQLiteHelper.TABLE_SONGS, MySQLiteHelper.TABLE_SONGS_COLUMN_ID
                + "=?", new String[]{String.valueOf(song.getId())});
    }

    public void deleteFolder(String path) {
        path = DatabaseUtils.sqlEscapeString(path);
        database.delete(MySQLiteHelper.TABLE_FOLDERS, MySQLiteHelper.TABLE_FOLDERS_COLUMN_PATH
                + "=?", new String[]{path});
    }

    public void deleteFolder(long folderID) {
        database.delete(MySQLiteHelper.TABLE_FOLDERS, MySQLiteHelper.TABLE_FOLDERS_COLUMN_ID
                + "=?", new String[]{String.valueOf(folderID)});
    }

    public void deletePlaylist(long playlistID) {
        database.delete(MySQLiteHelper.TABLE_PLAYLISTS, MySQLiteHelper.TABLE_PLAYLISTS_COLUMN_ID
                + "=?", new String[]{String.valueOf(playlistID)});
    }

    public void deleteSongFromPlaylist(long playlistID, long songID){
        database.delete(MySQLiteHelper.TABLE_PLAYLISTS_SONGS, MySQLiteHelper.TABLE_PLAYLISTS_SONGS_COLUMN_PLAYLIST_ID + "=? and " + MySQLiteHelper.TABLE_PLAYLISTS_SONGS_COLUMN_SONG_ID + "=?", new String[]{String.valueOf(playlistID), String.valueOf(songID)});
        Playlist playlist = getPlaylist(playlistID);
        Song song = getSong(songID);
        playlist.setMaxDuration(playlist.getMaxDuration() - song.getDuration());
        playlist.setSongsin(playlist.getSongsin()-1);
        updatePlaylistDurationAndCount(playlist);
    }

    /*********DELETE DATA*********/

    /*********GET DATA*********/

    public Song getSong(String pathFile) {
        Cursor cursor = database.rawQuery("SELECT * FROM " + MySQLiteHelper.TABLE_SONGS + " WHERE " + MySQLiteHelper.TABLE_SONGS_COLUMN_PATH_FILE + " = ?", new String[]{pathFile});

        cursor.moveToFirst();
        if( cursor != null && cursor.moveToFirst()) {
            Song song = cursorToSong(cursor);
            cursor.close();
            return song;
        }
        cursor.close();
        return null;
    }

    public Song getSong(long songID) {
        Cursor cursor = database.rawQuery("SELECT * FROM " + MySQLiteHelper.TABLE_SONGS + " WHERE " + MySQLiteHelper.TABLE_SONGS_COLUMN_ID + " = ?", new String[]{String.valueOf(songID)});

        cursor.moveToFirst();
        if( cursor != null && cursor.moveToFirst()) {
            Song song = cursorToSong(cursor);
            cursor.close();
            return song;
        }
        cursor.close();
        return null;
    }

    public boolean isSongInPlaylist(long playlistID, long songID){
        boolean songIsInPlaylist = false;
        Cursor cursor = database.rawQuery("SELECT * FROM " + MySQLiteHelper.TABLE_PLAYLISTS_SONGS + " WHERE " + MySQLiteHelper.TABLE_PLAYLISTS_SONGS_COLUMN_PLAYLIST_ID + " = ? AND " + MySQLiteHelper.TABLE_PLAYLISTS_SONGS_COLUMN_SONG_ID + " = ?" , new String[] {String.valueOf(playlistID), String.valueOf(songID)});
        cursor.moveToFirst();
        if( cursor != null && cursor.moveToFirst()) {
            songIsInPlaylist = true;
            cursor.close();
        }
        cursor.close();
        return songIsInPlaylist;
    }

    public Folder getFolder(String path) {
        Cursor cursor = database.rawQuery("SELECT * FROM " + MySQLiteHelper.TABLE_FOLDERS + " WHERE " + MySQLiteHelper.TABLE_FOLDERS_COLUMN_PATH + " = ?", new String[]{path});

        cursor.moveToFirst();
        if( cursor != null && cursor.moveToFirst()) {
            Folder folder = cursorToFolder(cursor);
            cursor.close();
            return folder;
        }
        cursor.close();
        return null;
    }

    public Folder getFolder(long folderID) {
        Cursor cursor = database.rawQuery("SELECT * FROM " + MySQLiteHelper.TABLE_FOLDERS + " WHERE " + MySQLiteHelper.TABLE_FOLDERS_COLUMN_ID + " = ?", new String[]{String.valueOf(folderID)});

        cursor.moveToFirst();
        if( cursor != null && cursor.moveToFirst()) {
            Folder folder = cursorToFolder(cursor);
            cursor.close();
            return folder;
        }
        cursor.close();
        return null;
    }

    public Playlist getPlaylist(long playlistID) {
        Cursor cursor = database.rawQuery("SELECT * FROM " + MySQLiteHelper.TABLE_PLAYLISTS + " WHERE " + MySQLiteHelper.TABLE_PLAYLISTS_COLUMN_ID + " = ?", new String[]{String.valueOf(playlistID)});

        cursor.moveToFirst();
        if( cursor != null && cursor.moveToFirst()) {
            Playlist playlist = cursorToPlaylist(cursor);
            cursor.close();
            return playlist;
        }
        cursor.close();
        return null;
    }

    public Playlist getPlaylist(String playlistName) {
        Cursor cursor = database.rawQuery("SELECT * FROM " + MySQLiteHelper.TABLE_PLAYLISTS + " WHERE " + MySQLiteHelper.TABLE_PLAYLISTS_COLUMN_NAME + " = ?", new String[]{playlistName});

        cursor.moveToFirst();
        if( cursor != null && cursor.moveToFirst()) {
            Playlist playlist = cursorToPlaylist(cursor);
            cursor.close();
            return playlist;
        }
        cursor.close();
        return null;
    }

    public Playlist getFavouritePlaylist(){
        return getPlaylist("Favoriten");
    }


    public ArrayList<Folder> getAllFolders() {
        ArrayList<Folder> a = new ArrayList<>();
        Cursor cursor = database.query(MySQLiteHelper.TABLE_FOLDERS,
                allColumnsFOLDER, null, null, null, null, MySQLiteHelper.TABLE_FOLDERS_COLUMN_NAME + " COLLATE NOCASE ASC");
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            a.add(cursorToFolder(cursor));
            cursor.moveToNext();
        }
        // make sure to close the cursor
        cursor.close();
        return a;
    }

    public ArrayList<Playlist> getAllPlaylists() {
        ArrayList<Playlist> a = new ArrayList<>();
        Cursor cursor = database.query(MySQLiteHelper.TABLE_PLAYLISTS,
                allColumnsPLAYLISTS, null, null, null, null, MySQLiteHelper.TABLE_PLAYLISTS_COLUMN_NAME + " COLLATE NOCASE ASC");
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            a.add(cursorToPlaylist(cursor));
            cursor.moveToNext();
        }
        // make sure to close the cursor
        cursor.close();
        return a;
    }

    public ArrayList<Song> getAllSongs() {
        ArrayList<Song> a = new ArrayList<>();
        //Cursor cursor = database.query(MySQLiteHelper.TABLE_SONGS,
         //       allColumnsSONGS, null, null, null, null, MySQLiteHelper.TABLE_SONGS_COLUMN_SONGTITLE + " COLLATE NOCASE ASC");
        Cursor cursor = database.rawQuery("SELECT "+ MySQLiteHelper.TABLE_SONGS_COLUMN_ID + ", " + MySQLiteHelper.TABLE_SONGS_COLUMN_FILETITLE + ", " + MySQLiteHelper.TABLE_SONGS_COLUMN_PATH + ", " + MySQLiteHelper.TABLE_SONGS_COLUMN_SONGTITLE + ", " + MySQLiteHelper.TABLE_SONGS_COLUMN_AUTHOR + ", " + MySQLiteHelper.TABLE_SONGS_COLUMN_ALBUM_NAME + ", " + MySQLiteHelper.TABLE_SONGS_COLUMN_SONGLYRICS + " FROM " + MySQLiteHelper.TABLE_SONGS + " ORDER BY " + MySQLiteHelper.TABLE_SONGS_COLUMN_FILETITLE + " COLLATE NOCASE ASC", null);
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            Song s = cursorShortToSong(cursor);
            a.add(s);
            System.out.println(s.getFileTitle());
            cursor.moveToNext();
        }
        // make sure to close the cursor
        cursor.close();
        return a;
    }

    public ArrayList<Song> getAllSongsFilteredFromPlaylist(long playlistID){
        ArrayList<Song> allSongs = getAllSongs();
        ArrayList<Song> playlistSongs = getAllSongsFromPlaylist(playlistID);
        for(Song song : playlistSongs){
            for (int i = 0; i < allSongs.size(); i++){
                if(song.getId() == allSongs.get(i).getId()){
                    allSongs.remove(i);
                    break;
                }
            }
        }
        return allSongs;
    }

    public ArrayList<Song> getAllSongsFromFolder(String folderPath) {
        ArrayList<Song> a = new ArrayList<>();
        Cursor cursor = database.rawQuery("SELECT * FROM " + MySQLiteHelper.TABLE_SONGS + " WHERE " + MySQLiteHelper.TABLE_SONGS_COLUMN_PATH + " = ? ORDER BY " + MySQLiteHelper.TABLE_SONGS_COLUMN_FILETITLE + " COLLATE NOCASE ASC", new String[] {folderPath});
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            a.add(cursorToSong(cursor));
            cursor.moveToNext();
        }
        // make sure to close the cursor
        cursor.close();
        return a;
    }

    public ArrayList<Song> getAllSongsFromPlaylist(long playlistID) {
        ArrayList<Song> a = new ArrayList<>();
        //SELECT * FROM todos td, tags tg, todo_tags tt WHERE tg.tag_name = ‘Watchlist’ AND tg.id = tt.tag_id AND td.id = tt.todo_id;
        //SELECT * FROM playlist_song ps, songs sg where ps.id = pid AND ps.id = sg.id
        Cursor cursor = database.rawQuery("SELECT * FROM " + MySQLiteHelper.TABLE_SONGS + " sg, " + MySQLiteHelper.TABLE_PLAYLISTS_SONGS + " ps WHERE ps." + MySQLiteHelper.TABLE_PLAYLISTS_SONGS_COLUMN_PLAYLIST_ID + " = ? AND ps." + MySQLiteHelper.TABLE_PLAYLISTS_SONGS_COLUMN_SONG_ID + " = sg." + MySQLiteHelper.TABLE_SONGS_COLUMN_ID + " ORDER BY sg." + MySQLiteHelper.TABLE_SONGS_COLUMN_FILETITLE + " COLLATE NOCASE ASC" , new String[] {String.valueOf(playlistID)});
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            a.add(cursorToSong(cursor));
            cursor.moveToNext();
        }
        // make sure to close the cursor
        cursor.close();
        return a;
    }

    public ArrayList<Song> getAllFavouriteSongs() {
        ArrayList<Song> a = new ArrayList<>();
        Cursor cursor = database.rawQuery("SELECT * FROM " + MySQLiteHelper.TABLE_SONGS + " WHERE " + MySQLiteHelper.TABLE_SONGS_COLUMN_IS_FAVOURITE + " = " + 1 + " ORDER BY " + MySQLiteHelper.TABLE_SONGS_COLUMN_FILETITLE + " COLLATE NOCASE ASC", null);
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            a.add(cursorToSong(cursor));
            cursor.moveToNext();
        }
        // make sure to close the cursor
        cursor.close();
        return a;
    }


    public ArrayList<Folder> getPopularFolders(){
        ArrayList<Folder> a = new ArrayList<>();
        Cursor cursor = database.query(MySQLiteHelper.TABLE_FOLDERS,
                allColumnsFOLDER, null, null, null, null, MySQLiteHelper.TABLE_FOLDERS_COLUMN_FOLDER_CLICK + " DESC", "3");
        cursor.moveToFirst();
        int i = 0;
        while (!cursor.isAfterLast()) {
            a.add(cursorToFolder(cursor));
            a.get(i).setClickedFolder(cursor.getInt(5));
            i++;
            cursor.moveToNext();
        }
        // make sure to close the cursor
        cursor.close();
        return a;
    }

    public ArrayList<Song> getPopularSongs(){
        ArrayList<Song> a = new ArrayList<>();
        Cursor cursor = database.query(MySQLiteHelper.TABLE_SONGS,
                allColumnsSONGS, null, null, null, null, MySQLiteHelper.TABLE_SONGS_COLUMN_SONG_CLICK + " DESC", "3");
        cursor.moveToFirst();
        int i = 0;
        while (!cursor.isAfterLast()) {
            a.add(cursorToSong(cursor));
            a.get(i).setClickedSong(cursor.getInt(9));
            i++;
            cursor.moveToNext();
        }
        // make sure to close the cursor
        cursor.close();
        return a;
    }

    public int getSumSongs(){
        Cursor cursor = database.rawQuery("SELECT " + MySQLiteHelper.TABLE_STATISTICS_COLUMN_VALUE + " FROM " + MySQLiteHelper.TABLE_STATISTICS + " WHERE " + MySQLiteHelper.TABLE_STATISTICS_COLUMN_NAME + " = ?", new String[]{Config.STATISTIC_SUM_SONGS});
        cursor.moveToFirst();
        if( cursor != null && cursor.moveToFirst()) {
            return cursor.getInt(0);
        }
        else {
            ContentValues values = new ContentValues();
            values.put(MySQLiteHelper.TABLE_STATISTICS_COLUMN_NAME, Config.STATISTIC_SUM_SONGS);
            values.put(MySQLiteHelper.TABLE_STATISTICS_COLUMN_VALUE, 0);
            database.insertOrThrow(MySQLiteHelper.TABLE_STATISTICS, null,
                    values);
            getSumSongs();
        }
        cursor.close();
        return 0;
    }


    public int getSumDuration(){
        Cursor cursor = database.rawQuery("SELECT " + MySQLiteHelper.TABLE_STATISTICS_COLUMN_VALUE + " FROM " + MySQLiteHelper.TABLE_STATISTICS + " WHERE " + MySQLiteHelper.TABLE_STATISTICS_COLUMN_NAME + " = ?", new String[]{Config.STATISTIC_SUM_DURATION});
        cursor.moveToFirst();
        if( cursor != null && cursor.moveToFirst()) {
            return cursor.getInt(0);
        }
        else {
            ContentValues values = new ContentValues();
            values.put(MySQLiteHelper.TABLE_STATISTICS_COLUMN_NAME, Config.STATISTIC_SUM_DURATION);
            values.put(MySQLiteHelper.TABLE_STATISTICS_COLUMN_VALUE, 0);
            database.insertOrThrow(MySQLiteHelper.TABLE_STATISTICS, null,
                    values);
            getSumDuration();
        }
        cursor.close();
        return 0;
    }



    public int getSongClick(Song song){
        Cursor cursor = database.rawQuery("SELECT " + MySQLiteHelper.TABLE_SONGS_COLUMN_SONG_CLICK + " FROM " + MySQLiteHelper.TABLE_SONGS + " WHERE " + MySQLiteHelper.TABLE_SONGS_COLUMN_PATH_FILE + " = ?", new String[]{song.getPathFile()});

        cursor.moveToFirst();
        if( cursor != null && cursor.moveToFirst()) {
            return cursor.getInt(0);
        }
        cursor.close();
        return 0;
    }

    public int getFolderClick(Folder folder){
        Cursor cursor = database.rawQuery("SELECT " + MySQLiteHelper.TABLE_FOLDERS_COLUMN_FOLDER_CLICK + " FROM " + MySQLiteHelper.TABLE_FOLDERS + " WHERE " + MySQLiteHelper.TABLE_FOLDERS_COLUMN_PATH + " = ?", new String[]{folder.getPath()});

        cursor.moveToFirst();
        if( cursor != null && cursor.moveToFirst()) {
            return cursor.getInt(0);
        }
        cursor.close();
        return 0;
    }

    /*********GET DATA*********/

    /*********UPDATE DATA*********/

    public void incrementSongClick(Song song){
        ContentValues values = new ContentValues();
        values.put(MySQLiteHelper.TABLE_SONGS_COLUMN_SONG_CLICK, getSongClick(song) + 1);
        database.update(MySQLiteHelper.TABLE_SONGS, values, MySQLiteHelper.TABLE_SONGS_COLUMN_PATH_FILE + " = ?", new String[]{song.getPathFile()});
    }

    public void incrementFolderClick(Folder folder){
        ContentValues values = new ContentValues();
        values.put(MySQLiteHelper.TABLE_FOLDERS_COLUMN_FOLDER_CLICK,getFolderClick(folder) + 1);
        database.update(MySQLiteHelper.TABLE_FOLDERS, values, MySQLiteHelper.TABLE_FOLDERS_COLUMN_PATH + " = ?", new String[]{folder.getPath()});
    }

    public void incrementSumSongs(){
        ContentValues values = new ContentValues();
        values.put(MySQLiteHelper.TABLE_STATISTICS_COLUMN_VALUE, getSumSongs() + 1);
        database.update(MySQLiteHelper.TABLE_STATISTICS, values, MySQLiteHelper.TABLE_STATISTICS_COLUMN_NAME + " = ?", new String[]{Config.STATISTIC_SUM_SONGS});
    }

    public void updateSumDuration(int duration){
        ContentValues values = new ContentValues();
        values.put(MySQLiteHelper.TABLE_STATISTICS_COLUMN_VALUE, getSumDuration() + duration);
        database.update(MySQLiteHelper.TABLE_STATISTICS, values, MySQLiteHelper.TABLE_STATISTICS_COLUMN_NAME + " = ?", new String[]{Config.STATISTIC_SUM_DURATION});
    }

    public void updateIsFavourite(Song song){
        ContentValues values = new ContentValues();
        values.put(MySQLiteHelper.TABLE_SONGS_COLUMN_IS_FAVOURITE, song.isFavourite() ? 1 : 0);
        database.update(MySQLiteHelper.TABLE_SONGS, values, MySQLiteHelper.TABLE_SONGS_COLUMN_PATH_FILE + " = ?", new String[]{song.getPathFile()});
    }

    public void updateLyricsOfSong(Song song){
        ContentValues values = new ContentValues();
        values.put(MySQLiteHelper.TABLE_SONGS_COLUMN_SONGLYRICS, song.getLyrics());
        database.update(MySQLiteHelper.TABLE_SONGS, values, MySQLiteHelper.TABLE_SONGS_COLUMN_PATH_FILE + " = ?", new String[]{song.getPathFile()});
    }

    public void updateAlbumImgOfSong(Song song){
        ContentValues values = new ContentValues();
        values.put(MySQLiteHelper.TABLE_SONGS_COLUMN_ALBUM_PIC, song.getAlbumPic());
        database.update(MySQLiteHelper.TABLE_SONGS, values, MySQLiteHelper.TABLE_SONGS_COLUMN_PATH_FILE + " = ?", new String[]{song.getPathFile()});
    }

    public void updateSongInformation(Song song){
        ContentValues values = new ContentValues();
        values.put(MySQLiteHelper.TABLE_SONGS_COLUMN_SONGTITLE, song.getSongTitle());
        values.put(MySQLiteHelper.TABLE_SONGS_COLUMN_AUTHOR, song.getAuthor());
        values.put(MySQLiteHelper.TABLE_SONGS_COLUMN_ALBUM_NAME, song.getAlbumname());
        values.put(MySQLiteHelper.TABLE_SONGS_COLUMN_ALBUM_PIC, song.getAlbumPic());
        values.put(MySQLiteHelper.TABLE_SONGS_COLUMN_SONGLYRICS, song.getLyrics());
        database.update(MySQLiteHelper.TABLE_SONGS, values, MySQLiteHelper.TABLE_SONGS_COLUMN_PATH_FILE + " = ?", new String[]{song.getPathFile()});
    }

    public void updateFolderDurationAndCount(Folder folder){
        ContentValues values = new ContentValues();
        values.put(MySQLiteHelper.TABLE_FOLDERS_COLUMN_DURATION, folder.getMaxDuration());
        values.put(MySQLiteHelper.TABLE_FOLDERS_COLUMN_SONGSIN, folder.getSongsin());
        database.update(MySQLiteHelper.TABLE_FOLDERS, values, MySQLiteHelper.TABLE_FOLDERS_COLUMN_PATH + " = ?" , new String[]{folder.getPath()});
    }

    public void updatePlaylistDurationAndCount(Playlist playlist){
        ContentValues values = new ContentValues();
        values.put(MySQLiteHelper.TABLE_PLAYLISTS_COLUMN_DURATION, playlist.getMaxDuration());
        values.put(MySQLiteHelper.TABLE_PLAYLISTS_COLUMN_SONGSIN, playlist.getSongsin());
        database.update(MySQLiteHelper.TABLE_PLAYLISTS, values, MySQLiteHelper.TABLE_PLAYLISTS_COLUMN_ID + " = ?" , new String[]{String.valueOf(playlist.getId())});
    }

    public void addSongToPlaylist(long playlistID, long songID){
        if(!isSongInPlaylist(playlistID, songID)){
            ContentValues values = new ContentValues();
            values.put(MySQLiteHelper.TABLE_PLAYLISTS_SONGS_COLUMN_PLAYLIST_ID, playlistID);
            values.put(MySQLiteHelper.TABLE_PLAYLISTS_SONGS_COLUMN_SONG_ID, songID);
            database.insertOrThrow(MySQLiteHelper.TABLE_PLAYLISTS_SONGS, null, values);
            Playlist playlist = getPlaylist(playlistID);
            Song song = getSong(songID);
            playlist.setMaxDuration(playlist.getMaxDuration() + song.getDuration());
            playlist.setSongsin(playlist.getSongsin()+1);
            updatePlaylistDurationAndCount(playlist);
        }
    }


    /*********UPDATE DATA*********/





    private static Folder cursorToFolder(Cursor cursor) {
        return new Folder(cursor.getString(1), cursor.getString(2), cursor.getInt(3), cursor.getInt(4), cursor.getLong(0));
    }

    private static Song cursorToSong(Cursor cursor) {
        return new Song(cursor.getString(1), cursor.getString(2), cursor.getString(3), cursor.getString(6), cursor.getString(4), cursor.getBlob(7), cursor.getInt(8) == 1 ? true : false, cursor.getInt(10), cursor.getLong(0), cursor.getString(11));
    }

    private static Song cursorShortToSong(Cursor cursor){
        return new Song(cursor.getLong(0), cursor.getString(1), cursor.getString(2), cursor.getString(3), cursor.getString(4), cursor.getString(5), cursor.getString(6));
    }

    private static Playlist cursorToPlaylist(Cursor cursor) {
        return new Playlist(cursor.getString(1), cursor.getInt(2), cursor.getInt(3), cursor.getLong(0));
    }

}
