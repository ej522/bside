package com.example.beside.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SimpleUserDto {
    @Schema(description = "유저 ID", example = "1")
    private Long user_id;

    @Schema(description = "유저 닉네임", example = "닉네임")
    private String nickname;

    @Schema(description = "유저 프로필", example = "프로필")
    private String profile;
}
