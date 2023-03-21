package com.example.beside.api.user;

import com.example.beside.domain.User;
import com.example.beside.service.SocialLoginService;
<<<<<<< HEAD
<<<<<<< HEAD
import com.example.beside.service.UserService;
import com.example.beside.util.JwtProvider;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
=======
=======
import com.example.beside.service.UserService;
import com.example.beside.util.JwtProvider;
import jakarta.servlet.http.HttpServletResponse;
>>>>>>> 17ac0eb (카카오로그인)
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
<<<<<<< HEAD
>>>>>>> ca038a6 (카카오로그인구현, JWT적용x)
=======
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
<<<<<<< HEAD
>>>>>>> 17ac0eb (카카오로그인)
=======
import org.springframework.web.bind.annotation.GetMapping;
>>>>>>> 0b323c6 (카카오로그인수정)
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.net.http.HttpResponse;
import java.util.HashMap;

<<<<<<< HEAD
<<<<<<< HEAD
@RequiredArgsConstructor
@RestController
@RequestMapping("/api")
<<<<<<< HEAD
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
=======
@RequiredArgsConstructor
>>>>>>> 17ac0eb (카카오로그인)
@RestController
=======
>>>>>>> 0b323c6 (카카오로그인수정)
public class SocialLoginController {

    private final SocialLoginService socialLoginService;
    private final UserService userService;
    private final JwtProvider jwtProvider;


    @GetMapping(value = "/v1/kakaoLogin")
    public ResponseEntity kakaoLogin(@RequestParam("code") String code, HttpServletResponse response) {
        HashMap<String, Object> userInfo = socialLoginService.getUserInfo(code);

<<<<<<< HEAD
        //세션에 유저 이메일과 토큰 담기
        session.setAttribute("userEmail", userInfo.get("email"));
        session.setAttribute("access_token", access_token);
>>>>>>> ca038a6 (카카오로그인구현, JWT적용x)
=======
        User user = (User) userInfo.get("user");

        //jwt 토큰발급
        String jwtToken = jwtProvider.createToken(user);

        // JWT 토큰 헤더에 담아 전달
        response.addHeader("Authorization","Bearer "+jwtToken);

        return new ResponseEntity(HttpStatus.OK);
>>>>>>> 17ac0eb (카카오로그인)
    }
}
