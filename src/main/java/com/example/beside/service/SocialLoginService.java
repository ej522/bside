package com.example.beside.service;

import com.example.beside.common.Exception.UserNotExistException;
import com.example.beside.domain.LoginType;
import com.example.beside.domain.User;
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

@Service
@RequiredArgsConstructor
public class SocialLoginService {

    private final UserRepository userRepository;

    // 카카오 유저 정보
    public User getKaKaoUserInfo(String access_token) {
        User userInfo = new User();
        String reqUrl = "https://kapi.kakao.com/v2/user/me";

        try {
            URL url = new URL(reqUrl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");

            // 요청에 필요한 Header에 포함될 내용
            conn.setRequestProperty("Authorization", "Bearer " + access_token);

            int responseCode = conn.getResponseCode();

            BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));

            String line = "";
            String result = "";

            while ((line = br.readLine()) != null) {
                result += line;
            }

            JsonParser parser = new JsonParser();
            JsonObject object = (JsonObject) parser.parse(result);
            JsonObject profile = (JsonObject) ((JsonObject) object.get("kakao_account")).get("static");

            String kakao_id = object.get("id").getAsString();
            String nickname = profile.get("nickname").getAsString();
            String imgUrl = profile.get("profile_image_url").getAsString();

            userInfo.setEmail(kakao_id);
            userInfo.setName(nickname);
            userInfo.setProfile_image(imgUrl);
            userInfo.setSocial_type(LoginType.KAKAO.name());

        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

        return userInfo;
    }

    //로그인
    @Transactional
    public User loginKakao(User user) throws UserNotExistException {
        Optional<User> optionalUser = userRepository.findUserByEmailAndSocialType(user.getEmail(), user.getSocial_type());

        if(optionalUser.isPresent()) {
            return optionalUser.get();
        } else {
            Long id = userRepository.saveUser(user);
            user.setId(id);
            return user;
        }
    }
}
