package com.steveq.qroclock_20.presentation.activities;

import android.app.FragmentManager;
import android.app.TimePickerDialog;
import android.icu.util.Calendar;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.steveq.qroclock_20.R;
import com.steveq.qroclock_20.presentation.dialogs.AddAlarmDialog;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MainActivity extends AppCompatActivity implements MainView {
    private RecyclerView alarmsRecyclerView;
    private TextView emptyTextView;
    private FloatingActionButton alarmFab;
    private MainActivityPresenter presenter;

    private static final String ADD_DIALOG = "add_dialog";

    private final View.OnClickListener fabClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            presenter.showAddAlarmDialog();
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

        presenter = new MainActivityPresenterImpl(this);
        presenter.initView();
    }

    @Override
    public void configRecyclerView(RecyclerView.Adapter adapter) {
        alarmsRecyclerView.setHasFixedSize(true);
        alarmsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
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
    public void showAddAlarmDialog() {
        SimpleDateFormat sdfHours = new SimpleDateFormat("HH");
        SimpleDateFormat sdfMinutes = new SimpleDateFormat("mm");
        TimePickerDialog tmd = new TimePickerDialog(this, (TimePickerDialog.OnTimeSetListener)presenter, Integer.valueOf(sdfHours.format(new Date())) + 1, Integer.valueOf(sdfMinutes.format(new Date())), true);
        tmd.show();
    }
}
