package com.example.beside.service;

import com.example.beside.common.Exception.UserAlreadyExistException;
import com.example.beside.common.Exception.UserNotExistException;
import com.example.beside.domain.LoginType;
import com.example.beside.domain.User;
import com.example.beside.repository.UserRepository;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class SocialLoginService {
    private static UserRepository userRepository;

    // 카카오 유저 정보
    public User getKaKaoUserInfo(String access_token) {
        User userInfo = new User();
        //HashMap<String, Object> userInfo = new HashMap<>();
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
            JsonObject obj = (JsonObject) parser.parse(result);

            JsonObject id = (JsonObject) obj.get("id");
            JsonObject kakao_account = (JsonObject) obj.get("kakao_account");
            JsonObject profile = (JsonObject) kakao_account.get("profile");
            JsonObject properties = (JsonObject) obj.get("properties");

            // 정보받아와서 db등록 여부 확인 후 DB추가할 곳
            String kakao_id = id.toString();
            String nickname = profile.get("nickname").toString();
            String imgUrl = profile.get("profile_image").toString();

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
