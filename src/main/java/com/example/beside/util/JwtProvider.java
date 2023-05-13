package com.example.beside.util;

import com.example.beside.domain.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Header;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import java.util.Date;

@Component
public class JwtProvider {
    @Value("${jwt.secret_key}")
    private String secret_key;
    @Value("${jwt.expTime}")
    private Long tokenValidTime;

    private RedisTemplate<String, String> redisTemplate;

    public JwtProvider(RedisTemplate<String, String> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public String createToken(User user) {
        Date now = new Date();
        return Jwts.builder()
                .signWith(SignatureAlgorithm.HS512, secret_key) // 사용할 암호화 알고리즘과 signature에 들어갈 secret값 세팅
                .setHeaderParam(Header.TYPE, Header.JWT_TYPE) // 헤더타입지정
                .setSubject("bside_moim") // jwt인증 식별자
                .setIssuedAt(now) // 토큰 발행 시간 정보, date 타입만 가능
                .setExpiration(new Date(now.getTime() + tokenValidTime)) // 만료시간, datetime만 가능
                .claim("user_id", user.getId())
                .claim("social_type", user.getSocial_type())
                .compact(); // 토큰생성
    }

    // 유효성확인
    public Claims validJwtToken(String authorizationHeader) {

        Claims claims = Jwts.parser()
                .setSigningKey(secret_key)
                .parseClaimsJws(authorizationHeader)
                .getBody();

        if (redisTemplate.opsForValue().get("jwt:" + claims.get("user_id")) == null) {
            throw new RuntimeException("로그아웃한 아이디입니다.");
        }

        return claims;
    }
}
