package com.home.dfundak.mewpurr;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{
    private ImageView clock, home, trends;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initializeUI();
        setUp();
    }

    private void initializeUI() {
        this.clock = (ImageView) this.findViewById(R.id.alarm_image);
        this.home = (ImageView) this.findViewById(R.id.home_image);
        this.trends = (ImageView) this.findViewById(R.id.stats_image);

        this.clock.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
        this.home.setOnClickListener(this);
        this.trends.setOnClickListener(this);
        this.clock.setOnClickListener(this);
    }
    @Override
    public void onClick(View v) {
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        switch (v.getId()) {
            case R.id.alarm_image:
                fragmentTransaction.replace(R.id.frameLayout, new AlarmFragment());
                fragmentTransaction.commit();
                break;
            case R.id.home_image:
                fragmentTransaction.replace(R.id.frameLayout, new HomeFragment());
                fragmentTransaction.commit();
                break;
            case R.id.stats_image:
                fragmentTransaction.replace(R.id.frameLayout, new TrendsFragment());
                fragmentTransaction.commit();
                break;
        }
    }

    private void setUp() {
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.add(R.id.frameLayout, new HomeFragment());
        fragmentTransaction.commit();
    }

}
