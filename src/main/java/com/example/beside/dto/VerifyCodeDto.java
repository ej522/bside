package com.example.beside.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class VerifyCodeDto {

    @NotNull
    private String verifyCode;

    private String email;

}
