package com.home.dfundak.mewpurr;

import android.app.AlertDialog;
import android.app.Fragment;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
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
import com.home.dfundak.mewpurr.Class.Alarm;
import com.home.dfundak.mewpurr.Class.Timestamp;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by DoraF on 22/02/2018.
 */

public class HomeFragment extends Fragment {
    private static String FOOD_RELEASED = "Food released";
    private Button feedMeButton;
    private RecyclerView timestampsLV;
    private ProgressBar progressBar;
    private TextView messageTV;

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

        if (isConnected(getActivity())) {
            adapter = new TimestampAdapter(timestamps);
            timestampsLV.setAdapter(adapter);
            timestampsLV.setLayoutManager(new LinearLayoutManager(getActivity()));

            new GetData().execute(TimestampSupportData.getAddressAPI());

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
                        Toast.makeText(getActivity(), FOOD_RELEASED, Toast.LENGTH_SHORT).show();
                    } catch (UnsupportedEncodingException | MqttException e) {
                        Toast.makeText(getActivity(), "Can't send to MQTT broker", Toast.LENGTH_SHORT).show();
                        e.printStackTrace();
                    }
                }
            });
        } else {
            feedMeButton.setEnabled(false);
            adapter = new TimestampAdapter(timestamps);
            timestampsLV.setAdapter(adapter);
            timestampsLV.setLayoutManager(new LinearLayoutManager(getActivity()));
            progressBar.setVisibility(View.GONE);
            messageTV.setText("Must be online to see this!");
        }
    }

    class GetData extends AsyncTask<String, Void, String> {

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
            adapter.updateData(timestamps);
            //PreferencesManagement.saveAlarms(getActivity(), timestamps);
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
