package com.example.beside.util;

import java.security.SecureRandom;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.example.beside.common.Exception.PasswordException;
import com.example.beside.common.Exception.UserValidateNickName;

public class Common {

    public static Boolean PasswordValidate(String password) throws PasswordException {
        // 비밀번호 길이가 8자 이상인지 확인
        if (password.length() < 8) {
            throw new PasswordException("패스워드 길이가 8자 이상 이어야 합니다");
        }

        // 숫자 포함 여부 확인
        Pattern pattern = Pattern.compile("\\d");
        Matcher matcher = pattern.matcher(password);
        if (!matcher.find()) {
            throw new PasswordException("숫자가 포함되어야 합니다");
        }

        // 영문 대소문자 포함 여부 확인
        pattern = Pattern.compile("[a-zA-Z]");
        matcher = pattern.matcher(password);
        if (!matcher.find()) {
            throw new PasswordException("영문 대소문자가 포함되어야 합니다");
        }

        // 특수 문자 포함 여부 확인
        pattern = Pattern.compile("[!@#$%^&*()\\[\\]\\-_+]+");
        matcher = pattern.matcher(password);
        if (!matcher.find()) {
            throw new PasswordException("!@#$%^&*()-_+ 등의 특수문자가 포함되어야 합니다");
        }

        return true;
    }

    public static Boolean NicknameValidate(String nickname) throws UserValidateNickName {
        Pattern pattern = Pattern.compile("^[a-zA-Zㄱ-ㅎㅏ-ㅣ가-힣0-9]{1,8}$");
        Matcher matcher = pattern.matcher(nickname);
        if(!matcher.find()) {
            throw new UserValidateNickName("한글, 영문, 숫자 조합 8자 이내");
        }
        return true;
    }

    public static String generateRandomPassword() {
        String randomStr = "ABCDEFGHIGKNLMOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789!@#$%^&";
        StringBuilder password = new StringBuilder();

        SecureRandom random = new SecureRandom();
        while (password.length()<9) {
            int randomIdx = random.nextInt(randomStr.length());
            password.append(randomStr.charAt(randomIdx));
        }

        return password.toString();
    }
}
