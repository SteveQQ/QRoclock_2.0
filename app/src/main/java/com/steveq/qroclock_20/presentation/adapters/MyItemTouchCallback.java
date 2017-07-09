package com.steveq.qroclock_20.presentation.adapters;

import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;

import com.steveq.qroclock_20.presentation.activities.MainActivityPresenter;

/**
 * Created by Adam on 2017-07-04.
 */

public class MyItemTouchCallback extends ItemTouchHelper.Callback {
    private static final String TAG = MyItemTouchCallback.class.getSimpleName();
    private final MainActivityPresenter presenter;

    public MyItemTouchCallback(MainActivityPresenter presenter) {
        this.presenter = presenter;
    }

    @Override
    public int getMovementFlags(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
        int swipeFlags = ItemTouchHelper.START | ItemTouchHelper.END;
        return makeMovementFlags(0, swipeFlags);
    }

    @Override
    public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
        return false;
    }

    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
        Log.d(TAG, "SWIPED");
        ((ItemTouchHelperListener)presenter).onItemDismiss(viewHolder.getAdapterPosition());
    }

    @Override
    public boolean isItemViewSwipeEnabled() {
        return true;
    }

    @Override
    public boolean isLongPressDragEnabled() {
        return false;
    }
}
