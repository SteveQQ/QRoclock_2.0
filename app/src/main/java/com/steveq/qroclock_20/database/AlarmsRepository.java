package com.steveq.qroclock_20.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.steveq.qroclock_20.model.Alarm;

import java.util.List;

/**
 * Created by Adam on 2017-06-19.
 */

public class AlarmsRepository implements Repository{
    private SQLiteDatabase database;
    private AlarmsSQLiteHelper dbHelper;

    public AlarmsRepository(Context context){
        dbHelper = AlarmsSQLiteHelper.getInstance(context);
    }

    @Override
    public void open() {
        database = dbHelper.getWritableDatabase();
    }

    @Override
    public void close() {
        database.close();
    }

    @Override
    public int createAlarm(Alarm alarm) {
        return 0;
    }

    @Override
    public List<Alarm> getAlarms() {
        return null;
    }

    @Override
    public Alarm getAlarmById() {
        return null;
    }

    @Override
    public Boolean updateAlarm(Alarm alarm) {
        return null;
    }

    @Override
    public Boolean deleteAlarm(Alarm alarm) {
        return null;
    }
}
