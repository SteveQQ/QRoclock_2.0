package com.steveq.qroclock_20.service;

import android.app.Notification;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.PowerManager;
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
    private Ringtone ringtone;
    private boolean isWaking;

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

    public void playRingtone(){
        isWaking = true;
        final AudioManager audioManager = (AudioManager) getSystemService(AUDIO_SERVICE);
        HandlerThread thread = new HandlerThread("QRoclock_waking");
        thread.start();
        Handler handler = new Handler(thread.getLooper());
        handler.post(new Runnable() {
            @Override
            public void run() {
                while(isWaking){
                    if(!ringtone.isPlaying()){
                        ringtone.play();
                        //volume ringtone up
                        audioManager.setStreamVolume(AudioManager.STREAM_RING, audioManager.getStreamMaxVolume(AudioManager.STREAM_RING), 0);
                    }
                }
            }
        });
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        startForeground(FOREGROUND_ID, buildNotification());
        if(getResources().getString(R.string.start_service_action).equals(intent.getAction())){
            ringtone = RingtoneManager.getRingtone(this, Uri.parse(intent.getStringExtra(AlarmCreator.RINGTONE_PLAY)));
            playRingtone();
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
        isWaking = false;
        Log.d(TAG, ": SERVICE DESTROYED : ");
    }
}
