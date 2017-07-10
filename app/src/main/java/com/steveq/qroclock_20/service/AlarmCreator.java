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
import java.util.Date;

/**
 * Created by Adam on 2017-07-10.
 */

public class AlarmCreator {
    private static final String TAG = AlarmCreator.class.getSimpleName();
    public static final String RINGTONE_PLAY = "ringtone to play";
    private AlarmManager alarmManager;
    private PendingIntent pendingIntent;
    private Context context;

    public AlarmCreator(Context context){
        this.context = context;
        alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
    }

    public void registerAlarm(Alarm alarm){
        Long timeStamp = getTimeInMillis(alarm.getTime());
        Intent intent = new Intent(context, StartAlarmService.class);
        intent.setAction(context.getResources().getString(R.string.start_service_action));
        intent.putExtra(RINGTONE_PLAY, alarm.getRingtone());
        pendingIntent = PendingIntent.getService(context, 0, intent, PendingIntent.FLAG_ONE_SHOT);
        Log.d(TAG, " : TIMESTAMP : " + timeStamp);

        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, timeStamp, AlarmManager.INTERVAL_DAY, pendingIntent);
    }

    private long getTimeInMillis(String timeString){
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
        Date parsedTime = null;
        try {
            parsedTime = sdf.parse(timeString);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(parsedTime);
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);
        Log.d(TAG, " : HOUR SET : " + String.valueOf(calendar.get(Calendar.HOUR_OF_DAY)));
        Log.d(TAG, " : MINUTe SET : " + String.valueOf(calendar.get(Calendar.MINUTE)));
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE, minute);
        calendar.set(Calendar.SECOND, 0);

        return calendar.getTimeInMillis();
    }
}
