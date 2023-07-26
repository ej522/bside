package com.example.beside.dto;

import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class KakaoLoginInfoDto {
    public long id;
    public Date connected_at;
    public Properties properties;
    public KakaoAccount kakao_account;

    public class KakaoAccount {
        public boolean profile_nickname_needs_agreement;
        public boolean profile_image_needs_agreement;
        public Profile profile;
        public boolean has_gender;
        public boolean gender_needs_agreement;
        public String gender;
    }

    public class Properties {
        public String nickname;
        public String profile_image;
        public String thumbnail_image;
    }

    public class Profile {
        public String nickname;
        public String thumbnail_image_url;
        public String profile_image_url;
        public boolean is_default_image;
    }
}
