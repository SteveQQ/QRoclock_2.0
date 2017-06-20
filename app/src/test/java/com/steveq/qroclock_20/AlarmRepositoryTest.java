package com.steveq.qroclock_20;

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
import static org.junit.Assert.*;

/**
 * Created by Adam on 2017-06-20.
 */

@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class, sdk = 21)
public class AlarmRepositoryTest {
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
        testAlarm1 = repository.createAlarm(testAlarm1);

        assertEquals(1, testAlarm1.getId());
    }

    @Test
    public void gettingAlarms(){
        Alarm testAlarm1 = new Alarm();
        testAlarm1.setTime("test_time");
        testAlarm1.setRingtone("test_ringtone");
        testAlarm1.setActive(true);
        testAlarm1 = repository.createAlarm(testAlarm1);

        assertEquals(1, repository.getAlarms().size());
        assertEquals(testAlarm1.getId(), repository.getAlarms().get(0).getId());
    }

    @Test
    public void gettingAlarmById(){
        Alarm testAlarm1 = new Alarm();
        testAlarm1.setTime("test_time");
        testAlarm1.setRingtone("test_ringtone");
        testAlarm1.setActive(true);
        testAlarm1 = repository.createAlarm(testAlarm1);

        assertEquals("test_time", repository.getAlarmById(1).getTime());
    }

    @Test
    public void updatingAlarm(){
        Alarm testAlarm1 = new Alarm();
        testAlarm1.setTime("test_time");
        testAlarm1.setRingtone("test_ringtone");
        testAlarm1.setActive(true);
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
        testAlarm1 = repository.createAlarm(testAlarm1);

        repository.deleteAlarm(testAlarm1);

        assertEquals(0, repository.getAlarms().size());
    }
}
