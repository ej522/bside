package com.example.beside.util;

import javax.crypto.Cipher;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.UnsupportedEncodingException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

public class Aes256Utils {
    private static final String ALGORITHM = "AES";
    private static final String TRANSFORMATION = "AES/CBC/PKCS5PADDING";
    static final Base64.Decoder DECODER = Base64.getDecoder();
    static final Base64.Encoder ENCODER = Base64.getEncoder();
    private static final String key = "01234567890123456789012345678901"; //비밀키
    private static final String iv = key.substring(0, 16); // 16byte

    public static String encrypt(String plainText) {
        try {
            Cipher cipher = getCipher(Cipher.ENCRYPT_MODE);
            byte[] encrypted = cipher.doFinal(plainText.getBytes());

            return ENCODER.encodeToString(encrypted); //암호문을 base64로 인코딩하여 출력 해줌
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }

    public static String decrypt(String cipherText) {
        try {
            Cipher cipher = getCipher(Cipher.DECRYPT_MODE);
            byte[] decrypted = cipher.doFinal(DECODER.decode(cipherText)); //base64 to byte

            return new String(decrypted );
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }

    private static Cipher getCipher(int decryptMode) throws NoSuchPaddingException, NoSuchAlgorithmException, UnsupportedEncodingException, InvalidAlgorithmParameterException, InvalidKeyException {
        Cipher cipher = Cipher.getInstance(TRANSFORMATION);
        IvParameterSpec ivParameterSpec = new IvParameterSpec(iv.getBytes("UTF-8"));
        SecretKeySpec secretKeySpec = new SecretKeySpec(key.getBytes("UTF-8"), ALGORITHM);
        cipher.init(decryptMode, secretKeySpec, ivParameterSpec);

        return cipher;
    }
}
