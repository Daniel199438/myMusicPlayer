package company.ds.mymusicplayer.Adapter;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import company.ds.mymusicplayer.Fragments.FolderList;
import company.ds.mymusicplayer.Fragments.PlaylistList;
import company.ds.mymusicplayer.Fragments.SongList;
import company.ds.mymusicplayer.Fragments.SongListDetail;

/**
 * Created by DS on 02.12.2017.
 */

public class PagerAdapterSongDetail extends FragmentPagerAdapter {
    Context ctxt=null;

    public PagerAdapterSongDetail(Context ctxt, FragmentManager mgr) {
        super(mgr);
        this.ctxt=ctxt;
    }

    @Override
    public int getCount() {
        return(2);
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return SongList.newInstance("SongList");
            case 1:
                return SongListDetail.newInstance("SecondFragment, Instance 2");
            default:
                return SongList.newInstance("FirstFragment, Instance 1");
        /*
        case 1: return SecondFragment.newInstance("SecondFragment, Instance 1");
        case 2: return ThirdFragment.newInstance("ThirdFragment, Instance 1");
        */
        }

    }


    @Override
    public String getPageTitle(int position) {
        String[] title = new String[] {"Songliste","Songtext"};

        return(title[position]);
    }
}
