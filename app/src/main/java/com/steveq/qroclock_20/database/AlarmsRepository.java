package com.steveq.qroclock_20.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.steveq.qroclock_20.model.Alarm;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by Adam on 2017-06-19.
 */

public class AlarmsRepository implements Repository{
    private static final String TAG = AlarmsRepository.class.getSimpleName();
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
        if(alarm.getDaysRepeat().size() != 0){
            createAlarmDay(alarm,alarm.getDaysRepeat());
        }
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
        Map<Long, Alarm> alarms = new HashMap();
        while(cursor.moveToNext()){
            Alarm alarm = new Alarm();
            alarm.setId(cursor.getLong(cursor.getColumnIndex(AlarmsContract.AlarmsEntry._ID)));
            alarm.setTime(cursor.getString(cursor.getColumnIndex(AlarmsContract.AlarmsEntry.COLUMN_TIME)));
            alarm.setRingtone(cursor.getString(cursor.getColumnIndex(AlarmsContract.AlarmsEntry.COLUMN_RINGTONE)));
            int activeStatus = cursor.getInt(cursor.getColumnIndex(AlarmsContract.AlarmsEntry.COLUMN_ACTIVE));
            alarm.setActive(activeStatus != 0);
            alarms.put(alarm.getId(), alarm);
        }
        cursor.close();
        close();
        for(Long id : alarms.keySet()){
            alarms.get(id).setDaysRepeat(getDaysForAlarm(id));
        }
        List<Alarm> result = new ArrayList<>(alarms.values());
        Collections.sort(result);
        Log.d(TAG, "ALARMS FROM DB : " + result);
        return result;
    }

    @Override
    public Alarm getAlarmByTime(String time) {
        open();

        Cursor cursor = database.query(
                AlarmsContract.AlarmsEntry.TABLE_NAME,
                alarmProjection,
                AlarmsContract.AlarmsEntry.COLUMN_TIME + " = ?",
                new String[]{time},
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
        } else if(alarms.isEmpty()){
            return new Alarm();
        }
        return alarms.get(0);
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

    @Override
    public List<Long> createAlarmDay(Alarm alarm, List<String> days) {
        List<Long> result = new ArrayList<>();
        open();
        for(String day : days){
            ContentValues values = new ContentValues();
            values.put(AlarmsContract.AlarmsDaysEntry.COLUMN_ALARM_ID, alarm.getId());
            values.put(AlarmsContract.AlarmsDaysEntry.COLUMN_DAY, day);
            result.add(database.insert(AlarmsContract.AlarmsDaysEntry.TABLE_NAME, null, values));
        }
        alarm.setDaysRepeat(days);
        close();
        return result;
    }

    @Override
    public List<String> getDaysForAlarm(long alarmId) {
        open();

        Cursor cursor = database.query(
                AlarmsContract.AlarmsDaysEntry.TABLE_NAME,
                new String[]{AlarmsContract.AlarmsDaysEntry.COLUMN_DAY},
                AlarmsContract.AlarmsDaysEntry.COLUMN_ALARM_ID + " = ?",
                new String[]{String.valueOf(alarmId)},
                null,
                null,
                null
        );

        List<String> days = new ArrayList<>();
        while(cursor.moveToNext()){
            days.add(cursor.getString(cursor.getColumnIndex(AlarmsContract.AlarmsDaysEntry.COLUMN_DAY)));
        }
        cursor.close();
        close();
        return days;
    }

    private class UpdatePackage{
        private List<String> toAdd;
        private List<String> toDelete;

        public UpdatePackage(List<String> toAdd, List<String> toDelete) {
            this.toAdd = toAdd;
            this.toDelete = toDelete;
        }

        public List<String> getToAdd() {
            return toAdd;
        }

        public void setToAdd(List<String> toAdd) {
            this.toAdd = toAdd;
        }

        public List<String> getToDelete() {
            return toDelete;
        }

        public void setToDelete(List<String> toDelete) {
            this.toDelete = toDelete;
        }
    }

    private UpdatePackage getUpdatePackage(List<String> oldDays, List<String> daysToUpdate){
        List<String> newDays = new ArrayList<>();
        for(String dayToUpdate : daysToUpdate){
            int i = 0;
            for(String oldDay : oldDays){
                System.err.println("DAY TO UPDATE : " + dayToUpdate);
                System.err.println("OLD DAY : " + oldDay);
                if(dayToUpdate.equals(oldDay)){
                    oldDays.remove(i);
                    i = -1;
                    break;
                }
                i++;
            }
            if(i != -1){
                newDays.add(dayToUpdate);
            }
        }
        return new UpdatePackage(newDays, oldDays);
    }

    @Override
    public void updateAlarmDays(Alarm alarm, List<String> days) {
        List<String> currentDays = getDaysForAlarm(alarm.getId());
        UpdatePackage updatePackage = getUpdatePackage(currentDays, days);

        System.err.println("TO DELETE : " + updatePackage.getToDelete());
        System.err.println("TO ADD : " + updatePackage.getToAdd());

        deleteAlarmDayEntries(alarm.getId(), updatePackage.getToDelete());
        createAlarmDay(alarm, updatePackage.getToAdd());
    }

    @Override
    public Boolean deleteAlarmDays(long alarmId) {
        open();
        int affectedRows = database.delete(AlarmsContract.AlarmsDaysEntry.TABLE_NAME, AlarmsContract.AlarmsDaysEntry.COLUMN_ALARM_ID + "=?", new String[]{String.valueOf(alarmId)});
        close();
        return affectedRows > 1;
    }

    @Override
    public Boolean deleteAlarmDayEntries(long alarmId, List<String> days) {
        open();
        boolean deleted = false;
        for(String day : days){
            int affectedRows = database.delete(AlarmsContract.AlarmsDaysEntry.TABLE_NAME, AlarmsContract.AlarmsDaysEntry.COLUMN_ALARM_ID + "=? and " + AlarmsContract.AlarmsDaysEntry.COLUMN_DAY + "=?", new String[]{String.valueOf(alarmId), day});
            if(affectedRows > 0) deleted = true ;
        }
        close();
        return deleted;
    }
}
