package com.example.beside.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.*;

class SocialLoginServiceTest {

    @Test
    @DisplayName("카카오유저정보")
    void getKakaoUserInfo() {
        //given
        String token = "카카오 access token";

        //when
        HashMap<String, Object> userInfo = SocialLoginService.getKaKaoUserInfo(token);
        String kakaoAcountInfo = userInfo.get("kakaoAccount").toString();

        //then
        assertTrue(kakaoAcountInfo.length()>1);

    }

}