package company.ds.mymusicplayer.helper;

/**
 * Created by DS on 02.04.2016.
 */

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaMetadataRetriever;
import android.media.MediaScannerConnection;
import android.os.Environment;
import android.util.Log;
import android.view.inputmethod.InputMethodManager;

import java.io.File;
import java.io.FileInputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Formatter;
 import java.util.Iterator;
 import java.util.Locale;
import java.util.Random;

import company.ds.mymusicplayer.Config;
import company.ds.mymusicplayer.MySqlDatabases.DataSource;
import company.ds.mymusicplayer.Object.Folder;
import company.ds.mymusicplayer.Object.Playlist;
import company.ds.mymusicplayer.Object.Song;
import jaudiotagger.org.jaudiotagger.audio.AudioFile;
import jaudiotagger.org.jaudiotagger.audio.AudioFileIO;
import jaudiotagger.org.jaudiotagger.audio.exceptions.CannotReadException;
import jaudiotagger.org.jaudiotagger.audio.exceptions.InvalidAudioFrameException;
import jaudiotagger.org.jaudiotagger.audio.exceptions.ReadOnlyFileException;
import jaudiotagger.org.jaudiotagger.tag.FieldKey;
import jaudiotagger.org.jaudiotagger.tag.Tag;
import jaudiotagger.org.jaudiotagger.tag.TagException;
import wseemann.media.FFmpegMediaMetadataRetriever;

public class Utils {
    /*
    private static ArrayList<Folder> getFolderList_new(File parentDir, String pathToParentDir, Activity activity){
        final DataSource dataSource = new DataSource(activity);
        final MediaMetadataRetriever mMr = new MediaMetadataRetriever();
        //String files[] = root.list(audioFilter);

        FilenameFilter audioFilter = new FilenameFilter() {
            File f;
            public boolean accept(File dir, String name) {
                if(isSong(name)) {
                    if(dataSource.getSong(dir.getPath() + "/" + name) == null){
                        Song f = new Song(name, dir.getPath());
                        //System.out.println(f.getPathFile());
                        mMr.setDataSource(f.getPathFile());
                        f.setAlbumname(mMr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ALBUM));
                        f.setSongTitle(mMr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE));
                        f.setAuthor(mMr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ARTIST));
                        f.setAlbumPic(mMr.getEmbeddedPicture());
                        dataSource.storeSong(f);
                    }

                }
                else if(dir.isDirectory()) {

                }

                return true;
            }
        };


        return null;
    }
    */

    /**
     * get and compare Folders of DB and memory of phone
     *
     * */
    private static ArrayList<Folder> getFolderList(File parentDir, String pathToParentDir, Activity activity) {
        DataSource dataSource = new DataSource(activity);
        ArrayList<Folder> inFiles = new ArrayList<Folder>();
        String[] fileNames = parentDir.list();

        if(fileNames != null){
            for (String fileName : fileNames) {
                File file = new File(parentDir.getPath() + "/" + fileName);
                if(file.isDirectory()) {
                    inFiles.addAll(getFolderList(file, pathToParentDir + "/" + fileName, activity));
                }
                else {
                    if (isSong(fileName)) {
                        Folder f = new Folder(parentDir.getName(), parentDir.toString(), -1, -1);
                        if(!inFiles.contains(f)) {
                            if (dataSource.getFolder(f.getPath()) == null) {
                                //int[] durco = countDurationOfSong(fileNames, parentDir.getPath(), activity);
                                //f.setSongsin(durco[0]);
                                //f.setMaxDuration(durco[1]);
                                f = dataSource.createFolder(f.getName(), f.getPath(), f.getSongsin(), f.getMaxDuration());
                            }
                            else {
                                Folder oldF = dataSource.getFolder(f.getPath());
                                f.setSongsin(oldF.getSongsin());
                                f.setMaxDuration(oldF.getMaxDuration());
                            }
                            inFiles.add(f);
                        }
                        break;
                    }
                }
            }

            Collections.sort(inFiles, new Comparator<Folder>() {
                public int compare(Folder a, Folder b) {
                    return a.getName().toLowerCase().compareTo(b.getName().toLowerCase());
                }
            });
            return inFiles;
        }
        return null;
    }



    public static void deleteFile(String inputPath, String inputFile, Activity activity) {
        try {
            System.out.println("FILE DELETED: " + inputPath + "/" + inputFile);
            // delete the original file
            new File(inputPath + inputFile).delete();
            //update the FileIndex, so it will be deleted in Explorer
            MediaScannerConnection.scanFile(activity, new String[]{inputPath + "/" + inputFile}, null, null);

        } catch (Exception e) {
            Log.e("tag", e.getMessage());
        }
    }

    public static void hideSoftKeyboard(Activity activity) {
        /*
        InputMethodManager inputMethodManager =
                (InputMethodManager) activity.getSystemService(
                        Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(
                activity.getCurrentFocus().getWindowToken(), 0);
        */
        try
        {
            InputMethodManager inputManager = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
            inputManager.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(), 0);
        }
        catch (Exception e)
        {
            // Ignore exceptions if any
            Log.e("KeyBoardUtil", e.toString(), e);
        }
    }


    public static ArrayList<Song> updateSongs(String path, Activity activity) throws ReadOnlyFileException, IOException, TagException, InvalidAudioFrameException, CannotReadException {
        DataSource dataSource = new DataSource(activity);
        ArrayList<Song> oldSongs = dataSource.getAllSongsFromFolder(path);
        File parentDir = new File(path);
        boolean songIsAct;
        String[] fileNames = parentDir.list();
        for(Song song : oldSongs){
            File file = new File(song.getPathFile());
            if(!file.exists()){
                dataSource.deleteSong(song);
            }
        }
        oldSongs = dataSource.getAllSongsFromFolder(path);

        for(String fileName : fileNames){
            if(isSong(fileName)){
                Song f = new Song(fileName, path);
                //System.out.println(f.getPathFile());
                f = retrieveSongFileMetaData(f);
                //countDurationOfSong(fileNames, path, activity);
                if(!oldSongs.contains(f)){
                    oldSongs.add(f);
                    dataSource.storeSong(f);
                }

            }
        }

        updateFolderDurationAndCountOfSpecificFolder(activity, dataSource.getFolder(path));

        return oldSongs;
    }

    public static void addSongsToPlaylist(Activity activity, Playlist createdPlaylist, ArrayList<Long> songIDs){
        DataSource dataSource = new DataSource(activity);
        if(createdPlaylist != null && songIDs != null){
            for(Long songID: songIDs){
                dataSource.addSongToPlaylist(createdPlaylist.getId(), songID);
            }

        }
    }


    private static boolean isSong(String fileName){
        if(fileName.toLowerCase().endsWith(".mp3") || fileName.toLowerCase().endsWith(".m4a") || fileName.toLowerCase().endsWith(".flac")){
            return true;
        }
        return false;
    }

    public static void updateFolderDurationAndCountOfSpecificFolder(Activity activity, Folder folder) throws ReadOnlyFileException, CannotReadException, TagException, InvalidAudioFrameException, IOException {
        DataSource dataSource = new DataSource(activity);
        File parentDir = new File(folder.getPath());
        String[] fileNames = parentDir.list();
        int[] durco = countDurationOfSongAndStoretoDatabase(fileNames, parentDir.getPath(), activity);
        folder.setSongsin(durco[0]);
        folder.setMaxDuration(durco[1]);
        Folder oldF = dataSource.getFolder(folder.getPath());
        if (folder.getMaxDuration() != oldF.getMaxDuration() || folder.getSongsin() != oldF.getSongsin()) {
            dataSource.updateFolderDurationAndCount(folder);
        }

    }

    public static ArrayList<Folder> updateFolderDurationAndCount(Activity activity) throws IOException, TagException, InvalidAudioFrameException, CannotReadException, ReadOnlyFileException {
        DataSource dataSource = new DataSource(activity);
        ArrayList<Folder> folders = dataSource.getAllFolders();
        for(Folder folder: folders) {
            File parentDir = new File(folder.getPath());
            String[] fileNames = parentDir.list();
            int[] durco = countDurationOfSongAndStoretoDatabase(fileNames, parentDir.getPath(), activity);
            folder.setSongsin(durco[0]);
            folder.setMaxDuration(durco[1]);
            Folder oldF = dataSource.getFolder(folder.getPath());
            if (folder.getMaxDuration() != oldF.getMaxDuration() || folder.getSongsin() != oldF.getSongsin()) {
                dataSource.updateFolderDurationAndCount(folder);
            }
        }
        return folders;
    }


    /*
    private static ArrayList<Folder> getList(File parentDir, String pathToParentDir, Activity activity) {
        DataSource dataSource = new DataSource(activity);
        ArrayList<Folder> inFiles = new ArrayList<Folder>();
        String[] fileNames = parentDir.list();

        for (String fileName : fileNames) {
            File file = new File(parentDir.getPath() + "/" + fileName);
            if(file.isDirectory()) {
                inFiles.addAll(getFolderList(file, pathToParentDir + "/" + fileName, activity));
            }
            else {
                if(isSong(fileName)){
                    int[] durco = countDurationOfSongAndStoretoDatabase(fileNames, parentDir.getPath(), activity);
                    Folder f = new Folder(parentDir.getName(), parentDir.toString(), durco[0], durco[1]);
                    if(!inFiles.contains(f)) {
                        inFiles.add(f);
                        if (dataSource.getFolder(f.getPath()) == null) {
                            dataSource.storeFolder(f);
                        }
                        else {
                            Folder oldF = dataSource.getFolder(f.getPath());
                            if (f.getMaxDuration() != oldF.getMaxDuration() || f.getSongsin() != oldF.getSongsin()) {
                                dataSource.updateFolderDurationAndCount(f);
                            }
                        }
                    }
                    break;
                }
            }
        }

        Collections.sort(inFiles, new Comparator<Folder>() {
            public int compare(Folder a, Folder b) {
                return a.getName().toLowerCase().compareTo(b.getName().toLowerCase());
            }
        });
        return inFiles;
    }
    */
    public static ArrayList<Folder> getAndUpdateFolders(File parentDir, String pathToParentDir, Activity activity){
        DataSource dataSource = new DataSource(activity);
        ArrayList<Folder> inFiles = new ArrayList<Folder>();
        ArrayList<Folder> correctFolder = new ArrayList<>();

        inFiles = getFolderList(parentDir, pathToParentDir, activity);
        ArrayList<Folder> dbFolder = dataSource.getAllFolders();
        if(dbFolder != null){
            for(Folder folder : dbFolder){
                if(!inFiles.contains(folder)){
                    dataSource.deleteFolder(folder.getId());
                }
                else {
                    correctFolder.add(folder);
                }
            }
        }
        return correctFolder;
    }


    public static void incrementStatistics(Song song, Activity activity){
        DataSource dataSource = new DataSource(activity);
        dataSource.incrementFolderClick(dataSource.getFolder(song.getPath()));
        dataSource.incrementSongClick(song);
        dataSource.incrementSumSongs();
    }


    public static String showPathOutside(String path){
        return path.replace(Environment.getExternalStorageDirectory().getPath(), "");
    }

    public static int[] countDurationOfSong(String[] fileNames, String folderPath, Activity activity) {
        int duration = 0;
        int count = 0;
        int[] durco = new int[2];
        ArrayList<Song> inFiles = new ArrayList<Song>();
        DataSource dataSource = new DataSource(activity);
        MediaMetadataRetriever mMr = new MediaMetadataRetriever();
        for (String fileName : fileNames) {
            if(isSong(fileName)){
                Song f = new Song(fileName, folderPath);
                //System.out.println(f.getPathFile());
                mMr.setDataSource(f.getPathFile());
                count++;
                duration += Integer.parseInt(mMr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION));

            }
        }

        durco[0] = count;
        durco[1] = duration;
        return durco;
    }


    public static int[] countDurationOfSongAndStoretoDatabase(String[] fileNames, String folderPath, Activity activity) throws IOException, TagException, TagException, ReadOnlyFileException, CannotReadException, InvalidAudioFrameException, TagException, ReadOnlyFileException, CannotReadException, InvalidAudioFrameException, InvalidAudioFrameException, CannotReadException, ReadOnlyFileException, TagException, ReadOnlyFileException, CannotReadException, InvalidAudioFrameException {
        int duration = 0;
        int count = 0;
        int[] durco = new int[2];
        ArrayList<Song> inFiles = new ArrayList<Song>();
        DataSource dataSource = new DataSource(activity);

        for(String fileName : fileNames){
            if(isSong(fileName)){
                Song f = new Song(fileName, folderPath);
                //System.out.println(f.toString());
                f = retrieveSongFileMetaData(f);
                duration += f.getDuration();

                count++;

                if(!inFiles.contains(f)){
                    inFiles.add(f);
                    if(dataSource.getSong(f.getPathFile()) == null){
                        dataSource.storeSong(f);
                    }
                    else {
                        Song s = dataSource.getSong(f.getPathFile());
                        s.setAlbumPic(f.getAlbumPic());
                        s.setLyrics(f.getLyrics());
                        s.setAuthor(f.getAuthor());
                        s.setSongTitle(f.getSongTitle());
                        s.setAlbumname(f.getAlbumname());
                        dataSource.updateSongInformation(s);
                    }
                }
            }
        }

        //removes Songs who doesnt exist anymore
        ArrayList<Song> dbSongs = dataSource.getAllSongsFromFolder(folderPath);
        for(Song song : dbSongs){

            if(!inFiles.contains(song)){
                dataSource.deleteSong(song.getPathFile());
            }
        }

        durco[0] = count;
        durco[1] = duration;
        return durco;
    }


    private static Song retrieveSongFileMetaData(Song f) throws TagException, ReadOnlyFileException, CannotReadException, InvalidAudioFrameException, IOException {
        File file = new File(f.getPathFile());
        MediaMetadataRetriever mMr = new MediaMetadataRetriever();
        //System.out.println(f.getPathFile());
        mMr.setDataSource(f.getPathFile());

        f.setDuration(Integer.parseInt(mMr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)));
        f.setAlbumname(mMr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ALBUM));
        f.setSongTitle(mMr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE));
        f.setAuthor(mMr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ARTIST));
        f.setAlbumPic(mMr.getEmbeddedPicture());

        if(file != null){

            AudioFile musicFile = AudioFileIO.read(file);
            if(musicFile != null){
                Tag tag = musicFile.getTag();
                if(tag != null){
                    if(tag.hasField(FieldKey.LYRICS)){
                        String lyrics = tag.getFirstField(FieldKey.LYRICS).toString();
                        System.out.println("LYRICS: " + tag.getFirstField(FieldKey.LYRICS).toString());
                        if(lyrics.startsWith("Language")){

                            lyrics = lyrics.substring(28, lyrics.length()-3);

                        }
                        f.setLyrics(lyrics);
                    }
                }
            }
        }

        return f;
    }

    public static Bitmap convertBytetoBitmap(byte[] data){
        return BitmapFactory.decodeByteArray(data, 0, data.length);
    }

    public static String stringForTime(int timeMs) {
        StringBuilder mFormatBuilder = new StringBuilder();
        Formatter mFormatter = new Formatter(mFormatBuilder, Locale.getDefault());

        int totalSeconds = timeMs / 1000;

        int seconds = totalSeconds % 60;
        int minutes = (totalSeconds / 60) % 60;
        int hours   = totalSeconds / 3600;

        mFormatBuilder.setLength(0);
        if (hours > 0) {
            return mFormatter.format("%d:%02d:%02d", hours, minutes, seconds).toString();
        } else {
            return mFormatter.format("%02d:%02d", minutes, seconds).toString();
        }
    }

    public static ArrayList<Song> randomizeSongList(ArrayList<Song> songs){
        ArrayList<Song> newList = new ArrayList();
        if(songs.size() > 0){
            Random rand = new Random();
            int allSongs = songs.size();
            int rndSong = 0;
            while (allSongs > 0){
                rndSong = rand.nextInt(songs.size());
                if(!newList.contains(songs.get(rndSong))){
                    newList.add(songs.get(rndSong));
                    allSongs--;
                }
            }
            return newList;
        }
        return songs;
    }


}