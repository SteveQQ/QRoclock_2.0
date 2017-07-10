package com.steveq.qroclock_20.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Adam on 2017-07-09.
 */

public class TimeTickReceiver extends BroadcastReceiver {
    private static final String TAG = TimeTickReceiver.class.getSimpleName();
    @Override
    public void onReceive(Context context, Intent intent) {
        if(intent.getAction().compareTo(Intent.ACTION_TIME_TICK) == 0){
            String time = getCurrentTime();
            Log.d(TAG, " : CURRENT TIME : " + time);
        }
    }

    private String getCurrentTime(){
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
        return sdf.format(new Date());
    }
}
