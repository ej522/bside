package com.example.beside.repository;

import com.example.beside.domain.Alarm;

import java.util.List;

public interface FcmPushRepository {
    //create
    void insertAlarm(Alarm alarm);

    //read

    List<Alarm> getAlarmListByType(long user_id, String type);

    //update
    void updateAlarmStatus(long alarm_id, long user_id, String status);

}
