package com.steveq.qroclock_20.presentation.activities;

import android.app.AlertDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.BaseTransientBottomBar;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.TextView;

import com.steveq.qroclock_20.R;
import com.steveq.qroclock_20.model.Alarm;
import com.steveq.qroclock_20.presentation.adapters.AlarmsRecyclerViewAdapter;
import com.steveq.qroclock_20.presentation.adapters.MyItemTouchCallback;

import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.zip.Inflater;

import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class MainActivity extends AppCompatActivity implements MainView {
    private static final String TAG = MainActivity.class.getSimpleName();
    private RecyclerView alarmsRecyclerView;
    private TextView emptyTextView;
    private FloatingActionButton alarmFab;
    private MainActivityPresenter presenter;
    private Toolbar mainToolbar;
    private CoordinatorLayout rootView;

    private static final String ADD_DIALOG = "add_dialog";

    private final View.OnClickListener fabClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            presenter.showAddAlarmDialog(new Alarm());
        }
    };

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        alarmsRecyclerView = (RecyclerView) findViewById(R.id.alarmsRecyclerView);
        emptyTextView = (TextView) findViewById(R.id.emptyRecyclerViewReplacement);
        alarmFab = (FloatingActionButton) findViewById(R.id.addAlarmFab);
        mainToolbar = (Toolbar) findViewById(R.id.mainToolbar);
        rootView = (CoordinatorLayout) findViewById(R.id.mainCoordinatorLayout);

        alarmFab.setOnClickListener(fabClick);

        presenter = MainActivityPresenterImpl.getInstance(this);
        presenter.initView();
    }

    @Override
    public void configRecyclerView(RecyclerView.Adapter adapter) {
        alarmsRecyclerView.setHasFixedSize(true);
        alarmsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        alarmsRecyclerView.setAdapter(adapter);
        ItemTouchHelper.Callback callback = new MyItemTouchCallback(presenter);
        ItemTouchHelper touchHelper = new ItemTouchHelper(callback);
        touchHelper.attachToRecyclerView(alarmsRecyclerView);
    }

    @Override
    public void configToolbar(){
        TextView title = (TextView) mainToolbar.findViewById(R.id.toolbarTitleTextView);
        setSupportActionBar(mainToolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
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
        tmd.getWindow().setBackgroundDrawableResource(R.color.material_teal_200);
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
        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.MyDialogTheme);
        builder.setTitle(getResources().getString(R.string.ringtone))
                .setMultiChoiceItems(getResources().getStringArray(R.array.days), checkedItems, listener)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        presenter.collectDays(alarm, listener);
                    }
                })
                .setNegativeButton("CANCEL", null);
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    @Override
    public void showSnackbar() {
        Snackbar snackbar = Snackbar.make(rootView, getResources().getString(R.string.delete_message), BaseTransientBottomBar.LENGTH_INDEFINITE)
                                    .setAction(getResources().getString(R.string.undo), new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            presenter.restoreAlarm();
                                        }
                                    });
        snackbar.show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == MainActivityPresenterImpl.GET_RINGTONEPICKER && resultCode == RESULT_OK){
            Uri uri = data.getParcelableExtra(RingtoneManager.EXTRA_RINGTONE_PICKED_URI);
            presenter.collectRingtone(uri.getPath());
        }
    }
}
