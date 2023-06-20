package com.example.beside.service;

import com.example.beside.common.Exception.ExceptionDetail.SocialLoginException;
import com.example.beside.common.Exception.ExceptionDetail.UserNotExistException;
import com.example.beside.domain.LoginType;
import com.example.beside.domain.User;
import com.example.beside.dto.KakaoLoginInfoDto;
import com.example.beside.repository.UserRepositoryImpl;
import com.example.beside.util.JwtProvider;

import org.springframework.beans.factory.annotation.Value;
import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigInteger;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.spec.RSAPublicKeySpec;
import java.util.Base64;
import java.util.Objects;
import java.util.Optional;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;

@Service
@RequiredArgsConstructor
public class SocialLoginService {
    private final JwtProvider jwtProvider;

    private final UserRepositoryImpl userRepository;

    @Value("${spring.kakao.admin}")
    private String APP_ADMIN_KEY;

    // 카카오 유저 정보
    @Transactional
    public User getKaKaoUserInfo(String access_token, String fcm) throws SocialLoginException, UserNotExistException {
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
            var nickname = "";
            if (kakaoLoginInfo.getKakao_account().profile != null)
                nickname = kakaoLoginInfo.getKakao_account().profile.nickname;

            userInfo.setEmail(kakao_id);
            userInfo.setName(nickname);
            userInfo.setSocial_type(LoginType.KAKAO.name());

        } catch (Exception ex) {
            throw new SocialLoginException("로그인에 실패했습니다 " + ex.getMessage());
        }

        Optional<User> optionalUser = userRepository
                .findUserByEmailAndSocialType(userInfo.getEmail(), userInfo.getSocial_type());

        if (optionalUser.isPresent()) {
            User user = optionalUser.get();
            // fcm 갱신
            if (user.getFcm() != fcm) {
                user.setFcm(fcm);
                userRepository.updateFcmToken(user);
            }
            return user;
        }

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

    // 애플 로그인
    @Transactional
    public User appleLogin(String identityToken, String fcm) throws Exception {
        User userInfo = new User();
        String reqUrl = "https://appleid.apple.com/auth/keys";
        StringBuffer publicKey = new StringBuffer();
        String userEmail = "";

        try {
            URL url = new URL(reqUrl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));

            if (conn.getResponseCode() != HttpURLConnection.HTTP_OK)
                throw new SocialLoginException("로그인에 실패했습니다 " + conn.getResponseCode());

            String inputLine;
            while ((inputLine = br.readLine()) != null) {
                publicKey.append(inputLine);
            }

            userEmail = decodeIdentityToken(publicKey, identityToken);

        } catch (IOException ex) {
            throw new SocialLoginException("애플 로그인에 실패했습니다 " + ex.getMessage());
        }

        userInfo.setEmail(userEmail);
        userInfo.setSocial_type(LoginType.APPLE.name());

        Optional<User> optionalUser = userRepository
                .findUserByEmailAndSocialType(userInfo.getEmail(), userInfo.getSocial_type());

        if (optionalUser.isPresent()) {
            User user = optionalUser.get();
            // fcm 갱신
            if (user.getFcm() != fcm) {
                user.setFcm(fcm);
                userRepository.updateFcmToken(user);
            }
            return user;
        }

        userInfo.setProfile_image(null);
        return userRepository.saveUser(userInfo);
    }

    public String decodeIdentityToken(StringBuffer result, String identityToken) throws Exception {

        JsonParser parser = new JsonParser();
        JsonObject keys = (JsonObject) parser.parse(result.toString());
        JsonArray keyArray = (JsonArray) keys.get("keys");

        // 클라이언트로부터 가져온 identity token String decode
        String[] decodeArray = identityToken.split("\\.");
        String header = new String(Base64.getDecoder().decode(decodeArray[0]));

        // apple에서 제공해주는 kid값과 일치하는지 알기 위해
        JsonElement kid = ((JsonObject) parser.parse(header)).get("kid");
        JsonElement alg = ((JsonObject) parser.parse(header)).get("alg");

        // 써야하는 Element (kid, alg 일치하는 element)
        JsonObject avaliableObject = null;
        for (int i = 0; i < keyArray.size(); i++) {
            JsonObject appleObject = (JsonObject) keyArray.get(i);
            JsonElement appleKid = appleObject.get("kid");
            JsonElement appleAlg = appleObject.get("alg");

            if (Objects.equals(appleKid, kid) && Objects.equals(appleAlg, alg)) {
                avaliableObject = appleObject;
                break;
            }
        }
        // 일치하는 공개키 없음
        if (avaliableObject.isEmpty())
            throw new SocialLoginException("일치하는 공개키 없음");

        PublicKey publicKey = this.getPublicKey(avaliableObject);
        Claims claims = Jwts.parser()
                .setSigningKey(publicKey)
                .parseClaimsJws(identityToken)
                .getBody();

        JsonObject userInfoObject = (JsonObject) parser.parse(new Gson().toJson(claims));
        JsonElement appleAlg = userInfoObject.get("email");
        String email = appleAlg.getAsString();
        return email;
    }

    public PublicKey getPublicKey(JsonObject object) {
        // https://hello-gg.tistory.com/65
        String nStr = object.get("n").toString();
        String eStr = object.get("e").toString();

        byte[] nBytes = Base64.getUrlDecoder().decode(nStr.substring(1, nStr.length() - 1));
        byte[] eBytes = Base64.getUrlDecoder().decode(eStr.substring(1, eStr.length() - 1));

        BigInteger n = new BigInteger(1, nBytes);
        BigInteger e = new BigInteger(1, eBytes);

        try {
            RSAPublicKeySpec publicKeySpec = new RSAPublicKeySpec(n, e);
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            PublicKey publicKey = keyFactory.generatePublic(publicKeySpec);
            return publicKey;
        } catch (Exception ex) {
            throw new RuntimeException(ex.getMessage());
        }
    }

}
