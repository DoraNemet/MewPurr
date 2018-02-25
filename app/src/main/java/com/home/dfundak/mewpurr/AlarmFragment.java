package com.home.dfundak.mewpurr;

import android.app.Fragment;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TimePicker;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Calendar;

/**
 * Created by DoraF on 22/02/2018.
 */

public class AlarmFragment extends Fragment {
    Button setTime;
    RecyclerView alarmsLV;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View layout = inflater.inflate(R.layout.alarm_fragment, container, false);
        initializeUI(layout);
        return layout;
    }

    private void initializeUI(View layout) {
        this.setTime = (Button) layout.findViewById(R.id.set_time);
        this.alarmsLV = (RecyclerView) layout.findViewById(R.id.alarms_list_view);

        this.alarmsLV.setLayoutManager(new LinearLayoutManager(getActivity()));

        ArrayList<Alarm> alarms = this.loadAlarms();
        AlarmAdapter adapter = new AlarmAdapter(alarms);
        this.alarmsLV.setAdapter(adapter);

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
                            Toast.makeText(getActivity(), selectedHour + ":" + selectedMinute, Toast.LENGTH_SHORT).show();
                            setAlarm(selectedHour, selectedMinute);
                        }
                    }
                }, hour, minute, true);
                timePicker.setTitle("Select Time");
                timePicker.show();
            }
        });
    }

    private void setAlarm(int selectedHour, int selectedMinute) {
        String time = selectedHour + ":" + selectedMinute;
        Alarm newAlarm = new Alarm(time);
        Log.d("dora", newAlarm.getTime());
        AlarmDBHelper.getInstance(getActivity()).insertAlarm(newAlarm);
        AlarmAdapter adapter = (AlarmAdapter) alarmsLV.getAdapter();
        adapter.insert(newAlarm);
    }

    private ArrayList<Alarm> loadAlarms() {
        return AlarmDBHelper.getInstance(getActivity()).getAllAlarms();
    }
}
