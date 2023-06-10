package com.example.beside.service;

import org.springframework.stereotype.Service;

import com.example.beside.domain.AppInfo;
import com.example.beside.repository.AppInfoRepositoryImpl;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AppInfoService {

    private final AppInfoRepositoryImpl appInfoRepository;

    public AppInfo getAppTermInfo() {
        return appInfoRepository.getAppTermInfo();
    }

    public void saveAppTermInfo(String version, String content) {
        appInfoRepository.saveAppTermInfo(version, content);
    }
}
