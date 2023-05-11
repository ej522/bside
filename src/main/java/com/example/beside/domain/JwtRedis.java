package com.example.beside.domain;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

@Getter
@Setter
@RedisHash("jwtChk")
public class JwtRedis {
    @Id
    private String id;
    private String email;
    private String token;

    public JwtRedis(String id, String email, String token) {
        this.id = id;
        this.email = email;
        this.token = token;
    }
}
