package company.ds.mymusicplayer.Adapter;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import company.ds.mymusicplayer.Config;
import company.ds.mymusicplayer.Fragments.FolderList;
import company.ds.mymusicplayer.Fragments.PlaylistList;
import company.ds.mymusicplayer.Fragments.SongList;
import company.ds.mymusicplayer.Object.Playlist;

/**
 * Created by DS on 13.05.2017.
 */

public class PagerAdapter extends FragmentPagerAdapter {
    Context ctxt=null;

    public PagerAdapter(Context ctxt, FragmentManager mgr) {
        super(mgr);
        this.ctxt=ctxt;
    }

    @Override
    public int getCount() {
        return(3);
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return PlaylistList.newInstance("FirstFragment, Instance 1");
            case 1:
                return FolderList.newInstance("SecondFragment, Instance 2");
            case 2:
                return SongList.newInstance(Config.TYPE_ALLSONGS);
            default:
                return PlaylistList.newInstance("FirstFragment, Instance 1");
        /*
        case 1: return SecondFragment.newInstance("SecondFragment, Instance 1");
        case 2: return ThirdFragment.newInstance("ThirdFragment, Instance 1");
        */
        }

    }


    @Override
    public String getPageTitle(int position) {
       String[] title = new String[] {"Playlists","Ordner","Alle Songs"};

        return(title[position]);
    }


}