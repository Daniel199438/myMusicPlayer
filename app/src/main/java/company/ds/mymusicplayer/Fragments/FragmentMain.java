package company.ds.mymusicplayer.Fragments;

import android.app.Activity;
import android.content.SharedPreferences;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;

import company.ds.mymusicplayer.Adapter.PagerAdapter;
import company.ds.mymusicplayer.Config;
import company.ds.mymusicplayer.MainActivity;
import company.ds.mymusicplayer.R;


/**
 * Created by DS on 13.05.2017.
 */

public class FragmentMain extends Fragment {
    static Activity activity;
    private Menu mMenu;
    private static ViewPager mViewPager;
    private static PagerAdapter mSectionsPagerAdapter;
    /**
     * Returns a new instance of this fragment for the given section
     * number.
     */
    public static FolderList newInstance(String text) {
        FolderList fragment = new FolderList();
        Bundle args = new Bundle();
        //args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_main, container, false);
        activity = getActivity();
        //

        Toolbar toolbar = (Toolbar) view.findViewById(R.id.toolbar);
        ((AppCompatActivity) activity).setSupportActionBar(toolbar);

        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new PagerAdapter(activity, getChildFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) view.findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);
        SharedPreferences prefs = activity.getSharedPreferences(Config.SEND, 0);
        String songListType = prefs.getString(Config.SONGLISTTYPE_VISITED, "");
        if (songListType.equals(Config.TYPE_PLAYLIST) || songListType.equals(Config.TYPE_FAVOURITE_PLAYLIST)) {
            mViewPager.setCurrentItem(0);
        } else if (songListType.equals(Config.TYPE_FOLDER) || songListType.equals(Config.TYPE_FAVOURITE_FOLDER)) {
            mViewPager.setCurrentItem(1);
        }
        else {
            mViewPager.setCurrentItem(0);
        }

        TabLayout tabLayout = (TabLayout) view.findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);

        return view;
    }

    public static void setCurrentItem(final int position){
        if(mViewPager != null) {
            mViewPager.setCurrentItem(position, false);
            System.out.println("HALLO: " + position);
            mViewPager.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {

                }
            });
        }
    }

}
