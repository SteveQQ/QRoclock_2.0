package com.steveq.qroclock_20.presentation.activities;

import com.steveq.qroclock_20.model.Alarm;

/**
 * Created by Adam on 2017-06-26.
 */

public interface MainActivityPresenter {
    void initView();
    void showAddAlarmDialog(Alarm initAlarm);
    void collectDays(Alarm alarm, MainActivityPresenterImpl.DaysListener listener);
    void collectRingtone(String path);
}
