package com.example.beside.util;

import com.example.beside.domain.LoginType;
import com.example.beside.domain.User;
import io.jsonwebtoken.Claims;

import io.jsonwebtoken.Header;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

import org.assertj.core.api.Assertions;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import java.util.Date;

@SpringBootTest
@TestPropertySource(locations = "classpath:application.yml")
class JwtProviderTest {
    @Value("${jwt.secret_key}")
    private String secret_key;
    @Value("${jwt.expTime}")
    private Long tokenValidTime;

    @Mock
    private JwtProvider mockJwtProvider;

    private long id = 777;
    private User user;

    @BeforeEach
    void settingEntity() {
        user = new User();
        user.setId(id);
        user.setEmail("test-user@google.com");
        user.setSocial_type(LoginType.MOIM.name());
    }

    // @Test
    // public void createToken() throws Exception {
    // // given
    // String expectedToken = testCreateToken(user);
    // when(mockJwtProvider.createToken(user)).thenReturn(expectedToken);

    // // when
    // String token = mockJwtProvider.createToken(user);

    // // then
    // assertTrue(token.length() > 10);
    // }

    // @Test
    // public void valid() throws Exception {
    // // given
    // String expectedToken = testCreateToken(user);
    // when(mockJwtProvider.createToken(user)).thenReturn(expectedToken);
    // String token = mockJwtProvider.createToken(user);

    // Claims testValidJwtToken = testValidJwtToken(token);
    // when(mockJwtProvider.validJwtToken(token)).thenReturn(testValidJwtToken);
    // Claims claims = mockJwtProvider.validJwtToken(token);

    // // then
    // Assertions.assertThat(claims.get("user_id")).isEqualTo(777);
    // Assertions.assertThat(claims.get("social_type")).isEqualTo("MOIM");
    // }

    public String testCreateToken(User user) {
        Date now = new Date();
        return Jwts.builder()
                .signWith(SignatureAlgorithm.HS512, secret_key) // 사용할 암호화 알고리즘과 signature에 들어갈
                .setHeaderParam(Header.TYPE, Header.JWT_TYPE) // 헤더타입지정
                .setSubject("bside_moim") // jwt인증 식별자
                .setIssuedAt(now) // 토큰 발행 시간 정보, date 타입만 가능
                .setExpiration(new Date(now.getTime() + tokenValidTime)) // 만료시간, datetime만 가능
                .claim("user_id", user.getId())
                .claim("social_type", user.getSocial_type())
                .compact(); // 토큰생성
    }

    // 유효성확인
    public Claims testValidJwtToken(String authorizationHeader) {

        return Jwts.parser()
                .setSigningKey(secret_key)
                .parseClaimsJws(authorizationHeader)
                .getBody();
    }

}