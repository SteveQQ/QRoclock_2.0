package com.steveq.qroclock_20.presentation.activities;

import android.app.Activity;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.res.Resources;
import android.database.Cursor;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.TimePicker;

import com.steveq.qroclock_20.R;
import com.steveq.qroclock_20.database.AlarmsRepository;
import com.steveq.qroclock_20.database.Repository;
import com.steveq.qroclock_20.model.Alarm;
import com.steveq.qroclock_20.presentation.adapters.AlarmsRecyclerViewAdapter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by Adam on 2017-06-26.
 */

public class MainActivityPresenterImpl implements MainActivityPresenter, TimePickerDialog.OnTimeSetListener {
    private static final String TAG = MainActivityPresenterImpl.class.getSimpleName();
    private MainView mainView;
    private RecyclerView.Adapter alarmsAdapter;
    private Repository repository;

    public MainActivityPresenterImpl(MainView mainView) {
        this.mainView = mainView;
        this.repository = new AlarmsRepository((Activity)mainView);
    }

    @Override
    public void initView() {
        alarmsAdapter = new AlarmsRecyclerViewAdapter((Context)mainView, repository.getAlarms());
        Log.d(TAG, repository.getAlarms().toString());
        mainView.configRecyclerView(alarmsAdapter);
        if(alarmsAdapter.getItemCount() > 0){
            mainView.showRecyclerView();
        } else if(alarmsAdapter.getItemCount() == 0){
            mainView.hideRecyclerView();
        }

    }

    @Override
    public void showAddAlarmDialog() {
        mainView.showAddAlarmDialog();
    }

    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        Alarm alarm = new Alarm();
        alarm.setTime(String.valueOf(hourOfDay) + ":" + String.valueOf(minute));
        alarm.setRingtone(((Context)mainView).getResources().getString(R.string.default_ringtone_uri));
        alarm.setDaysRepeat(Collections.<String>emptyList());
        alarm.setActive(true);
        repository.createAlarm(alarm);
        ((AlarmsRecyclerViewAdapter)alarmsAdapter).setPayload(repository.getAlarms());
        alarmsAdapter.notifyDataSetChanged();
    }


}
