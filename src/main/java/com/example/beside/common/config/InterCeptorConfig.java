package com.example.beside.common.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.example.beside.common.jwt.JwtInterceptor;

@Configuration
public class InterCeptorConfig implements WebMvcConfigurer {

<<<<<<< HEAD
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // JWT 토큰 검증을 적용할 URL 패턴 지정
        registry.addInterceptor(new JwtInterceptor())
                .addPathPatterns("/api/users/v1/users");
    }
=======
    // @Override
    // public void addInterceptors(InterceptorRegistry registry) {
    // // JWT 토큰 검증을 적용할 URL 패턴 지정
    // registry.addInterceptor(new JwtInterceptor())
    // .addPathPatterns("/api/users/v1/users");
    // }
>>>>>>> 0b4f76a (로그인 기능 구현 (#16))

}
