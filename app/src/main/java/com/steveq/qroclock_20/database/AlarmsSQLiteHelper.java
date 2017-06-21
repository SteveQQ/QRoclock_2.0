package com.steveq.qroclock_20.database;

import android.content.Context;
import android.content.res.Resources;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.steveq.qroclock_20.R;

/**
 * Created by Adam on 2017-06-19.
 */

public class AlarmsSQLiteHelper extends SQLiteOpenHelper {
    public static final String DB_NAME = "alarms_db";
    public static final Integer DB_VERSION = 1;
    private static Context mContext;

    //CREATE TABLES
    private final String CREATE_ALARMS_TABLE = "CREATE TABLE IF NOT EXISTS " +
            AlarmsContract.AlarmsEntry.TABLE_NAME + " (" +
            AlarmsContract.AlarmsEntry._ID + " INTEGER PRIMARY KEY ON CONFLICT FAIL AUTOINCREMENT, " +
            AlarmsContract.AlarmsEntry.COLUMN_TIME + " TEXT, " +
            AlarmsContract.AlarmsEntry.COLUMN_RINGTONE + " TEXT, " +
            AlarmsContract.AlarmsEntry.COLUMN_ACTIVE + " INTEGER )";

    private final String CREATE_ALARMS_DAYS_TABLE = "CREATE TABLE IF NOT EXISTS " +
            AlarmsContract.AlarmsDaysEntry.TABLE_NAME+ " (" +
            AlarmsContract.AlarmsDaysEntry.COLUMN_ALARM_ID + " INTEGER, " +
            AlarmsContract.AlarmsDaysEntry.COLUMN_DAY + " TEXT CHECK(" +
            AlarmsContract.AlarmsDaysEntry.COLUMN_DAY + " == '" + mContext.getResources().getStringArray(R.array.days)[0] + "' OR " +
            AlarmsContract.AlarmsDaysEntry.COLUMN_DAY + " == '" + mContext.getResources().getStringArray(R.array.days)[1] + "' OR " +
            AlarmsContract.AlarmsDaysEntry.COLUMN_DAY + " == '" + mContext.getResources().getStringArray(R.array.days)[2] + "' OR " +
            AlarmsContract.AlarmsDaysEntry.COLUMN_DAY + " == '" + mContext.getResources().getStringArray(R.array.days)[3] + "' OR " +
            AlarmsContract.AlarmsDaysEntry.COLUMN_DAY + " == '" + mContext.getResources().getStringArray(R.array.days)[4] + "' OR " +
            AlarmsContract.AlarmsDaysEntry.COLUMN_DAY + " == '" + mContext.getResources().getStringArray(R.array.days)[5] + "' OR " +
            AlarmsContract.AlarmsDaysEntry.COLUMN_DAY + " == '" + mContext.getResources().getStringArray(R.array.days)[6] + "' ), " +
            "FOREIGN KEY(" + AlarmsContract.AlarmsDaysEntry.COLUMN_ALARM_ID + ") REFERENCES " + AlarmsContract.AlarmsEntry.TABLE_NAME + "(" + AlarmsContract.AlarmsEntry._ID + ")," +
            "PRIMARY KEY(" + AlarmsContract.AlarmsDaysEntry.COLUMN_ALARM_ID + "," + AlarmsContract.AlarmsDaysEntry.COLUMN_DAY + "))";


    private static AlarmsSQLiteHelper instance;

    public static AlarmsSQLiteHelper getInstance(Context context){
        mContext = context;
        if(instance == null){
            instance = new AlarmsSQLiteHelper(context);
            return instance;
        }
        return instance;
    }

    private AlarmsSQLiteHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_ALARMS_TABLE);
        db.execSQL(CREATE_ALARMS_DAYS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
