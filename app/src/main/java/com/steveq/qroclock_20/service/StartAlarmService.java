package com.steveq.qroclock_20.service;

import android.app.Notification;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.IntDef;
import android.support.annotation.Nullable;
import android.util.Log;

import com.steveq.qroclock_20.R;

/**
 * Created by Adam on 2017-07-09.
 */

public class StartAlarmService extends Service {
    private static final String TAG = StartAlarmService.class.getSimpleName();
    private static final int FOREGROUND_ID = 1111;

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, " : SERVICE ON CREATE : ");
    }

    private Notification buildNotification(){
        Notification.Builder builder = new Notification.Builder(this)
                                            .setContentTitle(getResources().getString(R.string.app_name))
                                            .setContentText(getResources().getString(R.string.active_alarms))
                                            .setSmallIcon(R.drawable.alarm_vec)
                                            .setOngoing(true);
        return builder.build();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        startForeground(FOREGROUND_ID, buildNotification());
        if(getResources().getString(R.string.start_service_action).equals(intent.getAction())){
            Log.d(TAG, ": SERVICE STARTED : " + intent.getAction());
        }
        return Service.START_STICKY;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, ": SERVICE DESTROYED : ");
    }
}
