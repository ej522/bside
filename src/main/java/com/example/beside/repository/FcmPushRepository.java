package com.example.beside.repository;

import com.example.beside.domain.Alarm;

public interface FcmPushRepository {
    void insertAlarm(Alarm alarm);
}
