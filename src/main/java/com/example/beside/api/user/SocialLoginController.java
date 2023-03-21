package com.example.beside.api.user;

import com.example.beside.service.SocialLoginService;
import com.example.beside.service.UserService;
import com.example.beside.util.JwtProvider;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.net.http.HttpResponse;
import java.util.HashMap;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api")
public class SocialLoginController {

    private final SocialLoginService socialLoginService;
    private final UserService userService;
    private final JwtProvider jwtProvider;


    @GetMapping(value = "/v1/kakaoLogin")
    public String kakaoLogin(@RequestParam("code") String code, HttpServletResponse response) {
        HashMap<String, Object> userInfo = socialLoginService.getKaKaoUserInfo(code);

        //jwt 토큰발급 추가예정

        return userInfo.get("kakaoAccount").toString();
    }
}
