package com.home.dfundak.mewpurr.Fragments;

import android.app.AlertDialog;
import android.app.Fragment;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.home.dfundak.mewpurr.Adapters.TimestampAdapter;
import com.home.dfundak.mewpurr.Class.Sensor;
import com.home.dfundak.mewpurr.Class.Timestamp;
import com.home.dfundak.mewpurr.R;
import com.home.dfundak.mewpurr.SupportData.HTTPDataHandler;
import com.home.dfundak.mewpurr.SupportData.SensorSupportData;
import com.home.dfundak.mewpurr.SupportData.TimestampSupportData;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by DoraF on 22/02/2018.
 */

//https://www.hivemq.com/blog/mqtt-client-library-enyclopedia-paho-android-service
//https://docs.mlab.com/data-api/
//https://developer.android.com/reference/android/net/ConnectivityManager
//https://antonioleiva.com/swiperefreshlayout/

public class HomeFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {
    private static String FOOD_RELEASED = "Food request sent";
    private Button feedMeButton;
    private RecyclerView timestampsLV;
    private ProgressBar progressBar;
    private TextView messageTV, warningTV;
    private SwipeRefreshLayout swipeRefreshLayout;

    ArrayList<Timestamp> timestamps = new ArrayList<Timestamp>();
    TimestampAdapter adapter;

    private final String TOPIC = "mewpurr/food";
    private MqttAndroidClient client = new MqttAndroidClient(null, null, null);

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View layout = inflater.inflate(R.layout.home_fragment, container, false);
        initializeUI(layout);

        ImageView homeImage = getActivity().findViewById(R.id.home_image);
        homeImage.setImageResource(R.drawable.home_yellow);
        ImageView statsImage = getActivity().findViewById(R.id.stats_image);
        statsImage.setImageResource(R.drawable.stats);
        ImageView alarmImage = getActivity().findViewById(R.id.alarm_image);
        alarmImage.setImageResource(R.drawable.clock);
        return layout;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        client.unregisterResources();
        client.close();
    }

    @Override
    public void onStart() {
        super.onStart();
        if (!isConnected(getActivity())) {
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setTitle("No Internet Connection")
                    .setMessage("You need to have mobile data or wifi to fully use the app")
                    .setPositiveButton("OK", null);
            AlertDialog dialog = builder.create();
            dialog.show();
        }
    }

    private void initializeUI(View layout) {
        feedMeButton = layout.findViewById(R.id.feed_me_button);
        timestampsLV = layout.findViewById(R.id.timestamps_list_view);
        progressBar = layout.findViewById(R.id.pbProgress);
        messageTV = layout.findViewById(R.id.message);
        warningTV = layout.findViewById(R.id.warning);
        swipeRefreshLayout = layout.findViewById(R.id.swipe_container);
        swipeRefreshLayout.setOnRefreshListener(this);

        if (isConnected(getActivity())) {
            adapter = new TimestampAdapter(timestamps);
            timestampsLV.setAdapter(adapter);
            timestampsLV.setLayoutManager(new LinearLayoutManager(getActivity()));

            new GetDataTimestamps().execute(TimestampSupportData.getAddressAPI());
            new GetDataSensor().execute(SensorSupportData.getAddressAPI());

            String clientId = MqttClient.generateClientId();
            client = new MqttAndroidClient(getActivity(), "tcp://broker.hivemq.com:1883", clientId);

            try {
                IMqttToken token = client.connect();
                token.setActionCallback(new IMqttActionListener() {
                    @Override
                    public void onSuccess(IMqttToken asyncActionToken) {
                        Log.d("mqtt", "connected");
                    }

                    @Override
                    public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                        Toast.makeText(getActivity(), "ERROR connecting to MQTT broker", Toast.LENGTH_SHORT).show();
                        Log.d("mqtt", "ERROR connecting to MQTT broker");
                    }
                });
            } catch (MqttException e) {
                e.printStackTrace();
            }

            feedMeButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String payload = "release the food";
                    byte[] encodedPayload = new byte[0];
                    try {
                        encodedPayload = payload.getBytes("UTF-8");
                        MqttMessage message = new MqttMessage(encodedPayload);
                        client.publish(TOPIC, message);
                        new GetDataTimestamps().execute(TimestampSupportData.getAddressAPI());
                        new GetDataSensor().execute(SensorSupportData.getAddressAPI());
                        Toast.makeText(getActivity(), FOOD_RELEASED, Toast.LENGTH_SHORT).show();
                    } catch (UnsupportedEncodingException | MqttException e) {
                        Toast.makeText(getActivity(), "Can't send to MQTT broker", Toast.LENGTH_SHORT).show();
                        e.printStackTrace();
                    }
                    new GetDataSensor().execute(SensorSupportData.getAddressAPI());
                }
            });
        } else {
            feedMeButton.setEnabled(false);
            feedMeButton.setAlpha(.5f);
            adapter = new TimestampAdapter(timestamps);
            timestampsLV.setAdapter(adapter);
            timestampsLV.setLayoutManager(new LinearLayoutManager(getActivity()));
            progressBar.setVisibility(View.GONE);
            messageTV.setText("Must be online to see this!");
        }
    }

    @Override
    public void onRefresh() {
        new Handler().postDelayed(new Runnable() {
            @Override public void run() {
                swipeRefreshLayout.setRefreshing(false);
                new GetDataTimestamps().execute(TimestampSupportData.getAddressAPI());
                new GetDataSensor().execute(SensorSupportData.getAddressAPI());
            }
        }, 5000);
    }

    class GetDataTimestamps extends AsyncTask<String, Void, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... params) {
            String stream = null;
            String urlString = params[0];

            HTTPDataHandler http = new HTTPDataHandler();
            stream = http.GetHTTPData(urlString);
            return stream;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            progressBar.setVisibility(View.GONE);

            Gson gson = new Gson();
            Type listType = new TypeToken<List<Timestamp>>() {
            }.getType();
            timestampsLV.setLayoutManager(new LinearLayoutManager(getActivity()));
            timestamps = gson.fromJson(s, listType); // parse to List
            Collections.reverse(timestamps);
            adapter.updateData(timestamps);
        }
    }

    class GetDataSensor extends AsyncTask<String, Void, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... params) {
            String stream = null;
            String urlString = params[0];

            HTTPDataHandler http = new HTTPDataHandler();
            stream = http.GetHTTPData(urlString);
            return stream;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            progressBar.setVisibility(View.GONE);

            Gson gson = new Gson();
            Type listType = new TypeToken<List<Sensor>>() {
            }.getType();
            List <Sensor> sensorItems = gson.fromJson(s, listType); // parse to List

            if(sensorItems.get(0).getFood().equals("empty")) {
                feedMeButton.setEnabled(false);
                feedMeButton.setAlpha(.5f);
                warningTV.setVisibility(View.VISIBLE);
            } else {
                feedMeButton.setEnabled(true);
                feedMeButton.setAlpha(1);
                warningTV.setVisibility(View.GONE);
            }
        }
    }


    public boolean isConnected(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netinfo = cm.getActiveNetworkInfo();

        if (netinfo != null && netinfo.isConnectedOrConnecting()) {
            android.net.NetworkInfo wifi = cm.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
            android.net.NetworkInfo mobile = cm.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);

            if ((mobile != null && mobile.isConnectedOrConnecting()) || (wifi != null && wifi.isConnectedOrConnecting()))
                return true;
            else return false;
        } else
            return false;
    }
}
