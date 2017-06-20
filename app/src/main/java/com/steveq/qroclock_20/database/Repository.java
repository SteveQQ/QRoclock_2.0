package com.steveq.qroclock_20.database;

import com.steveq.qroclock_20.model.Alarm;

import java.util.List;

/**
 * Created by Adam on 2017-06-19.
 */

public interface Repository {
    void open();
    void close();
    Alarm createAlarm(Alarm alarm);
    List<Alarm> getAlarms();
    Alarm getAlarmById(long id);
    Boolean updateAlarm(Alarm alarm);
    Boolean deleteAlarm(Alarm alarm);
}
