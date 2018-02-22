package com.home.dfundak.mewpurr;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

/**
 * Created by DoraF on 22/02/2018.
 */

public class HomeFragment extends Fragment {
    private static String FOOD_RELEASED = "Food released";
    Button feedMeButton;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View layout = inflater.inflate(R.layout.home_fragment, container, false);
        initializeUI(layout);
        return layout;
    }

    private void initializeUI(View layout) {
        this.feedMeButton = (Button) layout.findViewById(R.id.feed_me_button);
        this.feedMeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getActivity(), FOOD_RELEASED, Toast.LENGTH_SHORT).show();
            }
        });
    }


}
