package com.steveq.qroclock_20.service;

import android.app.Notification;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.icu.util.Calendar;
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
import com.steveq.qroclock_20.model.Alarm;

import java.util.Arrays;

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
        private String[] daysRepeat;

        public AlarmBasic(Ringtone ringtone, Long alarmId, String[] daysRepeat) {
            this.ringtone = ringtone;
            this.alarmId = alarmId;
            this.daysRepeat = daysRepeat;
        }

        @Override
        public String toString() {
            return "AlarmBasic{" +
                    "ringtone=" + ringtone +
                    ", alarmId=" + alarmId +
                    ", daysRepeat=" + Arrays.toString(daysRepeat) +
                    '}';
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
                alarmBasic.ringtone.stop();
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
            alarmBasic = new AlarmBasic(RingtoneManager.getRingtone(this, Uri.parse("content://media" + intent.getStringExtra(AlarmCreator.RINGTONE_PLAY))),
                                        intent.getLongExtra(AlarmCreator.ALARM_ID, -1),
                                        intent.getStringArrayExtra(AlarmCreator.DAYS_REPEAT));
            Log.d(TAG, "ALARM BASIC : " + alarmBasic);
            Log.d(TAG, "ALARM BASIC RINGTONE : " + intent.getStringExtra(AlarmCreator.RINGTONE_PLAY));
            if(alarmBasic.daysRepeat.length == 0){
                playRingtone();
            } else if(currentDayMatch(alarmBasic.daysRepeat)){
                playRingtone();
            }
        }
        return Service.START_STICKY;
    }

    private boolean currentDayMatch(String[] daysRepeat) {
        Calendar calendar = Calendar.getInstance();
        int dayNum = calendar.get(Calendar.DAY_OF_WEEK);
        for(String day : daysRepeat){
            if(getDayNum(day) == dayNum){
                return true;
            }
        }
        return false;
    }

    private int getDayNum(String dayString){
        String[] configuredDatys = getResources().getStringArray(R.array.days);
        int counter = 1;
        for(String day : configuredDatys){
            if(dayString.equals(day)){
                break;
            }
            counter++;
        }
        counter++;
        if(counter==8) counter = 1;
        return counter;
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
