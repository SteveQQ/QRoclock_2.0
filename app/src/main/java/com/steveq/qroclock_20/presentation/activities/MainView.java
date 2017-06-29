package com.steveq.qroclock_20.presentation.activities;

import android.support.v7.widget.RecyclerView;

/**
 * Created by Adam on 2017-06-26.
 */

public interface MainView {
    void configRecyclerView(RecyclerView.Adapter adapter);
    void showRecyclerView();
    void hideRecyclerView();
    void showAddAlarmDialog();
    void showDaysDialog();
}
