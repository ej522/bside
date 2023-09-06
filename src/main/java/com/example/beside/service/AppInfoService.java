package com.example.beside.service;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;

import com.example.beside.domain.AppInfo;
import com.example.beside.repository.AppInfoRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AppInfoService {

    private final AppInfoRepository appInfoRepository;

    private final RedisTemplate<String, String> redisTemplate;

    public AppInfo getAppTermInfo() {
        String ios_version = redisTemplate.opsForValue().get("ios_version");
        String android_version = redisTemplate.opsForValue().get("android_version");
        String terms = redisTemplate.opsForValue().get("terms");
        String privacy_policy = redisTemplate.opsForValue().get("privacy_policy");
        String marketing_info = redisTemplate.opsForValue().get("marketing_info");
        String withdraw_terms = redisTemplate.opsForValue().get("withdraw_terms");

        if (terms == null || ios_version == null  || android_version == null || 
            privacy_policy == null|| marketing_info == null || withdraw_terms == null) {
            AppInfo appTermInfo = appInfoRepository.getAppTermInfo();

            ValueOperations<String, String> valueOperations = redisTemplate.opsForValue();
            valueOperations.set("ios_version", appTermInfo.getIos_version());
            valueOperations.set("android_version", appTermInfo.getAndriod_version());
            valueOperations.set("terms", appTermInfo.getTerms());
            valueOperations.set("privacy_policy", appTermInfo.getPrivacy_policy());
            valueOperations.set("marketing_info", appTermInfo.getMarketing_info());
            valueOperations.set("withdraw_terms", appTermInfo.getWithdraw_terms());

            return appTermInfo;
        } else
            return new AppInfo(ios_version,android_version, terms, privacy_policy, marketing_info, withdraw_terms);

    }

    public void updateAppVersionInfo(String os_type, String version){
        ValueOperations<String, String> valueOperations = redisTemplate.opsForValue();

        if (os_type.equalsIgnoreCase("ios")){
            appInfoRepository.updateIosVersion(version);
            valueOperations.set("ios_version", version);
        }
            
        else if (os_type.equalsIgnoreCase("android")){
            appInfoRepository.updateAndroidVersion(version);
            valueOperations.set("android_version", version);
        }
    }

    public void saveAppTermInfo(AppInfo appInfo) {
        appInfoRepository.saveAppTermInfo(appInfo);

        ValueOperations<String, String> valueOperations = redisTemplate.opsForValue();
        valueOperations.set("ios_version", appInfo.getIos_version());
        valueOperations.set("android_version", appInfo.getAndriod_version());
        valueOperations.set("terms", appInfo.getTerms());
        valueOperations.set("privacy_policy", appInfo.getPrivacy_policy());
        valueOperations.set("marketing_info", appInfo.getMarketing_info());
        valueOperations.set("withdraw_terms", appInfo.getWithdraw_terms());
    }
}
