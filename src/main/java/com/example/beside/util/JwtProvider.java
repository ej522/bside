package com.example.beside.util;

import com.example.beside.domain.User;
import com.example.beside.repository.JwtRedisRepository;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Header;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class JwtProvider {

    @Autowired
    private RedisTemplate redisTemplate;

    private static String key = "098765432123456789";
    private static long tokenValidTime = 8 * 60 * 60 * 1000L;

    @Autowired
    private JwtRedisRepository jwtRedisRepository;

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
    public Claims validJwtToken(String authorizationHeader) throws Exception {
        Claims claims = Jwts.parser()
                .setSigningKey(key)
                .parseClaimsJws(authorizationHeader)
                .getBody();

        //jwtRedisRepository.findById("jwtChk:"+authorizationHeader);

        ValueOperations<String, String> logoutValueOperation = redisTemplate.opsForValue();
        System.out.println(logoutValueOperation.get("jwt:"+claims.get("user_id").toString()));

//        if(logoutValueOperation.get(authorizationHeader) != null) {
//            throw new RuntimeException("로그아웃 된 토큰 입니다.");
//        }
        return claims;
    }
}
