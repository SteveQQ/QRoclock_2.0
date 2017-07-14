package com.steveq.qroclock_20.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.steveq.qroclock_20.database.AlarmsRepository;
import com.steveq.qroclock_20.database.Repository;
import com.steveq.qroclock_20.model.Alarm;
import com.steveq.qroclock_20.presentation.activities.MainActivityPresenter;
import com.steveq.qroclock_20.presentation.activities.MainActivityPresenterImpl;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Adam on 2017-07-09.
 */

public class AlarmFinishReceiver extends BroadcastReceiver {
    private static final String TAG = AlarmFinishReceiver.class.getSimpleName();
    private MainActivityPresenter presenter = MainActivityPresenterImpl.getInstance();
    private Repository repository = ((MainActivityPresenterImpl)presenter).getRepository();

    @Override
    public void onReceive(Context context, Intent intent) {
        Alarm alarm = repository.getAlarmById(intent.getLongExtra(AlarmCreator.ALARM_ID, -1));
        //If alarm exists yet (It can be deleted when alarm ringing)
        if(alarm.getId() > 0 && alarm.getDaysRepeat().isEmpty()){
            MainActivityPresenterImpl.stopAlarmService(alarm);
            alarm.setActive(false);
            repository.updateAlarm(alarm);
            MainActivityPresenterImpl.reloadDataInAdapter();
        }
    }
}
