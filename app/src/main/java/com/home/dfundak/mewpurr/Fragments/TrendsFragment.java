package com.home.dfundak.mewpurr.Fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.widget.ImageView;

import com.home.dfundak.mewpurr.R;

/**
 * Created by DoraF on 22/02/2018.
 */

public class TrendsFragment extends Fragment {

    final String URL = "https://meowmeow-app.herokuapp.com/#statistics";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View layout = inflater.inflate(R.layout.trends_fragment, container, false);
        ImageView statsImage = getActivity().findViewById(R.id.stats_image);
        statsImage.setImageResource(R.drawable.stats_yellow);
        ImageView alarmImage = getActivity().findViewById(R.id.alarm_image);
        alarmImage.setImageResource(R.drawable.clock);
        ImageView homeImage = getActivity().findViewById(R.id.home_image);
        homeImage.setImageResource(R.drawable.home);

        final WebView webview = layout.findViewById(R.id.webView);
        webview.setWebChromeClient(new WebChromeClient());
        webview.getSettings().setBuiltInZoomControls(false);
        webview.getSettings().setSupportZoom(false);
        webview.getSettings().setJavaScriptEnabled(true);
        webview.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
        webview.getSettings().setAllowFileAccess(true);
        webview.getSettings().setDomStorageEnabled(true);
        webview.loadUrl(URL);

        return layout;
    }

}