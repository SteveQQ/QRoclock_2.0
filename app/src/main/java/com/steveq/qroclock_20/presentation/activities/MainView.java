package com.steveq.qroclock_20.presentation.activities;

import android.support.v7.widget.RecyclerView;

import com.steveq.qroclock_20.model.Alarm;

/**
 * Created by Adam on 2017-06-26.
 */

public interface MainView {
    void configRecyclerView(RecyclerView.Adapter adapter);
    void showRecyclerView();
    void hideRecyclerView();
    void showAddAlarmDialog(Alarm initAlarm);
    void showDaysDialog(Alarm alarm);
}
