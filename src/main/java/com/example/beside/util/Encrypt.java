package com.example.beside.util;

import javax.crypto.Cipher;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.UnsupportedEncodingException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

public class Encrypt {

    public static String getHashingPassword(String plainText) throws NoSuchAlgorithmException {

        MessageDigest md = MessageDigest.getInstance("SHA-256");
        md.update(plainText.getBytes());

        byte[] bytes = md.digest();

        StringBuffer sb = new StringBuffer();
        for(byte b : bytes) {
            sb.append(String.format("%02x",b));
        }

        return sb.toString();
    }
}
