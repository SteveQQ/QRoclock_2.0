package com.steveq.qroclock_20;

import android.util.Log;

import com.steveq.qroclock_20.database.AlarmsRepository;
import com.steveq.qroclock_20.database.Repository;
import com.steveq.qroclock_20.model.Alarm;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.*;

/**
 * Created by Adam on 2017-06-20.
 */

@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class, sdk = 21)
public class AlarmRepositoryTest {
    private static String TAG = AlarmRepositoryTest.class.getSimpleName();
    Repository repository;
    @Before
    public void setUp() throws Exception {
         repository = new AlarmsRepository(RuntimeEnvironment.application);
    }

    @Test
    public void creatingAlarm(){
        Alarm testAlarm1 = new Alarm();
        testAlarm1.setTime("test_time");
        testAlarm1.setRingtone("test_ringtone");
        testAlarm1.setActive(true);
        testAlarm1.setDaysRepeat(Collections.<String>emptyList());
        testAlarm1 = repository.createAlarm(testAlarm1);

        assertEquals(1, testAlarm1.getId());
    }

    @Test
    public void gettingAlarms(){
        Alarm testAlarm1 = new Alarm();
        testAlarm1.setTime("test_time");
        testAlarm1.setRingtone("test_ringtone");
        testAlarm1.setActive(true);
        testAlarm1.setDaysRepeat(Collections.<String>emptyList());
        testAlarm1 = repository.createAlarm(testAlarm1);

        assertEquals(1, repository.getAlarms().size());
        assertEquals(testAlarm1.getId(), repository.getAlarms().get(0).getId());
    }

    @Test
    public void gettingAlarmByTime(){
        Alarm testAlarm1 = new Alarm();
        testAlarm1.setTime("test_time");
        testAlarm1.setRingtone("test_ringtone");
        testAlarm1.setActive(true);
        testAlarm1.setDaysRepeat(Collections.<String>emptyList());
        testAlarm1 = repository.createAlarm(testAlarm1);

        assertEquals(testAlarm1.getId(), repository.getAlarmByTime("test_time").getId());
    }

    @Test
    public void notGettingAlarmByTime(){
        Alarm testAlarm1 = new Alarm();
        testAlarm1.setTime("test_time");
        testAlarm1.setRingtone("test_ringtone");
        testAlarm1.setActive(true);
        testAlarm1.setDaysRepeat(Collections.<String>emptyList());
        testAlarm1 = repository.createAlarm(testAlarm1);

        assertEquals(0, repository.getAlarmByTime("abc").getId());
    }

    @Test
    public void gettingAlarmById(){
        Alarm testAlarm1 = new Alarm();
        testAlarm1.setTime("test_time");
        testAlarm1.setRingtone("test_ringtone");
        testAlarm1.setActive(true);
        testAlarm1.setDaysRepeat(Collections.<String>emptyList());
        testAlarm1 = repository.createAlarm(testAlarm1);

        assertEquals("test_time", repository.getAlarmById(1).getTime());
    }

    @Test
    public void updatingAlarm(){
        Alarm testAlarm1 = new Alarm();
        testAlarm1.setTime("test_time");
        testAlarm1.setRingtone("test_ringtone");
        testAlarm1.setActive(true);
        testAlarm1.setDaysRepeat(Collections.<String>emptyList());
        testAlarm1 = repository.createAlarm(testAlarm1);

        testAlarm1.setRingtone("updated_test_ringtone");
        repository.updateAlarm(testAlarm1);

        assertEquals("updated_test_ringtone", repository.getAlarmById(1).getRingtone());
    }

    @Test
    public void deletingAlarm(){
        Alarm testAlarm1 = new Alarm();
        testAlarm1.setTime("test_time");
        testAlarm1.setRingtone("test_ringtone");
        testAlarm1.setActive(true);
        testAlarm1.setDaysRepeat(Collections.<String>emptyList());
        testAlarm1 = repository.createAlarm(testAlarm1);

        repository.deleteAlarm(testAlarm1);

        assertEquals(0, repository.getAlarms().size());
    }

    @Test
    public void creatingAlarmDay(){
        Alarm testAlarm1 = new Alarm();
        testAlarm1.setTime("test_time");
        testAlarm1.setRingtone("test_ringtone");
        testAlarm1.setActive(true);
        testAlarm1.setDaysRepeat(Collections.<String>emptyList());
        testAlarm1 = repository.createAlarm(testAlarm1);

        List<Long> createdIds = repository.createAlarmDay(testAlarm1, new ArrayList<String>(Arrays.asList(new String[]{"MONDAY", "TUESDAY"})));

        System.err.println(TAG + " : CREATED IDS : " + createdIds);

        assertEquals(2, createdIds.size());
    }

    @Test
    public void creatingAlarmDayAgainstCheckConstraint(){
        Alarm testAlarm1 = new Alarm();
        testAlarm1.setTime("test_time");
        testAlarm1.setRingtone("test_ringtone");
        testAlarm1.setActive(true);
        testAlarm1.setDaysRepeat(Collections.<String>emptyList());
        testAlarm1 = repository.createAlarm(testAlarm1);

        List<Long> createdIds = repository.createAlarmDay(testAlarm1, new ArrayList<String>(Arrays.asList(new String[]{"WRONG"})));

        System.err.println(TAG + " : CREATED IDS : " + createdIds);

        assertEquals(1, createdIds.size());
        assertEquals(-1, createdIds.get(0).intValue());
    }

    @Test
    public void deletingAlarmDay(){
        Alarm testAlarm1 = new Alarm();
        testAlarm1.setTime("test_time");
        testAlarm1.setRingtone("test_ringtone");
        testAlarm1.setActive(true);
        testAlarm1.setDaysRepeat(Collections.<String>emptyList());
        testAlarm1 = repository.createAlarm(testAlarm1);

        List<Long> createdIds = repository.createAlarmDay(testAlarm1, new ArrayList<String>(Arrays.asList(new String[]{"MONDAY", "TUESDAY"})));
        repository.deleteAlarmDays(testAlarm1.getId());
        System.err.println(TAG + " : CREATED IDS : " + createdIds);

        assertEquals(0, repository.getDaysForAlarm(testAlarm1.getId()).size());
    }

    @Test
    public void deletingParticularAlarmDayEntry(){
        Alarm testAlarm1 = new Alarm();
        testAlarm1.setTime("test_time");
        testAlarm1.setRingtone("test_ringtone");
        testAlarm1.setActive(true);
        testAlarm1.setDaysRepeat(Collections.<String>emptyList());
        testAlarm1 = repository.createAlarm(testAlarm1);

        List<Long> createdIds = repository.createAlarmDay(testAlarm1, new ArrayList<String>(Arrays.asList(new String[]{"MONDAY", "TUESDAY"})));
        repository.deleteAlarmDayEntries(testAlarm1.getId(), new ArrayList<String>(Arrays.asList(new String[]{"MONDAY"})));
        System.err.println(TAG + " : CREATED IDS : " + createdIds);

        assertEquals(1, repository.getDaysForAlarm(testAlarm1.getId()).size());
    }

    @Test
    public void updatingAlarmDaysWithDaysWithCommonPart(){
        Alarm testAlarm1 = new Alarm();
        testAlarm1.setTime("test_time");
        testAlarm1.setRingtone("test_ringtone");
        testAlarm1.setActive(true);
        testAlarm1.setDaysRepeat(Collections.<String>emptyList());
        testAlarm1 = repository.createAlarm(testAlarm1);

        List<Long> createdIds = repository.createAlarmDay(testAlarm1, new ArrayList<String>(Arrays.asList(new String[]{"MONDAY", "TUESDAY"})));
        repository.updateAlarmDays(testAlarm1, new ArrayList<String>(Arrays.asList(new String[]{"MONDAY", "THURSDAY", "FRIDAY"})));
        System.err.println(TAG + " : UPDATED DAYS : " + repository.getDaysForAlarm(testAlarm1.getId()));

        assertEquals(3, repository.getDaysForAlarm(testAlarm1.getId()).size());
    }

    @Test
    public void updatingAlarmDaysWithDaysWithNoCommonPart(){
        Alarm testAlarm1 = new Alarm();
        testAlarm1.setTime("test_time");
        testAlarm1.setRingtone("test_ringtone");
        testAlarm1.setActive(true);
        testAlarm1.setDaysRepeat(Collections.<String>emptyList());
        testAlarm1 = repository.createAlarm(testAlarm1);

        List<Long> createdIds = repository.createAlarmDay(testAlarm1, new ArrayList<String>(Arrays.asList(new String[]{"MONDAY", "TUESDAY"})));
        repository.updateAlarmDays(testAlarm1, new ArrayList<String>(Arrays.asList(new String[]{"FRIDAY"})));
        System.err.println(TAG + " : UPDATED DAYS : " + repository.getDaysForAlarm(testAlarm1.getId()));

        assertEquals(1, repository.getDaysForAlarm(testAlarm1.getId()).size());
    }
}
