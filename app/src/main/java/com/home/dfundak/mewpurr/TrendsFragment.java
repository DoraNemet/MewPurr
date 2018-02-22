package com.home.dfundak.mewpurr;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

/**
 * Created by DoraF on 22/02/2018.
 */

public class TrendsFragment extends Fragment {
    Button setTime;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View layout = inflater.inflate(R.layout.trends_fragment, container, false);
        return layout;
    }

}