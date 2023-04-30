package com.example.beside.common.config;

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

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // JWT 토큰 검증을 적용할 URL 패턴 지정
        registry.addInterceptor(new JwtInterceptor(userService))
                .addPathPatterns("/api/users/v1/users")
                .addPathPatterns("/api/moim/v1/make")
                .addPathPatterns("/api/moim/v1/participate")
                .addPathPatterns("/api/users/v1/update/nickname")
                .addPathPatterns("/api/moim/v1/adjust-schedule")
                .addPathPatterns("/api/users/v1/update/profileImage")
                .addPathPatterns("/api/moim/v1/my_moim_history");
    }

}
