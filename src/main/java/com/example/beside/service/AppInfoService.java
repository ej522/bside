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
        String version = redisTemplate.opsForValue().get("version");
        String terms = redisTemplate.opsForValue().get("terms");
        String privacy_policy = redisTemplate.opsForValue().get("privacy_policy");
        String marketing_info = redisTemplate.opsForValue().get("marketing_info");
        String withdraw_terms = redisTemplate.opsForValue().get("withdraw_terms");

        if (terms == null || version == null || privacy_policy == null
                || marketing_info == null || withdraw_terms == null) {
            AppInfo appTermInfo = appInfoRepository.getAppTermInfo();

            ValueOperations<String, String> valueOperations = redisTemplate.opsForValue();
            valueOperations.set("version", appTermInfo.getVersion());
            valueOperations.set("terms", appTermInfo.getTerms());
            valueOperations.set("privacy_policy", appTermInfo.getPrivacy_policy());
            valueOperations.set("marketing_info", appTermInfo.getMarketing_info());
            valueOperations.set("withdraw_terms", appTermInfo.getWithdraw_terms());

            return appTermInfo;
        } else
            return new AppInfo(version, terms, privacy_policy, marketing_info, withdraw_terms);

    }

    public void saveAppTermInfo(AppInfo appInfo) {
        appInfoRepository.saveAppTermInfo(appInfo);

        ValueOperations<String, String> valueOperations = redisTemplate.opsForValue();
        valueOperations.set("version", appInfo.getVersion());
        valueOperations.set("terms", appInfo.getTerms());
        valueOperations.set("privacy_policy", appInfo.getPrivacy_policy());
        valueOperations.set("marketing_info", appInfo.getMarketing_info());
        valueOperations.set("withdraw_terms", appInfo.getWithdraw_terms());
    }
}
