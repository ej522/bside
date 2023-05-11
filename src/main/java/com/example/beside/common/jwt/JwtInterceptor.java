package com.example.beside.common.jwt;

import com.example.beside.repository.JwtRedisRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.HandlerInterceptor;

import com.example.beside.domain.User;
import com.example.beside.service.UserService;
import com.example.beside.util.JwtProvider;

import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class JwtInterceptor implements HandlerInterceptor {

    private UserService userService;
    private JwtProvider jwtProvider = new JwtProvider();

    public JwtInterceptor(UserService userService) {
        this.userService = userService;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
            throws Exception {

        // JWT 토큰 검증 로직 구현
        String token = request.getHeader("Authorization");
        if (token != null && token.startsWith("Bearer ")) {
            String jwtToken = token.substring(7);
            Claims claims = jwtProvider.validJwtToken(jwtToken);

            // Claim 값 복호화
            String user_id = String.valueOf(claims.get("user_id"));
            String social_type = String.valueOf(claims.get("social_type"));
            String subject = String.valueOf(claims.get("sub"));

            // token user_id 로 User 조회
            User user = userService.findUserById(Long.parseLong(user_id));

            if (!subject.equals("bside_moim") || !social_type.equals(user.getSocial_type())) {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                return false;
            }

            // 컨트롤러에서 사용할 변수를 request 객체에 저장
            request.setAttribute("user", user);

            return true;
        } else {
            // JWT 토큰이 없는 경우 처리
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return false;
        }
    }
}
