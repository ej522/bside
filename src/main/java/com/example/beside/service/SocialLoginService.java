package com.example.beside.service;

import com.example.beside.common.Exception.SocialLoginException;
import com.example.beside.common.Exception.UserNotExistException;
import com.example.beside.domain.LoginType;
import com.example.beside.domain.User;
import com.example.beside.dto.KakaoLoginInfoDto;
import com.example.beside.repository.UserRepository;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Optional;
import java.util.logging.Logger;

import com.google.gson.Gson;

@Service
@RequiredArgsConstructor
public class SocialLoginService {
    private final UserRepository userRepository;

    // 카카오 유저 정보
    public User getKaKaoUserInfo(String access_token) throws SocialLoginException {
        User userInfo = new User();
        String reqUrl = "https://kapi.kakao.com/v2/user/me";

        try {
            URL url = new URL(reqUrl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Authorization", "Bearer " + access_token);

            if (conn.getResponseCode() != HttpURLConnection.HTTP_OK)
                throw new SocialLoginException("로그인에 실패했습니다 " + conn.getResponseCode());

            BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));

            String inputLine;
            StringBuilder response = new StringBuilder();
            while ((inputLine = br.readLine()) != null) {
                response.append(inputLine);
            }
            Gson gson = new Gson();
            KakaoLoginInfoDto kakaoLoginInfo = gson.fromJson(response.toString(), KakaoLoginInfoDto.class);

            var kakao_id = String.valueOf(kakaoLoginInfo.getId());
            var nickname = kakaoLoginInfo.getKakao_account().profile.nickname;
            var imgUrl = kakaoLoginInfo.getKakao_account().profile.profile_image_url;

            userInfo.setEmail(kakao_id);
            userInfo.setName(nickname);
            userInfo.setProfile_image(imgUrl);
            userInfo.setSocial_type(LoginType.KAKAO.name());

        } catch (Exception ex) {
            throw new SocialLoginException("로그인에 실패했습니다 " + ex.getMessage());
        }

        return userInfo;
    }

    // 로그인
    @Transactional
    public User loginKakao(User user) throws UserNotExistException {
        Optional<User> optionalUser = userRepository.findUserByEmailAndSocialType(user.getEmail(),
                user.getSocial_type());

        if (optionalUser.isPresent()) {
            return optionalUser.get();
        } else {
            Long id = userRepository.saveUser(user);
            user.setId(id);
            return user;
        }
    }
}
