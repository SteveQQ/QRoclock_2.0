package com.steveq.qroclock_20.presentation.activities;

import android.app.FragmentManager;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.steveq.qroclock_20.R;
import com.steveq.qroclock_20.presentation.adapters.AlarmsRecyclerViewAdapter;
import com.steveq.qroclock_20.presentation.dialogs.AddAlarmDialog;

public class MainActivity extends AppCompatActivity implements MainView {
    private RecyclerView alarmsRecyclerView;
    private RecyclerView.Adapter alarmsAdapter;
    private TextView emptyTextView;
    private FloatingActionButton alarmFab;
    private MainActivityPresenter presenter;

    private static final String ADD_DIALOG = "add_dialog";

    private final View.OnClickListener fabClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            FragmentManager fm = getFragmentManager();
            AddAlarmDialog dialog = new AddAlarmDialog();
            dialog.show(fm, ADD_DIALOG);
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

        presenter = new MainActivityPresenterImpl();
    }

    @Override
    public void configRecyclerView() {
        alarmsRecyclerView.setHasFixedSize(true);
        alarmsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        alarmsAdapter = new AlarmsRecyclerViewAdapter(this);
    }

    @Override
    public void showRecyclerView() {
        CoordinatorLayout.LayoutParams params = (CoordinatorLayout.LayoutParams) alarmFab.getLayoutParams();
        alarmsRecyclerView.setVisibility(View.GONE);
        emptyTextView.setVisibility(View.VISIBLE);
        params.setAnchorId(R.id.emptyRecyclerViewReplacement);
        alarmFab.setLayoutParams(params);
    }

    @Override
    public void hideRecyclerView() {
        CoordinatorLayout.LayoutParams params = (CoordinatorLayout.LayoutParams) alarmFab.getLayoutParams();
        alarmsRecyclerView.setVisibility(View.VISIBLE);
        emptyTextView.setVisibility(View.GONE);
        params.setAnchorId(R.id.alarmsRecyclerView);
        alarmFab.setLayoutParams(params);
    }

    @Override
    public void showAddAlarmDialog() {

    }
}
