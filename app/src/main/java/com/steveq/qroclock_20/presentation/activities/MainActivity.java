package com.steveq.qroclock_20.presentation.activities;

import android.app.AlertDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.steveq.qroclock_20.R;
import com.steveq.qroclock_20.model.Alarm;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class MainActivity extends AppCompatActivity implements MainView {
    private static final String TAG = MainActivity.class.getSimpleName();
    private RecyclerView alarmsRecyclerView;
    private TextView emptyTextView;
    private FloatingActionButton alarmFab;
    private MainActivityPresenter presenter;

    private static final String ADD_DIALOG = "add_dialog";

    private final View.OnClickListener fabClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            presenter.showAddAlarmDialog(new Alarm());
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        alarmsRecyclerView = (RecyclerView) findViewById(R.id.alarmsRecyclerView);
        emptyTextView = (TextView) findViewById(R.id.emptyRecyclerViewReplacement);
        alarmFab = (FloatingActionButton) findViewById(R.id.addAlarmFab);

        alarmFab.setOnClickListener(fabClick);

        presenter = MainActivityPresenterImpl.getInstance(this);
        presenter.initView();
    }

    @Override
    public void configRecyclerView(RecyclerView.Adapter adapter) {
        alarmsRecyclerView.setHasFixedSize(true);
        alarmsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        alarmsRecyclerView.setAdapter(adapter);
    }

    @Override
    public void showRecyclerView() {
        CoordinatorLayout.LayoutParams params = (CoordinatorLayout.LayoutParams) alarmFab.getLayoutParams();
        alarmsRecyclerView.setVisibility(View.VISIBLE);
        emptyTextView.setVisibility(View.GONE);
        params.setAnchorId(R.id.alarmsRecyclerView);
        alarmFab.setLayoutParams(params);
    }

    @Override
    public void hideRecyclerView() {
        CoordinatorLayout.LayoutParams params = (CoordinatorLayout.LayoutParams) alarmFab.getLayoutParams();
        alarmsRecyclerView.setVisibility(View.GONE);
        emptyTextView.setVisibility(View.VISIBLE);
        params.setAnchorId(R.id.emptyRecyclerViewReplacement);
        alarmFab.setLayoutParams(params);
    }

    @Override
    public void showAddAlarmDialog(Alarm initAlarm) {
        SimpleDateFormat sdfHours = new SimpleDateFormat("HH");
        SimpleDateFormat sdfMinutes = new SimpleDateFormat("mm");
        TimePickerDialog.OnTimeSetListener listener = new MainActivityPresenterImpl.TimeListener(initAlarm);
        TimePickerDialog tmd = new TimePickerDialog(this, listener, Integer.valueOf(sdfHours.format(new Date())) + 1, Integer.valueOf(sdfMinutes.format(new Date())), true);
        tmd.show();
    }

    @Override
    public void showDaysDialog(final Alarm alarm) {
        List<String> daysToCheck = alarm.getDaysRepeat();
        String[] configuredDays = getResources().getStringArray(R.array.days);
        boolean[] checkedItems = new boolean[configuredDays.length];
        int i = 0;
        for(String day : configuredDays){
            if(daysToCheck.contains(day)){
                checkedItems[i] = true;
            } else {
                checkedItems[i] = false;
            }
            i++;

        }
        final MainActivityPresenterImpl.DaysListener listener = new MainActivityPresenterImpl.DaysListener();
        Log.d(TAG, "DAYS TO CHECK : " + daysToCheck);
        listener.setChosenDays(daysToCheck);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getResources().getString(R.string.ringtone))
                .setMultiChoiceItems(getResources().getStringArray(R.array.days), checkedItems, listener)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        presenter.collectDays(alarm, listener);
                    }
                })
                .setNegativeButton("CANCEL", null);
        builder.create().show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == MainActivityPresenterImpl.GET_RINGTONEPICKER && resultCode == RESULT_OK){
            Uri uri = data.getParcelableExtra(RingtoneManager.EXTRA_RINGTONE_PICKED_URI);
            presenter.collectRingtone(uri.getPath());
        }
    }
}
