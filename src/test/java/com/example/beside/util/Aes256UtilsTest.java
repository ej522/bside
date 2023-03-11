package com.example.beside.util;

import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@SpringBootTest
class Aes256UtilsTest {

    static String plainString = "test_문자열";

    @Test
    @DisplayName("문자열_암호화")
    public void encrypt() throws Exception{
        // when
        String encryptedPassword = Aes256Utils.encrypt(plainString);

        // then
        assertNotEquals(plainString, encryptedPassword);
    }

    @Test
    @DisplayName("문자열_복호화")
    public void decrypt() throws Exception{
        // given 
        String encryptedPassword = Aes256Utils.encrypt(plainString);

        // when 
        String decodedString = Aes256Utils.decrypt(encryptedPassword);

        // then
        assertEquals(decodedString, plainString);

    }

}