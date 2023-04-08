package com.example.beside.util;

import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.security.NoSuchAlgorithmException;

@SpringBootTest
class EncryptTest {

    @Test
    @DisplayName("해싱암호화")
    public void hashEncryptTest() throws NoSuchAlgorithmException {
        //given
        String plainText = "hashEncryptTest";

        //when
        String test = Encrypt.getHashingPassword(plainText);

        System.out.println("tettt-"+Encrypt.getHashingPassword("testtt"));
        //then
        assertNotEquals(plainText, test);
    }

}