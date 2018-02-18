package company.ds.mymusicplayer.Object;

/**
 * Created by DS on 02.04.2016.
 */
public class Song {
    String fileTitle;
    String songTitle;
    String author;
    String albumName;
    String path;
    byte[] albumPic;
    boolean selectedSong;
    boolean isFavourite;
    int duration;
    int clickedSong;
    long id;
    String lyrics;

    public Song(Long id, String fileTitle, String path, String songTitle, String author, String albumName, String lyrics) {
        this.fileTitle = fileTitle;
        this.path = path;
        this.id = id;
        this.songTitle = songTitle;
        this.author = author;
        this.albumName = albumName;
        selectedSong = false;
        isFavourite = false;
        this.lyrics = lyrics;
    }

    public Song(String fileTitle, String path) {
        this.fileTitle = fileTitle;
        this.path = path;
        selectedSong = false;
        isFavourite = false;
    }

    public Song(String fileTitle, String songTitle, String author, String albumName, String path, byte[] albumPic, boolean isFavourite, int duration) {
        this.fileTitle = fileTitle;
        this.songTitle = songTitle;
        this.author = author;
        this.albumName = albumName;
        this.path = path;
        this.albumPic = albumPic;
        this.isFavourite = isFavourite;
        this.duration = duration;
    }

    public Song(String fileTitle, String songTitle, String author, String albumName, String path, byte[] albumPic, boolean isFavourite, int duration, long id, String lyrics) {
        this.fileTitle = fileTitle;
        this.songTitle = songTitle;
        this.author = author;
        this.albumName = albumName;
        this.path = path;
        this.albumPic = albumPic;
        this.isFavourite = isFavourite;
        this.duration = duration;
        this.id = id;
        this.lyrics = lyrics;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public int getClickedSong() {
        return clickedSong;
    }

    public void setClickedSong(int clickedSong) {
        this.clickedSong = clickedSong;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public boolean isFavourite() {
        return isFavourite;
    }

    public void setIsFavourite(boolean isFavourite) {
        this.isFavourite = isFavourite;
    }

    public byte[] getAlbumPic() {
        return albumPic;
    }

    public void setAlbumPic(byte[] albumPic) {
        this.albumPic = albumPic;
    }

    public String getSongTitle() {
        return songTitle;
    }

    public void setSongTitle(String songTitle) {
        this.songTitle = songTitle;
    }

    public String getFileTitle() {
        return fileTitle;
    }

    public void setFileTitle(String title) {
        this.fileTitle = title;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getAlbumname() {
        return albumName;
    }

    public void setAlbumname(String albumname) {
        this.albumName = albumname;
    }

    public String getPath() {
        return path;
    }

    public String getPathFile() {
        return path + "/" + getFileTitle();
    }

    public void setPath(String path) {
        this.path = path;
    }

    public boolean isSelectedSong() {
        return selectedSong;
    }

    public void setSelectedSong(boolean selectedSong) {
        this.selectedSong = selectedSong;
    }

    public String getLyrics() {
        return lyrics;
    }

    public void setLyrics(String lyrics) {
        this.lyrics = lyrics;
    }

    @Override
    public String toString() {
        return "Song{" +
                "title='" + songTitle + '\'' +
                ", author='" + author + '\'' +
                ", albumname='" + albumName + '\'' +
                ", path='" + path + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Song song = (Song) o;

        if (isSelectedSong() != song.isSelectedSong()) return false;
        if (!getFileTitle().equals(song.getFileTitle())) return false;
        if (getSongTitle() != null ? !getSongTitle().equals(song.getSongTitle()) : song.getSongTitle() != null)
            return false;
        if (getAuthor() != null ? !getAuthor().equals(song.getAuthor()) : song.getAuthor() != null)
            return false;
        if (getAlbumname() != null ? !getAlbumname().equals(song.getAlbumname()) : song.getAlbumname() != null)
            return false;
        return getPath().equals(song.getPath());

    }

    @Override
    public int hashCode() {
        int result = getFileTitle().hashCode();
        result = 31 * result + (getAuthor() != null ? getAuthor().hashCode() : 0);
        result = 31 * result + (getAlbumname() != null ? getAlbumname().hashCode() : 0);
        result = 31 * result + getPath().hashCode();
        return result;
    }
}
