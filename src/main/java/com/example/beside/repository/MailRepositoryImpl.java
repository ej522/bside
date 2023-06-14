package com.example.beside.repository;

import java.time.LocalDateTime;

import org.springframework.stereotype.Repository;

import com.example.beside.common.Exception.ExceptionDetail.EmailValidateException;
import com.example.beside.domain.EmailValidate;
import com.example.beside.domain.QEmailValidate;
import com.querydsl.jpa.impl.JPAQueryFactory;

import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class MailRepositoryImpl implements MailRepository {
        private final EntityManager em;
        private JPAQueryFactory queryFactory;

        @Override
        public long saveEmailValidateCode(String email, String validateCode) {
                queryFactory = new JPAQueryFactory(em);
                QEmailValidate qEmail = new QEmailValidate("email");

                queryFactory.insert(qEmail)
                                .columns(qEmail.email, qEmail.validate_code, qEmail.create_time)
                                .values(email, validateCode, LocalDateTime.now())
                                .execute();

                return queryFactory.select(qEmail.id)
                                .from(qEmail)
                                .where(qEmail.email.eq(email)
                                                .and(qEmail.validate_code.eq(validateCode)))
                                .orderBy(qEmail.create_time.desc())
                                .fetchFirst();
        }

        @Override
        public Boolean checkEmailValidateTime(String email, String validateCode) throws EmailValidateException {
                queryFactory = new JPAQueryFactory(em);
                QEmailValidate qEmail = new QEmailValidate("email");

                EmailValidate result = queryFactory.selectFrom(qEmail)
                                .from(qEmail)
                                .where(qEmail.email.eq(email))
                                .orderBy(qEmail.create_time.desc())
                                .fetchFirst();

                if (result == null)
                        throw new EmailValidateException("해당 이메일이 존재하지 않습니다 ");

                LocalDateTime now = LocalDateTime.now();
                LocalDateTime created_time = result.getCreate_time();

                // 이메일 인증 시간 + 3분 <= 현재 시간
                if (created_time.plusMinutes(3).isBefore(now)) {
                        throw new EmailValidateException("제한 시간을 초과했습니다.");
                }

                if (!result.getValidate_code().equals(validateCode)) {
                        throw new EmailValidateException("인증 코드가 일치하지 않습니다");
                }

                return true;
        }
}
