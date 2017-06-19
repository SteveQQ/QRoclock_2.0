package com.steveq.qroclock_20;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.steveq.qroclock_20.database.AlarmsRepository;
import com.steveq.qroclock_20.database.Repository;

public class MainActivity extends AppCompatActivity {
    private Repository repository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        repository = new AlarmsRepository(this);
        repository.open();
    }
}
