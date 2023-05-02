package com.example.beside.service;

import static org.junit.jupiter.api.Assertions.assertThrows;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.example.beside.common.Exception.EmailValidateException;
import com.example.beside.domain.QEmailValidate;
import com.querydsl.jpa.impl.JPAQueryFactory;

import jakarta.mail.MessagingException;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;

@SpringBootTest
@Transactional
public class EmailServiceTest {

    @Autowired
    private EmailService emailService;

    @Autowired
    private EntityManager em;
    private JPAQueryFactory queryFactory;

    private String toEmail = "test@naver.com";
    private String beforeVerifyCode = "123456";
    private String newVerifyCode = "654321";

    @AfterEach
    public void AfterEach() {
        queryFactory = new JPAQueryFactory(em);
        QEmailValidate qEmail = new QEmailValidate("qEmail");

        queryFactory.delete(qEmail).where(qEmail.email.eq(toEmail)).execute();
    }

    @Test
    @DisplayName("이메일 인증 번호 내부 검증")
    void testSaveVerificationLog() throws MessagingException {
        // given
        emailService.sendVerificationEmail(toEmail, newVerifyCode);
        // when
        long saveVerificationLog = emailService.saveVerificationLog(toEmail, newVerifyCode);

        // then
        Assertions.assertThat(saveVerificationLog).isNotZero();
    }

    @Test
    @DisplayName("이메일 인증 번호 검증")
    void testCheckEmailValidate() throws EmailValidateException, MessagingException {
        // given
        emailService.sendVerificationEmail(toEmail, newVerifyCode);

        // when
        Boolean checkEmailValidate = emailService.checkEmailValidate(toEmail, newVerifyCode);

        // then
        Assertions.assertThat(checkEmailValidate).isTrue();
    }

    @Test
    @DisplayName("시간초과한 이메일 인증 번호 검증")
    void testCheckEmailValidateWithTimeExceed() throws EmailValidateException {
        // when, then
        assertThrows(EmailValidateException.class, () -> emailService.checkEmailValidate(toEmail, beforeVerifyCode));
    }

    @Test
    @DisplayName("이메일 인증 번호 전송")
    void testSendVerificationEmail() throws Exception {
        // when
        emailService.sendVerificationEmail(toEmail, newVerifyCode);

        // then
        Assertions.assertThat(emailService).isNotNull();
    }

    @Test
    @DisplayName("임시 비밀번호 전송")
    void testSendTemporaryPasswordEmail() throws Exception {
        //when
        emailService.sendTemporaryPasswordEmail(toEmail, "test", "test123!");

        //then
        Assertions.assertThat(emailService).isNotNull();
    }
}
