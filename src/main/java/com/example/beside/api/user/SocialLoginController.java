package com.example.beside.api.user;

import com.example.beside.service.SocialLoginService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;

@RestController
public class SocialLoginController {

    @Autowired
    private SocialLoginService socialLoginService;

    @RequestMapping(value = "/kakaoLogin")
    public void kakaoLogin(@RequestParam("code") String code, HttpSession session) {
        String access_token = socialLoginService.getKakaoAccessToken(code);
        System.out.printf("ctr access_token="+access_token);
        HashMap<String, Object> userInfo = socialLoginService.getUserInfo(access_token);

        //세션에 유저 이메일과 토큰 담기
        session.setAttribute("userEmail", userInfo.get("email"));
        session.setAttribute("access_token", access_token);
    }
}
