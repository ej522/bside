package com.example.beside.util;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
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
        if(nickname.length()>8) {
            throw new UserValidateNickName("닉네임은 8자 이내여야 합니다.");
        }

        return true;
    }

    public static String generateRandomPassword() {
        String alphabet = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
        String number = "0123456789";
        String specialKey = "~!@#$%^&";

        List<String> pswList = new ArrayList<>();
        pswList.addAll(getRandomStrList(alphabet, 5));
        pswList.addAll(getRandomStrList(number, 2));
        pswList.addAll(getRandomStrList(specialKey, 1));

        Collections.shuffle(pswList);
        StringBuilder password = new StringBuilder();
        for(String psw: pswList) {
            password.append(psw);
        }

        return password.toString();
    }

    private static List<String> getRandomStrList(String str, int len) {
        SecureRandom random = new SecureRandom();

        List<String> randomList = new ArrayList<>();
        while (randomList.size()<len) {
            int randomIdx = random.nextInt(str.length());

            randomList.add(String.valueOf(str.charAt(randomIdx)));
        }

        return randomList;
    }
}
