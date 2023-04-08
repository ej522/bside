package com.example.beside.dto;

import com.example.beside.domain.User;

import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserDto {

    @NotEmpty
    private Long id;
    @NotEmpty
    private String password;
    @NotEmpty
    private String email;
    @NotEmpty
    private String name;
    @NotEmpty
    private String profile_image;
    @NotEmpty
    private String social_type;

    public UserDto(User user) {
        this.id = user.getId();
        this.email = user.getEmail();
        this.name = user.getName();
        this.profile_image = user.getProfile_image();
        this.social_type = user.getSocial_type();
    }
}
