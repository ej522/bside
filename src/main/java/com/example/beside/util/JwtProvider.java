package com.example.beside.util;

import com.example.beside.domain.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Header;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class JwtProvider {

    private static String key = "098765432123456789";

    private static long tokenValidTime = 30 * 60 * 1000L;

    public static String createToken(User user) {
        Date now = new Date();

        return Jwts.builder()
                .signWith(SignatureAlgorithm.HS512, key) // 사용할 암호화 알고리즘과 signature에 들어갈 secret값 세팅
                .setHeaderParam(Header.TYPE, Header.JWT_TYPE) // 헤더타입지정
                .setSubject("bside_moim") // jwt인증 식별자
                .setIssuedAt(now) // 토큰 발행 시간 정보, date 타입만 가능
                .setExpiration(new Date(now.getTime() + tokenValidTime)) // 만료시간, datetime만 가능
                .claim("user_id", user.getId())
                .claim("social_type", user.getSocial_type())
                .compact(); // 토큰생성
    }

    // 유효성확인
    public Claims validJwtToken(String authorizationHeader) throws IllegalAccessException {
        return Jwts.parser()
                .setSigningKey(key)
                .parseClaimsJws(authorizationHeader)
                .getBody();
    }
}
