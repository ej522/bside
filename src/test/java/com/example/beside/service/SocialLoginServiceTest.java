package com.example.beside.service;

import com.example.beside.domain.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class SocialLoginServiceTest {
    private static SocialLoginService socialLoginService;

    @Test
    @DisplayName("카카오유저정보")
    void getKakaoUserInfo() {
        // given
        String token = "카카오 access token";

        // when
//        User userInfo = socialLoginService.getKaKaoUserInfo(token);
//        String kakao_id = "";
//        if (userInfo != null) {
//            kakao_id = userInfo.getEmail();
//        }

        // then
        // assertTrue(kakao_id.length()>1);

    }

}