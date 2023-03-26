package com.example.beside.util;

import com.example.beside.domain.User;
import io.jsonwebtoken.Claims;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class JwtProviderTest {
    JwtProvider jwtProvider = new JwtProvider();

    String email = "test";

    @Test
    public void createToken() {
        //given
        User user = new User();
        user.setEmail(email);

        //when
        String token = jwtProvider.createToken(user);
        System.out.println(token);

        //then
        assertTrue(token.length()>10);
    }

    @Test
    public void valid() throws IllegalAccessException {
        //given
        User user = new User();
        user.setEmail(email);
        String token = jwtProvider.createToken(user);
        String auth = "Bearer " + token;
        System.out.println(auth);

        //when
        Claims claims = jwtProvider.validJwtToken(auth);
        System.out.println(claims);

        //then
        assertTrue(claims.get("email").toString().length() > 0);
    }

}