package com.example.beside.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.HashMap;

<<<<<<< HEAD
=======
import static org.junit.jupiter.api.Assertions.*;

>>>>>>> f0cc91b (카카오로그인 수정, 테스트코드 추가)
class SocialLoginServiceTest {

    @Test
    @DisplayName("카카오유저정보")
    void getKakaoUserInfo() {
<<<<<<< HEAD
        // given
        String token = "카카오 access token";

        // when
        HashMap<String, Object> userInfo = SocialLoginService.getKaKaoUserInfo(token);
        String kakaoAcountInfo = "";
        if (userInfo.get("kakaoAccount") != null) {
            kakaoAcountInfo = userInfo.get("kakaoAccount").toString();
        }

        // then
        // assertTrue(kakaoAcountInfo.length()>1);
=======
        //given
        //카카오에서 받은 토큰
        String token = "";

        //when
        HashMap<String, Object> userInfo = SocialLoginService.getKaKaoUserInfo(token);
        String kakaoAcountInfo = userInfo.get("kakaoAccount").toString();

        //then
        assertTrue(kakaoAcountInfo.length()>1);
>>>>>>> f0cc91b (카카오로그인 수정, 테스트코드 추가)

    }

}