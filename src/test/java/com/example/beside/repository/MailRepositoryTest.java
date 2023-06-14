package com.example.beside.repository;

import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.LocalDateTime;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.example.beside.common.Exception.ExceptionDetail.EmailValidateException;
import com.example.beside.domain.EmailValidate;
import com.example.beside.domain.QEmailValidate;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;

import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;

@SpringBootTest
@Transactional
public class MailRepositoryTest {
    @Autowired
    private MailRepositoryImpl mailRepository;

    @Autowired
    private EntityManager em;
    private JPAQueryFactory queryFactory;

    private EmailValidate emailValidate = new EmailValidate();

    @BeforeEach
    public void setUp() {
        emailValidate.setEmail("test@email.com");
        emailValidate.setValidate_code("123456");
        emailValidate.setCreate_time(LocalDateTime.now());

        queryFactory = new JPAQueryFactory(em);
        QEmailValidate qEmail = new QEmailValidate("qEmail");

        queryFactory.insert(qEmail)
                .columns(qEmail.email, qEmail.validate_code, qEmail.create_time)
                .values(emailValidate.getEmail(), emailValidate.getValidate_code(), emailValidate.getCreate_time())
                .execute();
    }

    @AfterEach
    public void AfterEach() {
        queryFactory = new JPAQueryFactory(em);
        QEmailValidate qEmail = new QEmailValidate("qEmail");

        queryFactory.delete(qEmail).where(qEmail.email.eq("test@email.com")).execute();
    }

    @Test
    @DisplayName("이메일 인증 번호 저장")
    void testsaveEmailValidateCode() throws EmailValidateException {
        // given, when
        long saveEmailValidateCode = mailRepository.saveEmailValidateCode(emailValidate.getEmail(),
                emailValidate.getValidate_code());
        // then
        Assertions.assertThat(saveEmailValidateCode).isNotZero();
    }

    @Test
    @DisplayName("이메일 인증 번호 검증")
    void testCheckEmailValidateTime() throws EmailValidateException {
        // given, when
        Boolean checkEmailValidateTime = mailRepository.checkEmailValidateTime(emailValidate.getEmail(),
                emailValidate.getValidate_code());

        // then
        Assertions.assertThat(checkEmailValidateTime).isTrue();
    }

    @Test
    @DisplayName("틀린 인증 번호로 이메일 검증")
    void testCheckEmailWithWrongValidateCode() throws EmailValidateException {
        // given
        emailValidate.setValidate_code("654321");

        // when, then
        assertThrows(EmailValidateException.class, () -> mailRepository.checkEmailValidateTime(emailValidate.getEmail(),
                emailValidate.getValidate_code()));
    }

    @Test
    @DisplayName("존재하지 않는 이메일로 인증 번호 검증")
    void testCheckEmailWithNotExistEmail() throws EmailValidateException {
        // given
        emailValidate.setEmail("test@naver.com");

        // when, then
        assertThrows(EmailValidateException.class, () -> mailRepository.checkEmailValidateTime(emailValidate.getEmail(),
                emailValidate.getValidate_code()));

    }
}
