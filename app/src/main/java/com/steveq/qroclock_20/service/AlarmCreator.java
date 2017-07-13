package com.steveq.qroclock_20.service;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.icu.util.Calendar;
import android.util.Log;

import com.steveq.qroclock_20.R;
import com.steveq.qroclock_20.model.Alarm;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Adam on 2017-07-10.
 */

public class AlarmCreator {
    private static final String TAG = AlarmCreator.class.getSimpleName();
    public static final String RINGTONE_PLAY = "ringtone_to_play";
    public static final String ALARM_ID = "alarm_id";
    public static final String DAYS_REPEAT = "days_repeat";
    private AlarmManager alarmManager;
    private Map<Alarm, PendingIntent> activeIntents;
    private PendingIntent pendingIntent;
    private Context context;

    public AlarmCreator(Context context){
        this.context = context;
        alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        activeIntents = new HashMap<>();
    }

    public Map<Alarm, PendingIntent> getActiveIntents() {
        return activeIntents;
    }

    public void setActiveIntents(Map<Alarm, PendingIntent> activeIntents) {
        this.activeIntents = activeIntents;
    }

    public void registerAlarm(Alarm alarm){
        Long timeStamp = getTimeInMillis(alarm.getTime());
        Intent intent = new Intent(context, StartAlarmService.class);
        intent.setAction(context.getResources().getString(R.string.start_service_action));
        intent.putExtra(RINGTONE_PLAY, alarm.getRingtone());
        intent.putExtra(ALARM_ID, alarm.getId());
        intent.putExtra(DAYS_REPEAT, alarm.getDaysRepeat().toArray(new String[]{}));

        Log.d(TAG, " : previous intent : " + activeIntents.get(alarm));

        unregisterAlarm(alarm);

        activeIntents.put(alarm, PendingIntent.getService(context, (int)alarm.getId(), intent, PendingIntent.FLAG_UPDATE_CURRENT));
        Log.d(TAG, " : TIMESTAMP : " + timeStamp);
        Log.d(TAG, " : current intent : " + activeIntents.get(alarm));

        if(alarm.getDaysRepeat().isEmpty()){
            alarmManager.setExact(AlarmManager.RTC_WAKEUP, timeStamp, activeIntents.get(alarm));
        } else {
            alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, timeStamp, AlarmManager.INTERVAL_DAY, activeIntents.get(alarm));
        }
    }

    public void unregisterAlarm(Alarm alarm){
        if(activeIntents.containsKey(alarm)){
            alarmManager.cancel(activeIntents.get(alarm));
            activeIntents.remove(alarm);
        }
    }

    private long getTimeInMillis(String timeString){
        String[] subTimes = timeString.split(":");

        Calendar calendar = Calendar.getInstance();

        Log.d(TAG, " : HOUR SET : " + subTimes[0].trim());
        Log.d(TAG, " : MINUTE SET : " + subTimes[1].trim());

        calendar.setTimeInMillis(System.currentTimeMillis());

        if(isTomorrow(timeString)){
            calendar.set(Calendar.DAY_OF_MONTH, calendar.get(Calendar.DAY_OF_MONTH) + 1);
        }

        calendar.set(Calendar.HOUR_OF_DAY, Integer.valueOf(subTimes[0].trim()));
        calendar.set(Calendar.MINUTE, Integer.valueOf(subTimes[1].trim()));
        calendar.set(Calendar.SECOND, 0);

        return calendar.getTimeInMillis();
    }

    private boolean isTomorrow(String time){
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
        try {
            if (sdf.parse(time).before(sdf.parse(sdf.format(new Date())))) {
                return true;
            } else {
                return false;
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return false;
    }
}
