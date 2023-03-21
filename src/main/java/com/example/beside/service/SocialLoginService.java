package com.example.beside.service;

<<<<<<< HEAD
<<<<<<< HEAD
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.InputStreamReader;
=======
=======
import com.example.beside.domain.User;
>>>>>>> 17ac0eb (카카오로그인)
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
>>>>>>> ca038a6 (카카오로그인구현, JWT적용x)
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;

@Service
public class SocialLoginService {
<<<<<<< HEAD
<<<<<<< HEAD
    @Autowired
    private UserService userService;

    // 카카오 유저 정보 얻기
    public static HashMap<String, Object> getKaKaoUserInfo(String access_token) {
=======
=======
    @Autowired
    private UserService userService;
>>>>>>> 17ac0eb (카카오로그인)

    //카카오 accesstoken 발급
    public String getKakaoAccessToken (String code) {
        String access_token = "";
        String refresh_token = "";
        String reqUrl = "https://kauth.kakao.com/oauth/token";

        try {
            URL url = new URL(reqUrl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();

            //POST 요청을 위해 기본 값이 false인 setDoOutput을 true로
            conn.setRequestMethod("POST");
            conn.setDoOutput(true);

            //POST 요청에 필요로 요구하는 파라미터 스트림을 통해 전송
            BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(conn.getOutputStream()));
            StringBuilder sb = new StringBuilder();
            sb.append("grant_type=authorization_code");
            sb.append("&client_id=e0e9bb8073cb2394e6b373ed610a0635");
            sb.append("redirect_uri=http://localhost:8081/v1/kakaoLogin");
            sb.append("&code=" + code);
            bw.write(sb.toString());
            bw.flush();

            //결과 코드가 200이라면 성공
            int responseCode = conn.getResponseCode();
            System.out.printf("responseCode="+responseCode);

            //요청을 통해 얻은 JSON타입의 Response 메세지 읽어오기
            BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String line = "";
            String result = "";

            while ((line = br.readLine()) != null) {
                result += line;
            }

            //Gson 라이브러리에 포함된 클래스로 JSON파싱 객체 생성
            JsonParser parser = new JsonParser();
            JsonElement element = parser.parse(result);

            access_token = element.getAsJsonObject().get("access_token").getAsString();
            refresh_token = element.getAsJsonObject().get("refresh_token").getAsString();

            br.close();
            bw.close();

        } catch (Exception e) {
            e.getMessage();
        }

        return access_token;
    }

    //카카오 유저 정보 얻기
    public HashMap<String, Object> getUserInfo (String access_token) {
>>>>>>> ca038a6 (카카오로그인구현, JWT적용x)
        HashMap<String, Object> userInfo = new HashMap<>();
        String reqUrl = "https://kapi.kakao.com/v2/user/me";

        try {
            URL url = new URL(reqUrl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");

<<<<<<< HEAD
            // 요청에 필요한 Header에 포함될 내용
            conn.setRequestProperty("Authorization", "Bearer " + access_token);

            int responseCode = conn.getResponseCode();
=======
            //요청에 필요한 Header에 포함될 내용
            conn.setRequestProperty("Authorization", "Bearer " + access_token);

            int responseCode = conn.getResponseCode();
            System.out.printf("responseCode: " + responseCode);
>>>>>>> ca038a6 (카카오로그인구현, JWT적용x)

            BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));

            String line = "";
            String result = "";

            while ((line = br.readLine()) != null) {
                result += line;
            }
<<<<<<< HEAD

            JsonParser parser = new JsonParser();
            JsonObject obj = (JsonObject) parser.parse(result);

            JsonObject kakao_account = (JsonObject) obj.get("kakao_account");
            JsonObject properties = (JsonObject) obj.get("properties");

            userInfo.put("kakaoAccount", kakao_account);

            // 정보받아와서 db등록 여부 확인 후 DB추가할 곳

        } catch (Exception e) {
            System.out.println(e.getMessage());
=======
            System.out.printf("response body: " + result);

            JsonParser parser = new JsonParser();
            JsonElement element = parser.parse(result);

            JsonObject properties = element.getAsJsonObject().get("properties").getAsJsonObject();
            JsonObject kakao_account = element.getAsJsonObject().get("kakao_account").getAsJsonObject();

            String email = kakao_account.getAsJsonObject().get("email").getAsString();

            User user = new User();
            user.setEmail(email);

            //이메일 없으면 회원가입
            if(userService.findUserByEmail(email)==null) {
                userService.saveUser(user);
            }

            userInfo.put("user", user);
        } catch (Exception e) {
            e.getMessage();
>>>>>>> ca038a6 (카카오로그인구현, JWT적용x)
        }

        return userInfo;
    }
}
