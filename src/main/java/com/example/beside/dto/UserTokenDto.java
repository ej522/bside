package com.example.beside.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class UserTokenDto {

    @NotEmpty
    private String token;

    private UserDto user;

    public UserTokenDto(String token, UserDto user) {
        this.token = token;
        this.user = user;
    }
}
