package com.steveq.qroclock_20.presentation.activities;

import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.steveq.qroclock_20.R;
import com.steveq.qroclock_20.database.AlarmsRepository;
import com.steveq.qroclock_20.database.Repository;
import com.steveq.qroclock_20.presentation.adapters.AlarmsRecyclerViewAdapter;

public class MainActivity extends AppCompatActivity {
    private RecyclerView alarmsRecyclerView;
    private RecyclerView.Adapter alarmsAdapter;
    private TextView emptyTextView;
    private FloatingActionButton alarmFab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        alarmsRecyclerView = (RecyclerView) findViewById(R.id.alarmsRecyclerView);
        emptyTextView = (TextView) findViewById(R.id.emptyRecyclerViewReplacement);
        alarmFab = (FloatingActionButton) findViewById(R.id.addAlarmFab);
        alarmsRecyclerView.setHasFixedSize(true);
        alarmsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        alarmsAdapter = new AlarmsRecyclerViewAdapter(this);
        CoordinatorLayout.LayoutParams params = (CoordinatorLayout.LayoutParams) alarmFab.getLayoutParams();
        if(alarmsAdapter.getItemCount() == 0){
            alarmsRecyclerView.setVisibility(View.GONE);
            emptyTextView.setVisibility(View.VISIBLE);
            params.setAnchorId(R.id.emptyRecyclerViewReplacement);
        } else {
            alarmsRecyclerView.setVisibility(View.VISIBLE);
            emptyTextView.setVisibility(View.GONE);
            params.setAnchorId(R.id.alarmsRecyclerView);
        }
        alarmFab.setLayoutParams(params);
    }
}
