package company.ds.mymusicplayer.Fragments;


import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.util.List;

import company.ds.mymusicplayer.Config;
import company.ds.mymusicplayer.MainActivity;
import company.ds.mymusicplayer.Object.Song;
import company.ds.mymusicplayer.R;
import company.ds.mymusicplayer.Service.MusicService;

/**
 * Created by DS on 02.12.2017.
 */

public class SongListDetail extends Fragment {
    static Activity activity;
    static MusicService musicSrv;
    private static SharedPreferences prefs;
    View view;
    static TextView songTextTextView;
    static Song oldSong;


    public static SongListDetail newInstance(String text) {
        SongListDetail fragment = new SongListDetail();
        Bundle args = new Bundle();
        //args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        fragment.setArguments(args);
        return fragment;
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Bundle args = getArguments();
        activity = getActivity();
        musicSrv = MainActivity.getMusicSrv();
        oldSong = null;
        prefs = activity.getSharedPreferences(Config.SEND, 0);
        setHasOptionsMenu(true);

        view = inflater.inflate(R.layout.songlist_detail, container, false);

        songTextTextView = (TextView) view.findViewById(R.id.songText);


        buildAndRefreshLRCView();

        //view = ;
        return view;
    }



    public static void buildAndRefreshLRCView() {
        if (musicSrv != null) {
            Song actSong = musicSrv.getActSong();
            if (oldSong != null) {
                if (oldSong.getId() != actSong.getId()) {
                    createLRCView(actSong);
                } else {
                    createLRCView(actSong);
                }
            }
            else {
                createLRCView(actSong);
            }
        }
    }



private static void createLRCView(Song actSong){
    if (actSong != null) {
        String lrc = actSong.getLyrics();
        oldSong = actSong;
        //Log.d(TAG, "lrc:" + lrc);

        if (lrc != null && lrc != "" && lrc.length() > 0) {
            System.out.println("LYRICS + SIZE: " + lrc);

            /*
            StringReader reader = new StringReader(lrc);
            BufferedReader br = new BufferedReader(reader);

            String line = br.readLine();


            for(int i = 0; i < lrc.length(); i++){

            }
            */
            songTextTextView.setText(lrc);

        }
        else {
            songTextTextView.setText("Kein Songtext vorhanden!");
        }

    }
}

}
