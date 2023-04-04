package com.example.beside.common.jwt;

import org.springframework.web.servlet.HandlerInterceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class JwtInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
            throws Exception {

        // JWT 토큰 검증 로직 구현
        String token = request.getHeader("Authorization");
        if (token != null && token.startsWith("Bearer ")) {
            String jwtToken = token.substring(7);
            // JWT 토큰 검증 코드 작성
            // 검증에 실패한 경우 false 반환
            // 검증에 성공한 경우 true 반환
        } else {
            // JWT 토큰이 없는 경우 처리
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return false;
        }

        return false;
    }
}
