package com.example.beside.common.config;

import com.example.beside.util.JwtProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.example.beside.common.jwt.JwtInterceptor;
import com.example.beside.service.UserService;

@Configuration
public class InterCeptorConfig implements WebMvcConfigurer {

    @Autowired
    UserService userService;
    @Autowired
    JwtProvider jwtProvider;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // JWT 토큰 검증을 적용할 URL 패턴 지정
        registry.addInterceptor(new JwtInterceptor(userService, jwtProvider))
                // 유저
                .addPathPatterns("/api/users/v1/users")
                .addPathPatterns("/api/users/v1/update/nickname")
                .addPathPatterns("/api/users/v1/update/profile-image")
                .addPathPatterns("/api/users/v1/update/password")
                .addPathPatterns("/api/users/v1/update/alarm-state")
                .addPathPatterns("/api/users/v1/check/current-password")
                .addPathPatterns("/api/users/v1/my-friend")
                .addPathPatterns("/api/users/v1/logout")
                .addPathPatterns("/api/users/v1/delete")
                .addPathPatterns("/api/users/v1/user-info")
                // 모임
                .addPathPatterns("/api/moim/v1/adjust-schedule")
                .addPathPatterns("/api/moim/v1/make")
                .addPathPatterns("/api/moim/v1/participate")
                .addPathPatterns("/api/moim/v1/moim-history")
                .addPathPatterns("/api/moim/v1/voting-moim-list")
                .addPathPatterns("/api/moim/v1/invite-my-moim")
                .addPathPatterns("/api/moim/v1/host-select-date")
                .addPathPatterns("/api/moim/v1/date-vote")
                .addPathPatterns("/api/moim/v1/time-vote")
                .addPathPatterns("/api/moim/v1/delete/moim-history")
                .addPathPatterns("/api/moim/v1/moim-future")
                // 소셜
                .addPathPatterns("/api/social/v1/unlink/Kakao")
                .addPathPatterns("/api/social/v1/logout/kakao");
    }

}
