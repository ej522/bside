package com.example.beside.common.jwt;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import com.example.beside.util.JwtProvider;

import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class JwtInterceptor implements HandlerInterceptor {

    @Autowired
    private JwtProvider jwtProvider = new JwtProvider();

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
            throws Exception {

        // JWT 토큰 검증 로직 구현
        String token = request.getHeader("Authorization");
        if (token != null && token.startsWith("Bearer ")) {
            String jwtToken = token.substring(7);
            Claims claims = jwtProvider.validJwtT

            String social_type = (String) claims.get("social_type");
            String subject = (String) claims.get("sub");

            if (!subject.equals("bside_moim") || social_type.isEmpty() || user_id == 0) {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                return false;
            }

            return true;
        } else {
            // JWT 토큰이 없는 경우 처리
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return false;
        }
    }
}
