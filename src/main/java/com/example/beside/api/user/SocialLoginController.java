package com.example.beside.api.user;

import com.example.beside.domain.User;
import com.example.beside.service.SocialLoginService;
import com.example.beside.service.UserService;
import com.example.beside.util.JwtProvider;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
    public ResponseEntity kakaoLogin(@RequestParam("code") String code, HttpServletResponse response) {
//        String access_token = socialLoginService.getKakaoAccessToken(code);
//        System.out.printf("ctr access_token="+access_token);
        HashMap<String, Object> userInfo = socialLoginService.getUserInfo(code);

        User user = (User) userInfo.get("user");

        //jwt 토큰발급
        String jwtToken = jwtProvider.createToken(user);

        // JWT 토큰 헤더에 담아 전달
        response.addHeader("Authorization","Bearer "+jwtToken);

        return new ResponseEntity(HttpStatus.OK);
    }
}
