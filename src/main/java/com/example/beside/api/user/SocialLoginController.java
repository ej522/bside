package com.example.beside.api.user;

import com.example.beside.service.SocialLoginService;
<<<<<<< HEAD
import com.example.beside.service.UserService;
import com.example.beside.util.JwtProvider;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
=======
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
>>>>>>> ca038a6 (카카오로그인구현, JWT적용x)
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;

<<<<<<< HEAD
@RequiredArgsConstructor
@RestController
@RequestMapping("/api")
public class SocialLoginController {

    private final SocialLoginService socialLoginService;
    private final UserService userService;
    private final JwtProvider jwtProvider;

    @GetMapping(value = "/v1/kakaoLogin")
    public String kakaoLogin(@RequestParam("code") String code, HttpServletResponse response) {
        HashMap<String, Object> userInfo = SocialLoginService.getKaKaoUserInfo(code);

        // jwt 토큰발급 추가예정

        //return userInfo.get("kakaoAccount").toString();
        return "jwt토큰";
=======
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
>>>>>>> ca038a6 (카카오로그인구현, JWT적용x)
    }
}
