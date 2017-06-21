package com.steveq.qroclock_20.presentation.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by Adam on 2017-06-21.
 */

public class AlarmsRecyclerViewAdapter extends RecyclerView.Adapter<AlarmsRecyclerViewAdapter.RCViewHolder>{

    @Override
    public RCViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return null;
    }

    @Override
    public void onBindViewHolder(RCViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return 0;
    }

    public class RCViewHolder extends RecyclerView.ViewHolder{
        public RCViewHolder(View itemView) {
            super(itemView);
        }
    }
}
