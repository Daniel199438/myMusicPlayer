package company.ds.mymusicplayer.Object;

import android.media.Image;

/**
 * Created by DS on 02.04.2016.
 */
public class Folder {
    String name;
    String path;
    Image cover;
    int songsin;
    int maxDuration;
    boolean selectedFolder;
    int clickedFolder;
    long id;

    public Folder(String name, String path, int songsin, int maxDuration) {
        this.name = name;
        this.path = path;
        this.songsin = songsin;
        this.maxDuration = maxDuration;
    }

    public Folder(String name, String path, int songsin, int maxDuration, long id) {
        this.name = name;
        this.path = path;
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

    public int getClickedFolder() {
        return clickedFolder;
    }

    public void setClickedFolder(int clickedFolder) {
        this.clickedFolder = clickedFolder;
    }

    public boolean isSelectedFolder() {
        return selectedFolder;
    }

    public void setSelectedFolder(boolean selectedFolder) {
        this.selectedFolder = selectedFolder;
    }

    public int getMaxDuration() {
        return maxDuration;
    }

    public void setMaxDuration(int maxDuration) {
        this.maxDuration = maxDuration;
    }

    public Image getCover() {
        return cover;
    }

    public void setCover(Image cover) {
        this.cover = cover;
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

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    @Override
    public String toString() {
        return "Folder{" +
                "path='" + path + '\'' +
                ", name='" + name + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Folder folder = (Folder) o;

        if (!getName().equals(folder.getName())) return false;
        return getPath().equals(folder.getPath());

    }

    @Override
    public int hashCode() {
        int result = getName().hashCode();
        result = 31 * result + getPath().hashCode();
        return result;
    }

    public boolean isEqual(Folder folder){
        if(this.name.equals(folder.getName()) && this.path.equals(folder.getPath())){
            return true;
        }
        return false;
    }




}
