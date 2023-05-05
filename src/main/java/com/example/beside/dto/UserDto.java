package com.example.beside.dto;

import com.example.beside.domain.User;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserDto {

    @NotEmpty
    @Schema(description = "사용자 ID", example = "1")
    private Long id;

    @NotEmpty
    @Schema(description = "비밀번호", example = "password")
    private String password;

    @NotEmpty
    @Schema(description = "이메일", example = "owl@moim.life")
    private String email;

    @NotEmpty
    @Schema(description = "이름", example = "부엉이")
    private String name;

    @NotEmpty
    @Schema(description = "프로필 이미지", example = "https://moim.life/profile/green.jpg")
    private String profile_image;

    @NotEmpty
    @Schema(description = "소셜 타입", example = "MOIM")
    private String social_type;

    public UserDto(User user) {
        this.id = user.getId();
        this.email = user.getEmail();
        this.name = user.getName();
        this.profile_image = user.getProfile_image();
        this.social_type = user.getSocial_type();
    }
}
