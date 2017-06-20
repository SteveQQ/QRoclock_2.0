package com.steveq.qroclock_20.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.steveq.qroclock_20.model.Alarm;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Adam on 2017-06-19.
 */

public class AlarmsRepository implements Repository{
    private SQLiteDatabase database;
    private AlarmsSQLiteHelper dbHelper;
    private String[] alarmProjection = {AlarmsContract.AlarmsEntry._ID,
                                        AlarmsContract.AlarmsEntry.COLUMN_TIME,
                                        AlarmsContract.AlarmsEntry.COLUMN_RINGTONE,
                                        AlarmsContract.AlarmsEntry.COLUMN_ACTIVE};

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
    public Alarm createAlarm(Alarm alarm) {
        open();

        ContentValues values = new ContentValues();
        values.put(AlarmsContract.AlarmsEntry.COLUMN_TIME, alarm.getTime());
        values.put(AlarmsContract.AlarmsEntry.COLUMN_RINGTONE, alarm.getRingtone());
        boolean alarmState = alarm.getActive();
        values.put(AlarmsContract.AlarmsEntry.COLUMN_ACTIVE, (alarmState) ? 1 : 0);
        long id = database.insert(AlarmsContract.AlarmsEntry.TABLE_NAME, null, values);
        close();

        alarm.setId(id);
        return alarm;
    }

    @Override
    public List<Alarm> getAlarms() {
        open();
        Cursor cursor = database.query(
                AlarmsContract.AlarmsEntry.TABLE_NAME,
                alarmProjection,
                null,
                null,
                null,
                null,
                null
        );
        List<Alarm> alarms = new ArrayList<>();
        while(cursor.moveToNext()){
            Alarm alarm = new Alarm();
            alarm.setId(cursor.getLong(cursor.getColumnIndex(AlarmsContract.AlarmsEntry._ID)));
            alarm.setTime(cursor.getString(cursor.getColumnIndex(AlarmsContract.AlarmsEntry.COLUMN_TIME)));
            alarm.setRingtone(cursor.getString(cursor.getColumnIndex(AlarmsContract.AlarmsEntry.COLUMN_RINGTONE)));
            int activeStatus = cursor.getInt(cursor.getColumnIndex(AlarmsContract.AlarmsEntry.COLUMN_ACTIVE));
            alarm.setActive(activeStatus == 0);
            alarms.add(alarm);
        }
        cursor.close();
        close();
        return alarms;
    }

    @Override
    public Alarm getAlarmById(long id) throws IllegalStateException{
        open();

        Cursor cursor = database.query(
                AlarmsContract.AlarmsEntry.TABLE_NAME,
                alarmProjection,
                AlarmsContract.AlarmsEntry._ID + " = ?",
                new String[]{String.valueOf(id)},
                null,
                null,
                null
        );

        List<Alarm> alarms = new ArrayList<>();
        while(cursor.moveToNext()){
            Alarm alarm = new Alarm();
            alarm.setId(cursor.getLong(cursor.getColumnIndex(AlarmsContract.AlarmsEntry._ID)));
            alarm.setTime(cursor.getString(cursor.getColumnIndex(AlarmsContract.AlarmsEntry.COLUMN_TIME)));
            alarm.setRingtone(cursor.getString(cursor.getColumnIndex(AlarmsContract.AlarmsEntry.COLUMN_RINGTONE)));
            int activeStatus = cursor.getInt(cursor.getColumnIndex(AlarmsContract.AlarmsEntry.COLUMN_ACTIVE));
            alarm.setActive(activeStatus == 0);
            alarms.add(alarm);
        }
        cursor.close();
        close();
        if(alarms.size() > 1){
            throw new IllegalStateException("Only one alarm should be returned from particular ID");
        } else {
            return alarms.get(0);
        }
    }

    @Override
    public Boolean updateAlarm(Alarm alarm) throws IllegalStateException{
        open();

        ContentValues values = new ContentValues();
        values.put(AlarmsContract.AlarmsEntry.COLUMN_TIME, alarm.getTime());
        values.put(AlarmsContract.AlarmsEntry.COLUMN_RINGTONE, alarm.getRingtone());
        boolean alarmState = alarm.getActive();
        values.put(AlarmsContract.AlarmsEntry.COLUMN_ACTIVE, (alarmState) ? 1 : 0);

        int affectedRows = database.update(AlarmsContract.AlarmsEntry.TABLE_NAME, values, AlarmsContract.AlarmsEntry._ID + " = ?", new String[]{String.valueOf(alarm.getId())});

        close();

        if(affectedRows > 1){
            throw new IllegalStateException("Only one alarm should be affected from particular ID");
        } else {
            return true;
        }
    }

    @Override
    public Boolean deleteAlarm(Alarm alarm) {
        open();
        int affectedRows = database.delete(AlarmsContract.AlarmsEntry.TABLE_NAME, AlarmsContract.AlarmsEntry._ID + "=?", new String[]{String.valueOf(alarm.getId())});
        close();
        return affectedRows > 1;
    }
}
