package com.example.beside.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class UserTokenDto {

    @NotEmpty
    @Schema(description = "JWT 토큰", example = "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJic2lkZV9tb2ltIiwiaWF0Ijo")
    private String token;

    @NotEmpty
    private UserDto user;

    public UserTokenDto(String token, UserDto user) {
        this.token = token;
        this.user = user;
    }
}
