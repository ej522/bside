package com.example.beside.service;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;

import com.example.beside.domain.AppInfo;
import com.example.beside.repository.AppInfoRepositoryImpl;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AppInfoService {

    private final AppInfoRepositoryImpl appInfoRepository;

    private final RedisTemplate<String, String> redisTemplate;

    public AppInfo getAppTermInfo() {
        String appTerm = redisTemplate.opsForValue().get("appTerm");
        String version = redisTemplate.opsForValue().get("version");

        if (appTerm == null || version == null) {
            AppInfo appTermInfo = appInfoRepository.getAppTermInfo();

            ValueOperations<String, String> valueOperations = redisTemplate.opsForValue();
            valueOperations.set("appTerm", appTermInfo.getDetail());
            valueOperations.set("version", appTermInfo.getVersion());

            return appTermInfo;

        } else {
            AppInfo appInfo = new AppInfo();
            appInfo.setVersion(version);
            appInfo.setDetail(appTerm);

            return appInfo;
        }
    }

    public void saveAppTermInfo(String version, String content) {
        appInfoRepository.saveAppTermInfo(version, content);

        ValueOperations<String, String> valueOperations = redisTemplate.opsForValue();
        valueOperations.set("appTerm", content);
        valueOperations.set("version", version);
    }
}
