package com.steveq.qroclock_20.database;

import android.provider.BaseColumns;

/**
 * Created by Adam on 2017-06-19.
 */

public final class AlarmsContract {

    //explicitly make constructor private to prevent class to be instantiated
    private AlarmsContract(){
        //intentionally empty}
    }

    public static class AlarmsEntry implements BaseColumns {
        public static final String TABLE_NAME = "alarms_table";
        public static final String COLUMN_TIME = "time";
        public static final String COLUMN_RINGTONE = "ringtone";
        public static final String COLUMN_ACTIVE = "active";
    }

    public static class AlarmsDaysEntry implements BaseColumns {
        public static final String TABLE_NAME = "alarms_days_table";
        public static final String COLUMN_ALARM_ID = "alarm_id";
        public static final String COLUMN_DAY = "day";
    }
}
