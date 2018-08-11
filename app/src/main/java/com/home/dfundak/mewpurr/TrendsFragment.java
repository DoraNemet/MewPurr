package com.home.dfundak.mewpurr;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

/**
 * Created by DoraF on 22/02/2018.
 */

public class TrendsFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View layout = inflater.inflate(R.layout.trends_fragment, container, false);
        ImageView statsImage = getActivity().findViewById(R.id.stats_image);
        statsImage.setAlpha(0.5f);
        ImageView alarmImage = getActivity().findViewById(R.id.alarm_image);
        alarmImage.setAlpha(1f);
        ImageView homeImage = getActivity().findViewById(R.id.home_image);
        homeImage.setAlpha(1f);
        return layout;
    }

}