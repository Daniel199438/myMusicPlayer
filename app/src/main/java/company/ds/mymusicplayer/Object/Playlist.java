package company.ds.mymusicplayer.Object;

/**
 * Created by DS on 16.01.2017.
 */

public class Playlist {
    String name;
    int songsin;
    int maxDuration;
    boolean selectedPlaylist;
    int clickedPlaylist;
    long id;

    public Playlist(String name, int songsin, int maxDuration) {
        this.name = name;
        this.songsin = songsin;
        this.maxDuration = maxDuration;
    }

    public Playlist(String name) {
        this.name = name;
    }

    public Playlist(String name, int songsin, int maxDuration, long id) {
        this.name = name;
        this.songsin = songsin;
        this.maxDuration = maxDuration;
        this.id = id;
    }


    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public int getclickedPlaylist() {
        return clickedPlaylist;
    }

    public void setclickedPlaylist(int clickedPlaylist) {
        this.clickedPlaylist = clickedPlaylist;
    }

    public boolean isselectedPlaylist() {
        return selectedPlaylist;
    }

    public void setselectedPlaylist(boolean selectedPlaylist) {
        this.selectedPlaylist = selectedPlaylist;
    }

    public int getMaxDuration() {
        return maxDuration;
    }

    public void setMaxDuration(int maxDuration) {
        this.maxDuration = maxDuration;
    }

    public int getSongsin() {
        return songsin;
    }

    public void setSongsin(int songsin) {
        this.songsin = songsin;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "Playlist{" +
                ", name='" + name + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Playlist)) return false;

        Playlist playlist = (Playlist) o;

        if (getId() != playlist.getId()) return false;
        return getName().equals(playlist.getName());

    }

    @Override
    public int hashCode() {
        int result = getName().hashCode();
        result = 31 * result + (int) (getId() ^ (getId() >>> 32));
        return result;
    }
}
