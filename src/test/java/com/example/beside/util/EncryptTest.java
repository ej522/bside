package com.example.beside.util;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import java.nio.charset.StandardCharsets;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

@SpringBootTest
@TestPropertySource(locations = "classpath:application.yml")
class EncryptTest {

    @Value("${spring.secret.algorithm}")
    private String algorithm;
    @Value("${spring.secret.transformation}")
    private String transformation;
    @Value("${spring.secret.key}")
    private String secret_key;

    @Mock
    private Encrypt mockEncrypt;

    private String plainText = "1";

    @Test
    @DisplayName("해싱암호화")
    public void hashEncryptTest() throws NoSuchAlgorithmException {
        // given
        String plainText = "hashEncryptTest";

        // when
        String test = Encrypt.getHashingPassword(plainText);

        System.out.println("tettt-" + Encrypt.getHashingPassword("testtt"));
        // then
        assertNotEquals(plainText, test);
    }

    @Test
    @DisplayName("양방향 암호화")
    public void Encrypt() throws Exception {
        // given
        when(mockEncrypt.encrypt(plainText)).thenReturn(customEncrypt(plainText));

        // when 
        String encrypt2 = mockEncrypt.encrypt(plainText);

        // then
        System.out.println(encrypt2);
        Assertions.assertThat(encrypt2).isNotNull();
    }

    @Test
    @DisplayName("양방향 복호화")
    public void Decrypt() throws Exception {
        // given
        String encrypted = "W0qEdrjuJKdDToQVR9J7Nw==";
        when(mockEncrypt.encrypt(plainText)).thenReturn(customEncrypt(plainText));
        when(mockEncrypt.decrypt(encrypted)).thenReturn(custonDecrypt(encrypted));
        String encrypt = mockEncrypt.encrypt(plainText);

        // when
        String decryptedText = mockEncrypt.decrypt(encrypt);

        // then
        Assertions.assertThat(decryptedText).isEqualTo(plainText);
    }

    public String customEncrypt(String plainText) throws Exception {
        SecretKeySpec key = new SecretKeySpec(secret_key.getBytes(StandardCharsets.UTF_8), algorithm);
        Cipher cipher = Cipher.getInstance(transformation);
        cipher.init(Cipher.ENCRYPT_MODE, key);
        byte[] encryptedBytes = cipher.doFinal(plainText.getBytes());
        return Base64.getEncoder().encodeToString(encryptedBytes);
    }

    public String custonDecrypt(String encryptedText) throws Exception {
        SecretKeySpec key = new SecretKeySpec(secret_key.getBytes(StandardCharsets.UTF_8), algorithm);
        Cipher cipher = Cipher.getInstance(transformation);
        cipher.init(Cipher.DECRYPT_MODE, key);
        byte[] encryptedBytes = Base64.getDecoder().decode(encryptedText);
        byte[] decryptedBytes = cipher.doFinal(encryptedBytes);
        return new String(decryptedBytes);
    }

}