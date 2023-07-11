package com.example.beside.repository;

import com.example.beside.domain.Alarm;

import java.util.List;

public interface FcmPushRepository {
    //create
    void insertAlarm(Alarm alarm);

    //read
    List<Alarm> getAlarmAllList(long user_id);

    List<Alarm> getAlarmByType(long user_id, String type);

    //update
    Alarm updateAlarmStatus(long alarm_id, long user_id, String status);

}
