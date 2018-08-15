package com.home.dfundak.mewpurr;

import android.app.AlertDialog;
import android.app.Fragment;
import android.content.Context;
import android.content.DialogInterface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import java.io.UnsupportedEncodingException;

/**
 * Created by DoraF on 22/02/2018.
 */

public class HomeFragment extends Fragment {
    private static String FOOD_RELEASED = "Food released";
    private Button feedMeButton, sub;
    private TextView mess;

    private final String TOPIC = "mewpurr/food";
    private MqttAndroidClient client = new MqttAndroidClient(null, null, null);

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View layout = inflater.inflate(R.layout.home_fragment, container, false);
        initializeUI(layout);

        ImageView homeImage = getActivity().findViewById(R.id.home_image);
        homeImage.setAlpha(0.5f);
        ImageView statsImage = getActivity().findViewById(R.id.stats_image);
        statsImage.setAlpha(1f);
        ImageView alarmImage = getActivity().findViewById(R.id.alarm_image);
        alarmImage.setAlpha(1f);
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
        sub = layout.findViewById(R.id.sub);
        feedMeButton = layout.findViewById(R.id.feed_me_button);
        mess = layout.findViewById(R.id.message);

        if (isConnected(getActivity())) {

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

            sub.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int qos = 1;
                    try {
                        IMqttToken subToken = client.subscribe(TOPIC, qos);
                        subToken.setActionCallback(new IMqttActionListener() {
                            @Override
                            public void onSuccess(IMqttToken asyncActionToken) {
                                client.setCallback(new MqttCallback() {
                                    @Override
                                    public void connectionLost(Throwable cause) {
                                        Toast.makeText(getActivity(), "Lost connection", Toast.LENGTH_SHORT).show();
                                        Log.d("mqtt", "Lost connection");
                                    }

                                    @Override
                                    public void messageArrived(String topic, MqttMessage message) throws Exception {
                                        mess.setText(new String(message.getPayload()));
                                    }

                                    @Override
                                    public void deliveryComplete(IMqttDeliveryToken token) {

                                    }
                                });
                            }

                            @Override
                            public void onFailure(IMqttToken asyncActionToken,
                                                  Throwable exception) {
                                Toast.makeText(getActivity(), "ERROR connecting", Toast.LENGTH_SHORT).show();
                                Log.d("mqtt", "ERROR connecting");
                            }
                        });
                    } catch (MqttException e) {
                        e.printStackTrace();
                    }
                }
            });

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
            sub.setEnabled(false);
            feedMeButton.setEnabled(false);
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
