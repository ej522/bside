package com.example.beside.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.junit.jupiter.api.DisplayName;

import java.security.NoSuchAlgorithmException;
import java.util.List;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;

import com.example.beside.common.Exception.PasswordException;
import com.example.beside.common.Exception.UserNotExistException;
import com.example.beside.domain.QUser;
import com.example.beside.domain.User;
import com.querydsl.jpa.impl.JPAQueryFactory;

import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;

@SpringBootTest
@Transactional
public class UserServiceTest {

    @Autowired
    private UserService userService;

    @Autowired
    private EntityManager em;
    private JPAQueryFactory queryFactory;

    private User user1 = new User();
    private User user2 = new User();

    @BeforeEach
    public void setUp() throws PasswordException {
        user1.setEmail("test_1234@naver.com");
        user1.setPassword("wd!2awQWDas!@");

        user2.setEmail("test_4321@naver.com");
        user2.setPassword("dw2dscaAD!@");
    }

    @AfterEach
    public void AfterEach() throws PasswordException, NoSuchAlgorithmException {
        queryFactory = new JPAQueryFactory(em);
        QUser qUser = new QUser("u");

        queryFactory.delete(qUser).where(qUser.email.eq(user1.getEmail())).execute();
        queryFactory.delete(qUser).where(qUser.email.eq(user2.getEmail())).execute();
    }

    @Test
    @DisplayName("유저 삭제")
    void testDeleteUser() throws NoSuchAlgorithmException, PasswordException {
        // given
        String password = user1.getPassword();
        userService.saveUser(user1);
        user1.setPassword(password);

        // when
        userService.deleteUser(user1);
        em.flush();

        // then
        User findUserByEmail = userService.findUserByEmail(user1.getEmail());
        Assertions.assertThat(findUserByEmail).isNull();
    }

    @Test
    @DisplayName("유저 전체 조회")
    void testFindUserAll() throws PasswordException {
        // given
        userService.saveUser(user1);
        userService.saveUser(user2);
        em.flush();
        // when
        List<User> findUserAll = userService.findUserAll();
        // then
        Assertions.assertThat(findUserAll.size()).isGreaterThan(2);
    }

    @Test
    @DisplayName("유저 로그인")
    void testLoginUser() throws PasswordException, UserNotExistException {
        // given
        String password = user1.getPassword();
        userService.saveUser(user1);
        user1.setPassword(password);

        // when
        String token = userService.loginUser(user1);

        // then
        // TODO 토큰 복호화 검증 필요
        Assertions.assertThat(token).isNotEmpty();
    }

    @Test
    @DisplayName("유저 등록")
    void testSaveUser() throws PasswordException {
        // when
        userService.saveUser(user1);
        em.flush();

        // then
        User findUserById = userService.findUserByEmail(user1.getEmail());

        Assertions.assertThat(user1.getEmail()).isEqualTo(findUserById.getEmail());
    }
}
