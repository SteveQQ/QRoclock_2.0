package com.steveq.qroclock_20.service;

import android.app.Notification;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
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
    private boolean isWaking;
    private AlarmBasic alarmBasic;
    private BroadcastReceiver alarmStopReceiver;

    public class AlarmBasic{
        private Ringtone ringtone;
        private Long alarmId;

        public AlarmBasic(Ringtone ringtone, Long alarmId) {
            this.ringtone = ringtone;
            this.alarmId = alarmId;
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();
        alarmStopReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                isWaking = false;
            }
        };
        IntentFilter filter = new IntentFilter();
        filter.addAction(getResources().getString(R.string.stop_alarm_action));
        registerReceiver(alarmStopReceiver, filter);

        Log.d(TAG, " : SERVICE ON CREATE : ");
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
                    if(!alarmBasic.ringtone.isPlaying()){
                        alarmBasic.ringtone.play();
                        //volume ringtone up
                        audioManager.setStreamVolume(AudioManager.STREAM_RING, audioManager.getStreamMaxVolume(AudioManager.STREAM_RING), 0);
                    }
                }
                sendAlarmBackBroadcast();
                stopSelf();
            }
        });
    }

    private void sendAlarmBackBroadcast(){
        Intent intent = new Intent();
        intent.setAction(getResources().getString(R.string.finish_ringing_action));
        intent.putExtra(AlarmCreator.ALARM_ID, alarmBasic.alarmId);
        sendBroadcast(intent);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if(getResources().getString(R.string.start_service_action).equals(intent.getAction())){
            alarmBasic = new AlarmBasic(RingtoneManager.getRingtone(this, Uri.parse(intent.getStringExtra(AlarmCreator.RINGTONE_PLAY))),
                                        intent.getLongExtra(AlarmCreator.ALARM_ID, -1));
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
        unregisterReceiver(alarmStopReceiver);
        Log.d(TAG, ": SERVICE DESTROYED : ");
    }
}
