package com.steveq.qroclock_20.presentation.activities;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.TimePicker;

import com.steveq.qroclock_20.QRoclockApplication;
import com.steveq.qroclock_20.R;
import com.steveq.qroclock_20.database.AlarmsRepository;
import com.steveq.qroclock_20.database.Repository;
import com.steveq.qroclock_20.model.Alarm;
import com.steveq.qroclock_20.presentation.adapters.AlarmsRecyclerViewAdapter;
import com.steveq.qroclock_20.presentation.adapters.ItemTouchHelperListener;
import com.steveq.qroclock_20.scanner.ScannerCallback;
import com.steveq.qroclock_20.service.AlarmCreator;
import com.steveq.qroclock_20.service.StartAlarmService;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Created by Adam on 2017-06-26.
 */

public class MainActivityPresenterImpl implements MainActivityPresenter, ItemTouchHelperListener {
    private static final String TAG = MainActivityPresenterImpl.class.getSimpleName();
    public static final int GET_RINGTONEPICKER = 10;
    private static final int NOTIFICATION_ID = 1212;
    private static MainView mMainView;
    private static RecyclerView.Adapter alarmsAdapter;
    private static Repository repository;
    private static MainActivityPresenterImpl instance;
    private Alarm deletedAlarm;
    private static AlarmCreator alarmCreator;
    public static SharedPreferences sharedPreferences;
    public static SharedPreferences.Editor editor;

    private MainActivityPresenterImpl(MainView mainView) {
        mMainView = mainView;
        repository = new AlarmsRepository((Activity) mainView);
        alarmCreator = new AlarmCreator((Context) mMainView);
        sharedPreferences = ((Activity) mMainView).getSharedPreferences(((Activity) mMainView).getString(R.string.configured_qr), ((Activity) mMainView).MODE_PRIVATE);
        editor =sharedPreferences.edit();
    }

    public Repository getRepository() {
        return repository;
    }

    public void setRepository(Repository repository) {
        MainActivityPresenterImpl.repository = repository;
    }

    //according to android documentation it is not considered
    //bad practice, passing argument to singleton constructor
    public static MainActivityPresenterImpl getInstance(MainView mainView){
        if(instance == null){
            instance = new MainActivityPresenterImpl(mainView);
        }
        return instance;
    }

    public static MainActivityPresenterImpl getInstance(){
        if(instance != null){
            return instance;
        }
        throw new IllegalStateException("MainActivityPresenterImpl need to be first created for certain context using getInstance(Mainview)");
    }

    @Override
    public void initView() {
        alarmsAdapter = new AlarmsRecyclerViewAdapter((Activity) mMainView, repository.getAlarms());
        Log.d(TAG, repository.getAlarms().toString());
        mMainView.configRecyclerView(alarmsAdapter);
        if(alarmsAdapter.getItemCount() > 0){
            mMainView.showRecyclerView();
        } else if(alarmsAdapter.getItemCount() == 0){
            mMainView.hideRecyclerView();
        }
        mMainView.configToolbar();
    }

    @Override
    public void showAddAlarmDialog(Alarm initAlarm) {
        mMainView.showAddAlarmDialog(initAlarm);
    }

    @Override
    public void collectDays(Alarm alarm, DaysListener listener) {
        RowClickListener.lastClickedAlarm.setDaysRepeat(listener.mChosenDays);
        repository.updateAlarmDays(alarm, listener.mChosenDays);

        startAlarmService(RowClickListener.lastClickedAlarm);
        reloadDataInAdapter();
    }

    @Override
    public void collectRingtone(String path) {
        RowClickListener.lastClickedAlarm.setRingtone(path);
        repository.updateAlarm(RowClickListener.lastClickedAlarm);
        reloadDataInAdapter();
        startAlarmService(RowClickListener.lastClickedAlarm);
    }

    @Override
    public void restoreAlarm() {
        Log.d(TAG, "DELETED ALARM : " + deletedAlarm);
        deletedAlarm = repository.createAlarm(deletedAlarm);
        reloadDataInAdapter();
    }

    public static void reloadDataInAdapter(){
        ((AlarmsRecyclerViewAdapter)alarmsAdapter).setPayload(repository.getAlarms());
        alarmsAdapter.notifyDataSetChanged();
        if(alarmsAdapter.getItemCount() > 0){
            mMainView.showRecyclerView();
        }
    }

    @Override
    public void onItemDismiss(int position) {
        Alarm alarmToDelete = ((AlarmsRecyclerViewAdapter)alarmsAdapter).getPayload().get(position);
        deletedAlarm = repository.deleteAlarm(alarmToDelete);
        stopAlarmService(alarmToDelete);
        reloadDataInAdapter();
        mMainView.showSnackbar();
        if(alarmsAdapter.getItemCount() == 0){
            mMainView.hideRecyclerView();
        }
    }

    private static Notification buildNotification(){
        Notification.Builder builder = new Notification.Builder((Context)mMainView)
                .setContentTitle(((Context)mMainView).getResources().getString(R.string.app_name))
                .setContentText(((Context)mMainView).getResources().getString(R.string.active_alarms))
                .setSmallIcon(R.drawable.alarm_vec)
                .setOngoing(true);
        return builder.build();
    }

    private static void startAlarmService(Alarm alarm){
        alarmCreator.registerAlarm(alarm);
        if(alarmCreator.getActiveIntents().size() == 1){
            NotificationManager notificationManager = (NotificationManager) ((Context)mMainView).getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.notify(NOTIFICATION_ID, buildNotification());
        }
    }

    public static void stopAlarmService(Alarm alarm){
        alarmCreator.unregisterAlarm(alarm);
        if(alarmCreator.getActiveIntents().size() == 0){
            NotificationManager notificationManager = (NotificationManager) ((Context)mMainView).getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.cancel(NOTIFICATION_ID);
        }
    }

    public static class TimeListener implements TimePickerDialog.OnTimeSetListener{
        //alarm which was clicked when editing row or null object alarm when cerating new alarm
        private Alarm alarm;

        public TimeListener(Alarm a){
            alarm = a;
        }

        public Alarm getAlarm() {
            return alarm;
        }

        public void setAlarm(Alarm alarm) {
            this.alarm = alarm;
        }

        @Override
        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            String timeFormat = String.format("%02d", hourOfDay) + ":" + String.format("%02d", minute);
            //retrieves alarm object for set time
            Alarm alarmCheck = repository.getAlarmByTime(timeFormat);
            Log.d(TAG, "ALARM CHECK : " + alarm);
            if(alarm.getId() == 0 && alarmCheck.getId() == 0){
                if(sharedPreferences.contains(((Activity)mMainView).getString(R.string.QR))){
                    alarm.setTime(timeFormat);
                    alarm.setRingtone(((Context) mMainView).getResources().getString(R.string.default_ringtone_uri));
                    alarm.setDaysRepeat(Collections.<String>emptyList());
                    alarm.setActive(true);
                    alarm = repository.createAlarm(alarm);
                    startAlarmService(alarm);
                } else {
                    mMainView.showConfigureQrSnackbar();
                }
            } else if(alarm.getId() != 0){
                alarm.setTime(timeFormat);
                repository.updateAlarm(alarm);
                startAlarmService(alarm);
            }
            ((AlarmsRecyclerViewAdapter)alarmsAdapter).setPayload(repository.getAlarms());
            alarmsAdapter.notifyDataSetChanged();
            if(alarm.getId() > 0){
                mMainView.showRecyclerView();
            }
        }

    }

    //inner class bound to specific instance
    public static class RowClickListener implements View.OnClickListener{
        private static Alarm lastClickedAlarm;
        private Alarm alarm;

        public RowClickListener(){
        }

        public Alarm getAlarm() {
            return alarm;
        }

        public void setAlarm(Alarm alarm) {
            this.alarm = alarm;
        }

        @Override
        public void onClick(View v) {
            Log.d(TAG, "selected alarm : " + alarm);
            lastClickedAlarm = alarm;
            switch (v.getId()){
                case R.id.alarmTimeTextView:
                    mMainView.showAddAlarmDialog(alarm);
                    break;
                case R.id.currentRingtoneTextView:
                    Intent intent = new Intent(RingtoneManager.ACTION_RINGTONE_PICKER);
                    ((Activity)mMainView).startActivityForResult(intent, GET_RINGTONEPICKER);
                    break;
                case R.id.repeatDaysTextView:
                    mMainView.showDaysDialog(alarm);
                    break;
                case R.id.activeCompatSwitch:
                    alarm.setActive(!alarm.getActive());
                    repository.updateAlarm(alarm);
                    if(alarm.getActive()){
                        startAlarmService(alarm);
                    } else {
                        stopAlarmService(alarm);
                    }
                    break;
                default:
                    break;
            }
            reloadDataInAdapter();
        }
    }

    //inner class bound to specific instance
    public static class DaysListener implements DialogInterface.OnMultiChoiceClickListener{
        private List<String> mChosenDays;
        private List<String> days = new ArrayList<String>(Arrays.asList(((Context) mMainView).getResources().getStringArray(R.array.days)));

        public DaysListener(){
            mChosenDays = new ArrayList<>();
        }

        public List<String> getChosenDays() {
            return mChosenDays;
        }

        public void setChosenDays(List<String> days) {
            mChosenDays = days;
        }

        @Override
        public void onClick(DialogInterface dialog, int which, boolean isChecked) {
            Log.d(TAG, "CHOSEN DAY INDEX : " + which);
            if(isChecked){
                mChosenDays.add(days.get(which));
            } else if (mChosenDays.contains(days.get(which))){
                mChosenDays.remove(mChosenDays.indexOf(days.get(which)));
            }
            Log.d(TAG, "CURRENT CHOSEN DAYS : " + mChosenDays);
        }
    }

}
