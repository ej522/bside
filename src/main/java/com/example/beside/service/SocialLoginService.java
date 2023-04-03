package com.example.beside.service;

import com.example.beside.domain.User;
import com.example.beside.repository.UserRepository;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;

@Service
public class SocialLoginService {
    @Autowired
    private static UserService userService;

    // 카카오 유저 정보 얻기
    public static HashMap getKaKaoUserInfo(String access_token) {
        HashMap<String, Object> userInfo = new HashMap<>();
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

            JsonObject kakao_account = (JsonObject) obj.get("kakao_account");
            JsonObject properties = (JsonObject) obj.get("properties");

            userInfo.put("kakaoAccount", kakao_account);

            // 정보받아와서 db등록 여부 확인 후 DB추가할 곳

            String id = properties.get("id").toString();
            String imgUrl = properties.get("profile_image").toString();

            User user = new User();
            user.setEmail(id);

            if(userService.findUserByEmail(id)==null) {
                userService.saveUser(user);
            }

            userInfo.put("user", user);


        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

        return userInfo;
    }
}
