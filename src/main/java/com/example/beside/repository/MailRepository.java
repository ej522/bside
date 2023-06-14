package com.example.beside.repository;

import com.example.beside.common.Exception.ExceptionDetail.EmailValidateException;

public interface MailRepository {
    long saveEmailValidateCode(String email, String validateCode);

    Boolean checkEmailValidateTime(String email, String validateCode) throws EmailValidateException;
}
