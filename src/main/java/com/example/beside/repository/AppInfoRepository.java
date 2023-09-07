package com.example.beside.repository;

import com.example.beside.domain.AppInfo;

public interface AppInfoRepository {
    AppInfo getAppTermInfo();

    void updateIosVersion(String version);

    void updateAndroidVersion(String version);

    void saveAppTermInfo(AppInfo content);
}
