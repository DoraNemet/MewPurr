package com.home.dfundak.mewpurr.SupportData;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.home.dfundak.mewpurr.Class.Alarm;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class PreferencesManagement {

    public static String PREFS_FILE = "MyPreferences";

    public static void saveAlarms(Context context, ArrayList<Alarm> alarmsList) {
        SharedPreferences mPrefs = context.getSharedPreferences(PREFS_FILE, context.MODE_PRIVATE);
        SharedPreferences.Editor prefsEditor = mPrefs.edit();
        Gson gson = new Gson();
        String json = gson.toJson(alarmsList);
        prefsEditor.putString("myJson", json);
        prefsEditor.commit();
    }

    public static ArrayList<Alarm> loadAlarms(Context context) {
        ArrayList<Alarm> savedAlarms = new ArrayList<Alarm>();
        SharedPreferences mPrefs = context.getSharedPreferences(PREFS_FILE, context.MODE_PRIVATE);
        Gson gson = new Gson();
        String json = mPrefs.getString("myJson", "");
        if (json.isEmpty()) {
            savedAlarms = new ArrayList<Alarm>();
        } else {
            Type type = new TypeToken<List<Alarm>>() {
            }.getType();
            savedAlarms = gson.fromJson(json, type);
        }
        return savedAlarms;
    }
}
