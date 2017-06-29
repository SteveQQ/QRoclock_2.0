package com.steveq.qroclock_20.presentation.activities;

import android.app.Activity;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.TimePicker;

import com.steveq.qroclock_20.R;
import com.steveq.qroclock_20.database.AlarmsRepository;
import com.steveq.qroclock_20.database.Repository;
import com.steveq.qroclock_20.model.Alarm;
import com.steveq.qroclock_20.presentation.adapters.AlarmsRecyclerViewAdapter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Created by Adam on 2017-06-26.
 */

public class MainActivityPresenterImpl implements MainActivityPresenter, TimePickerDialog.OnTimeSetListener {
    private static final String TAG = MainActivityPresenterImpl.class.getSimpleName();
    private static MainView mMainView;
    private RecyclerView.Adapter alarmsAdapter;
    private Repository repository;
    private MainActivityPresenterImpl instance;

    private MainActivityPresenterImpl(MainView mainView) {
        mMainView = mainView;
        this.repository = new AlarmsRepository((Activity)mainView);
    }

    public static MainActivityPresenterImpl getInstance(){
        if(this.instance == null){
            this.instance = new MainActivityPresenterImpl();
        }
        return this.instance;
    }

    @Override
    public void initView() {
        alarmsAdapter = new AlarmsRecyclerViewAdapter((Context) mMainView, repository.getAlarms());
        Log.d(TAG, repository.getAlarms().toString());
        mMainView.configRecyclerView(alarmsAdapter);
        if(alarmsAdapter.getItemCount() > 0){
            mMainView.showRecyclerView();
        } else if(alarmsAdapter.getItemCount() == 0){
            mMainView.hideRecyclerView();
        }

    }

    @Override
    public void showAddAlarmDialog() {
        mMainView.showAddAlarmDialog();
    }

    @Override
    public void showRingtoneDialog() {
        mMainView.showDaysDialog();
    }

    @Override
    public void collectDays() {
    }

    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        Alarm alarm = new Alarm();
        alarm.setTime(String.valueOf(hourOfDay) + ":" + String.valueOf(minute));
        alarm.setRingtone(((Context) mMainView).getResources().getString(R.string.default_ringtone_uri));
        alarm.setDaysRepeat(Collections.<String>emptyList());
        alarm.setActive(true);
        repository.createAlarm(alarm);
        ((AlarmsRecyclerViewAdapter)alarmsAdapter).setPayload(repository.getAlarms());
        alarmsAdapter.notifyDataSetChanged();
    }

    public static class RowClickListener implements View.OnClickListener{

        private Alarm selectedAlarm;

        public Alarm getSelectedAlarm() {
            return selectedAlarm;
        }

        public void setSelectedAlarm(Alarm selectedAlarm) {
            this.selectedAlarm = selectedAlarm;
        }

        @Override
        public void onClick(View v) {
            switch (v.getId()){
                case R.id.alarmTimeTextView:
                    break;
                case R.id.currentRingtoneTextView:
                    break;
                case R.id.repeatDaysTextView:
                    mMainView.showDaysDialog();
                    break;
                case R.id.activeCompatSwitch:
                    break;
                default:
                    break;
            }
        }
    }

    public static class DaysListener implements DialogInterface.OnMultiChoiceClickListener{
        private static List<String> mChosenDays;
        private final List<String> days = new ArrayList<String>(Arrays.asList(((Context) mMainView).getResources().getStringArray(R.array.days)));

        public DaysListener(){
            mChosenDays = new ArrayList<>();
        }

        public List<String> getChosenDays() {
            return mChosenDays;
        }

        public void setChosenDays(List<String> chosenDays) {
            chosenDays = chosenDays;
        }

        @Override
        public void onClick(DialogInterface dialog, int which, boolean isChecked) {
            if(isChecked){
                mChosenDays.add(days.get(which));
            } else if (mChosenDays.contains(days.get(which))){
                mChosenDays.remove(which);
            }
        }
    }

}
