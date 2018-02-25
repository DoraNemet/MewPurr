package com.home.dfundak.mewpurr;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;

/**
 * Created by DoraF on 25/02/2018.
 */

public class AlarmDBHelper extends SQLiteOpenHelper {
    private static AlarmDBHelper mAlarmDBHelper = null;

    private AlarmDBHelper(Context context) {
        super(context.getApplicationContext(), Schema.DATABASE_NAME, null, Schema.SCHEMA_VERSION);
    }

    public static synchronized AlarmDBHelper getInstance(Context context) {
        if (mAlarmDBHelper == null) {
            mAlarmDBHelper = new AlarmDBHelper(context);
        }
        return mAlarmDBHelper;
    }

    //SQL statements
    static final String CREATE_TABLE_ALARMS = "CREATE TABLE " + Schema.TABLE_ALARMS + " (" + Schema.TIME + " TEXT);";
    static final String DROP_TABLE_ALARMS = "DROP TABLE IF EXISTS " + Schema.TABLE_ALARMS;
    static final String SELECT_ALL_ALARMS = "SELECT " + Schema.TIME + " FROM " + Schema.TABLE_ALARMS;

    @Override
    public void onCreate(SQLiteDatabase database) {
        database.execSQL(CREATE_TABLE_ALARMS);
    }

    @Override
    public void onUpgrade(SQLiteDatabase database, int oldVersion, int newVersion) {
        database.execSQL(DROP_TABLE_ALARMS);
        this.onCreate(database);
    }

    public void insertAlarm(Alarm alarm) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(Schema.TIME, alarm.getTime());
        SQLiteDatabase writeableDatabase = this.getWritableDatabase();
        writeableDatabase.insert(Schema.TABLE_ALARMS, Schema.TIME, contentValues);
        writeableDatabase.close();
    }

    public ArrayList<Alarm> getAllAlarms() {
        SQLiteDatabase writeableDatabase = this.getWritableDatabase();
        Cursor alarmCursor = writeableDatabase.rawQuery(SELECT_ALL_ALARMS, null);
        ArrayList<Alarm> alarms = new ArrayList<>();
        if (alarmCursor.moveToFirst()) {
            do {
                String time = alarmCursor.getString(0);
                alarms.add(new Alarm(time));
                Log.d("alarm", time);
            } while (alarmCursor.moveToNext());
        }
        alarmCursor.close();
        writeableDatabase.close();
        return alarms;
    }

    public static class Schema {
        private static final int SCHEMA_VERSION = 1;
        private static final String DATABASE_NAME = "alarms.db";
        static final String TABLE_ALARMS = "alarms";
        static final String TIME = "time";
    }
}
