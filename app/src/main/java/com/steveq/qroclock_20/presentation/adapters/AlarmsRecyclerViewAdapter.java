package com.steveq.qroclock_20.presentation.adapters;

import android.content.Context;
import android.content.res.Resources;
import android.database.Cursor;
import android.media.RingtoneManager;
import android.net.Uri;
import android.provider.OpenableColumns;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SwitchCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.steveq.qroclock_20.R;
import com.steveq.qroclock_20.database.AlarmsRepository;
import com.steveq.qroclock_20.database.Repository;
import com.steveq.qroclock_20.model.Alarm;
import com.steveq.qroclock_20.presentation.activities.MainActivityPresenterImpl;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by Adam on 2017-06-21.
 */

public class AlarmsRecyclerViewAdapter extends RecyclerView.Adapter<AlarmsRecyclerViewAdapter.RCViewHolder>{
    private static final String TAG = AlarmsRecyclerViewAdapter.class.getSimpleName();
    private Context context;
    private List<Alarm> payload;

    public AlarmsRecyclerViewAdapter(Context ctx, List<Alarm> alarms){
        this.payload = alarms;
        this.context = ctx;
    }

    public List<Alarm> getPayload() {
        return payload;
    }

    public void setPayload(List<Alarm> payload) {
        this.payload = payload;
    }

    @Override
    public RCViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.alarm_row, parent, false);
        return new RCViewHolder(v);
    }

    @Override
    public void onBindViewHolder(RCViewHolder holder, int position) {
        holder.alarmTimeTextView.setText(payload.get(position).getTime());
        Log.d(TAG, payload.get(position).toString());
        Log.d(TAG, payload.get(position).getDaysRepeat().toString());
        String daysDescription = createDaysDescription(payload.get(position));
        holder.repeatDaysTextView.setText(daysDescription);
        holder.activeCompatSwitch.setChecked(payload.get(position).getActive());
        MainActivityPresenterImpl.RowClickListener listener = new MainActivityPresenterImpl.RowClickListener();
        listener.setSelectedAlarm(payload.get(position));
        holder.repeatDaysTextView.setOnClickListener(listener);
        Log.d(TAG, "content:/" + payload.get(position).getRingtone());

        Uri ringtoneUri = Uri.parse("content://media" + payload.get(position).getRingtone());
        Log.d(TAG, RingtoneManager.getRingtone(context, ringtoneUri).getTitle(context));
        holder.currentRingtoneTextView.setText(RingtoneManager.getRingtone(context, ringtoneUri).getTitle(context));

    }

    @Override
    public int getItemCount() {
        return payload.size();
    }

    public static class RCViewHolder extends RecyclerView.ViewHolder{

        TextView alarmTimeTextView;
        TextView repeatDaysTextView;
        SwitchCompat activeCompatSwitch;
        TextView currentRingtoneTextView;

        public RCViewHolder(View itemView) {
            super(itemView);
            alarmTimeTextView = (TextView) itemView.findViewById(R.id.alarmTimeTextView);
            repeatDaysTextView = (TextView) itemView.findViewById(R.id.repeatDaysTextView);
            activeCompatSwitch = (SwitchCompat) itemView.findViewById(R.id.activeCompatSwitch);
            currentRingtoneTextView = (TextView) itemView.findViewById(R.id.currentRingtoneTextView);
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

    private String createDaysDescription(Alarm alarm){
        List<String> days = alarm.getDaysRepeat();
        if(days.isEmpty()){
            String result;
            SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
            try {
                if(sdf.parse(alarm.getTime()).before(sdf.parse(sdf.format(new Date())))){
                    return result = context.getResources().getString(R.string.tommorow);
                } else {
                    return result = context.getResources().getString(R.string.today);
                }
            } catch (ParseException e) {
                e.printStackTrace();
                return "";
            }
        } else {
            List<String> result = new ArrayList<>();
            for(String day : days){
                result.add(day.substring(0, 3).toLowerCase() + ".");
            }
            return joinList(result);
        }
    }
    //------ HELPERS ------//
}
