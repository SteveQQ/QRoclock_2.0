package com.steveq.qroclock_20.model;

import java.util.List;

/**
 * Created by Adam on 2017-06-19.
 */

public class Alarm {
    private long id;
    private String time;
    private String ringtone;
    private Boolean active;
    private List<String> daysRepeat;

    public Alarm(){}

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getRingtone() {
        return ringtone;
    }

    public void setRingtone(String ringtone) {
        this.ringtone = ringtone;
    }

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    public List<String> getDaysRepeat() {
        return daysRepeat;
    }

    public void setDaysRepeat(List<String> daysRepeat) {
        this.daysRepeat = daysRepeat;
    }
}
