package com.home.dfundak.mewpurr.Fragments;

import android.app.AlertDialog;
import android.app.Fragment;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.home.dfundak.mewpurr.Adapters.AlarmAdapter;
import com.home.dfundak.mewpurr.SupportData.AlarmSupportData;
import com.home.dfundak.mewpurr.Class.Alarm;
import com.home.dfundak.mewpurr.SupportData.HTTPDataHandler;
import com.home.dfundak.mewpurr.SupportData.PreferencesManagement;
import com.home.dfundak.mewpurr.R;
import com.home.dfundak.mewpurr.SupportData.RecyclerItemClickListener;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * Created by DoraF on 22/02/2018.
 */

//https://developer.android.com/reference/android/widget/TimePicker

public class AlarmFragment extends Fragment {
    private Button setTime;
    private RecyclerView alarmsLV;
    private ProgressBar progressBar;

    ArrayList<Alarm> alarms = new ArrayList<Alarm>();
    AlarmAdapter adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View layout = inflater.inflate(R.layout.alarm_fragment, container, false);
        initializeUI(layout);
        return layout;
    }

    private void initializeUI(View layout) {
        setTime = layout.findViewById(R.id.set_time);
        alarmsLV = layout.findViewById(R.id.alarms_list_view);
        progressBar = layout.findViewById(R.id.pbProgress);

        ImageView alarmImage = getActivity().findViewById(R.id.alarm_image);
        alarmImage.setImageResource(R.drawable.clock_yellow);
        ImageView statsImage = getActivity().findViewById(R.id.stats_image);
        statsImage.setImageResource(R.drawable.stats);
        ImageView homeImage = getActivity().findViewById(R.id.home_image);
        homeImage.setImageResource(R.drawable.home);

        if (isConnected(getActivity())) {
            adapter = new AlarmAdapter(alarms);
            alarmsLV.setAdapter(adapter);
            alarmsLV.setLayoutManager(new LinearLayoutManager(getActivity()));

            new GetData().execute(AlarmSupportData.getAddressAPI());
            this.setTime.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Calendar currentTime = Calendar.getInstance();
                    int hour = currentTime.get(Calendar.HOUR_OF_DAY);
                    int minute = currentTime.get(Calendar.MINUTE);

                    TimePickerDialog timePicker;
                    timePicker = new TimePickerDialog(getActivity(), new TimePickerDialog.OnTimeSetListener() {
                        @Override
                        public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                            if (timePicker.isShown()) {
                                setAlarm(selectedHour, selectedMinute);
                            }
                        }
                    }, hour, minute, true);
                    timePicker.setTitle("Select Time");
                    timePicker.show();
                }
            });

            alarmsLV.addOnItemTouchListener(new RecyclerItemClickListener(getActivity(), new RecyclerItemClickListener.OnItemClickListener() {
                @Override
                public void onItemClick(View view, int position) {
                    final int mpos = position;
                    AlertDialog alertDialog = new AlertDialog.Builder(getActivity())
                            .setIcon(android.R.drawable.ic_dialog_alert)
                            .setTitle("Are you sure you want to delete " + alarms.get(position).getTime() + " alarm?")
                            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    new DeleteData(alarms.get(mpos)).execute(AlarmSupportData.getAddressSingle(alarms.get(mpos)));
                                }
                            })
                            .setNegativeButton("No", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                }
                            })
                            .show();
                }
            }));

        } else {
            setTime.setEnabled(false);
            setTime.setAlpha(.5f);
            progressBar.setVisibility(View.GONE);
            alarms = PreferencesManagement.loadAlarms(getActivity());
            adapter = new AlarmAdapter(alarms);
            alarmsLV.setAdapter(adapter);
            alarmsLV.setLayoutManager(new LinearLayoutManager(getActivity()));
        }
    }

    private void setAlarm(int selectedHour, int selectedMinute) {
        StringBuilder timeString = new StringBuilder();
        if(selectedHour < 10){
            timeString.append("0");
        }
        timeString.append(selectedHour);
        timeString.append(":");
        if (selectedMinute < 10) {
            timeString.append("0");
        }
        timeString.append(selectedMinute);
        if (!alarms.stream().filter(x -> timeString.toString().equals(x.getTime())).findAny().isPresent()) {
            new PostData(timeString.toString()).execute(AlarmSupportData.getAddressAPI());
        } else {
            Toast.makeText(getActivity(), "This time already exists!", Toast.LENGTH_LONG).show();
        }
    }

    class PostData extends AsyncTask<String, String, String> {
        String alarmName;

        public PostData(String alarmName) {
            this.alarmName = alarmName;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected String doInBackground(String... params) {
            String urlString = params[0];

            HTTPDataHandler hh = new HTTPDataHandler();
            String json = "{\"time\":\"" + alarmName + "\"}";
            hh.PostHTTPData(urlString, json);
            return "";
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            new GetData().execute(AlarmSupportData.getAddressAPI());
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
            Type listType = new TypeToken<List<Alarm>>() {
            }.getType();
            alarmsLV.setLayoutManager(new LinearLayoutManager(getActivity()));
            alarms = gson.fromJson(s, listType); // parse to List
            adapter.updateData(alarms);
            PreferencesManagement.saveAlarms(getActivity(), alarms);
        }
    }

    class DeleteData extends AsyncTask<String, String, String> {
        Alarm alarm;

        public DeleteData(Alarm alarm) {
            this.alarm = alarm;
            progressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... params) {
            String urlString = params[0];

            HTTPDataHandler hh = new HTTPDataHandler();
            String json = "{\"alarm\":\"" + alarm.getTime() + "\"}";
            hh.DeleteHTTPData(urlString, json);
            return "";
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            new AlarmFragment.GetData().execute(AlarmSupportData.getAddressAPI());
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
