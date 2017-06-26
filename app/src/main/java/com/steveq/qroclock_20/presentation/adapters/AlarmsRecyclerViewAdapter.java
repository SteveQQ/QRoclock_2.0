package com.steveq.qroclock_20.presentation.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SwitchCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.steveq.qroclock_20.R;
import com.steveq.qroclock_20.database.AlarmsRepository;
import com.steveq.qroclock_20.database.Repository;
import com.steveq.qroclock_20.model.Alarm;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Adam on 2017-06-21.
 */

public class AlarmsRecyclerViewAdapter extends RecyclerView.Adapter<AlarmsRecyclerViewAdapter.RCViewHolder>{
    private List<Alarm> payload;
    private Repository repository;

    public AlarmsRecyclerViewAdapter(Context context){
        repository = new AlarmsRepository(context);
        payload = repository.getAlarms();

    }

    private void updatePayload(){
        payload = repository.getAlarms();
        this.notifyDataSetChanged();
    }

    @Override
    public RCViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.alarm_row, parent, false);
        return new RCViewHolder(v);
    }

    @Override
    public void onBindViewHolder(RCViewHolder holder, int position) {
        holder.alarmTimeTextView.setText(payload.get(position).getTime());
        List<String> daysRepeatShorts = createShortsOfDays(payload.get(position).getDaysRepeat());
        holder.repeatDaysTextView.setText(joinList(daysRepeatShorts));
        holder.activeCompatSwitch.setChecked(payload.get(position).getActive());
    }

    @Override
    public int getItemCount() {
        return payload.size();
    }

    public static class RCViewHolder extends RecyclerView.ViewHolder{

        TextView alarmTimeTextView;
        TextView repeatDaysTextView;
        SwitchCompat activeCompatSwitch;

        public RCViewHolder(View itemView) {
            super(itemView);
            alarmTimeTextView = (TextView) itemView.findViewById(R.id.alarmTimeTextView);
            repeatDaysTextView = (TextView) itemView.findViewById(R.id.repeatDaysTextView);
            activeCompatSwitch = (SwitchCompat) itemView.findViewById(R.id.activeCompatSwitch);
        }
    }

    //------ HELPERS ------//
    private String joinList(List<String> list){
        StringBuilder builder = new StringBuilder();
        for(String el : list){
            builder.append(el);
            if(!(list.lastIndexOf(el) == list.size()-1)){
                builder.append(",");
            }
        }
        return builder.toString();
    }

    private List<String> createShortsOfDays(List<String> days){
        List<String> result = new ArrayList<>();
        for(String day : days){
            result.add(day.substring(0, 3).toLowerCase() + ".");
        }
        return result;
    }
    //------ HELPERS ------//
}
