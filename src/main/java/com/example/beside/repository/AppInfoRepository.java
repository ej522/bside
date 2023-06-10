package com.example.beside.repository;

import com.example.beside.domain.AppInfo;

public interface AppInfoRepository {
    AppInfo getAppTermInfo();

    void saveAppTermInfo(String v1, String content);
}
