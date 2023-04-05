package com.example.beside.util;

import com.example.beside.domain.LoginType;
import com.example.beside.domain.User;
import io.jsonwebtoken.Claims;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

import org.assertj.core.api.Assertions;

class JwtProviderTest {
    JwtProvider jwtProvider = new JwtProvider();

    private long id = 777;
    private User user;

    @BeforeEach
    void settingEntity() {
        user = new User();
        user.setId(id);
        user.setEmail("test-user@google.com");
        user.setSocial_type(LoginType.MOIM.name());
    }

    @Test
    public void createToken() {
        // when
        String token = JwtProvider.createToken(user);
        System.out.println(token);

        // then
        assertTrue(token.length() > 10);
    }

    @Test
    public void valid() throws IllegalAccessException {
        String token = JwtProvider.createToken(user);

        // when
        Claims claims = jwtProvider.validJwtToken(token);

        // then
        Assertions.assertThat(claims.get("user_id")).isEqualTo(777);
        Assertions.assertThat(claims.get("social_type")).isEqualTo("MOIM");
        Assertions.assertThat(claims.get("email")).isEqualTo("test-user@google.com");
    }

}