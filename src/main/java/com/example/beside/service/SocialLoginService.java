package com.example.beside.service;

import com.example.beside.common.Exception.SocialLoginException;
import com.example.beside.common.Exception.UserNotExistException;
import com.example.beside.domain.LoginType;
import com.example.beside.domain.User;
import com.example.beside.dto.KakaoLoginInfoDto;
import com.example.beside.repository.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Optional;

import com.google.gson.Gson;

@Service
@RequiredArgsConstructor
public class SocialLoginService {
    private final UserRepository userRepository;

    @Value("${spring.kakao.admin}")
    private String APP_ADMIN_KEY;

    // 카카오 유저 정보
    @Transactional
    public User getKaKaoUserInfo(String access_token) throws SocialLoginException, UserNotExistException {
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

        Optional<User> optionalUser = userRepository
                .findUserByEmailAndSocialType(userInfo.getEmail(), userInfo.getSocial_type());

        if (optionalUser.isPresent())
            return optionalUser.get();

        userInfo.setProfile_image(null);
        return userRepository.saveUser(userInfo);
    }

    // 카카오 로그아웃
    public void logoutKakao(User user) throws SocialLoginException {
        String reqUrl = "https://kapi.kakao.com/v1/user/logout";
        String user_id = user.getEmail();

        try {
            URL url = new URL(reqUrl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            conn.setRequestProperty("Authorization", "KakaoAK " + APP_ADMIN_KEY);

            String body = "target_id_type=user_id&target_id=" + user_id;
            byte[] outputBytes = body.getBytes("UTF-8");
            conn.setDoOutput(true);
            conn.getOutputStream().write(outputBytes);

            if (conn.getResponseCode() != HttpURLConnection.HTTP_OK) {
                throw new SocialLoginException("카카오 로그아웃에 실패했습니다 " + conn.getResponseCode());
            }
            conn.disconnect();

        } catch (Exception ex) {
            throw new SocialLoginException("카카오 로그아웃에 실패했습니다" + ex.getMessage());
        }
    }

    // 카카오 연결 해제
    @Transactional
    public void unLinkKakao(User user) throws SocialLoginException {
        String reqUrl = "https://kapi.kakao.com/v1/user/unlink";
        String user_id = user.getEmail();

        try {
            URL url = new URL(reqUrl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            conn.setRequestProperty("Authorization", "KakaoAK " + APP_ADMIN_KEY);

            String body = "target_id_type=user_id&target_id=" + user_id;
            byte[] outputBytes = body.getBytes("UTF-8");
            conn.setDoOutput(true);
            conn.getOutputStream().write(outputBytes);

            if (conn.getResponseCode() != HttpURLConnection.HTTP_OK) {
                throw new SocialLoginException("카카오 연결 해제에 실패했습니다 " + conn.getResponseCode());
            }

            conn.disconnect();
            userRepository.deleteUser(user);

        } catch (Exception ex) {
            throw new SocialLoginException("카카오 연결 해제에 실패했습니다" + ex.getMessage());
        }
    }
}
