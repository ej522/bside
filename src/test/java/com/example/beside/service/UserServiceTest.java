package com.example.beside.service;

import com.example.beside.common.Exception.UserValidateNickName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.junit.jupiter.api.DisplayName;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.security.NoSuchAlgorithmException;
import java.util.List;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;

import com.example.beside.common.Exception.PasswordException;
import com.example.beside.common.Exception.PasswordNotCorrectException;
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
    private User user3 = new User();
    private User user4 = new User();
    private User user5 = new User();
    private User user6 = new User();
    private User user7 = new User();

    @BeforeEach
    public void setUp() throws PasswordException {
        user1.setEmail("test_1234@naver.com");
        user1.setPassword("wd!2awQWDas!@");

        user2.setEmail("test_2345@naver.com");
        user2.setPassword("12345");

        user3.setEmail("test_3456@naver.com");
        user3.setPassword("abcdefghi");

        user4.setEmail("test_4567@naver.com");
        user4.setPassword("1a!vD");

        user5.setId(52L);
        user5.setName("8자이상이상이상");

        user6.setId(52L);
        user6.setName("특수문자!");

        user7.setId(52L);
        user7.setName("은지");
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
    @DisplayName("틀린 패스워드로 유저 삭제")
    void testDeleteUserWithWrongPassword() throws NoSuchAlgorithmException, PasswordException {
        // given
        userService.saveUser(user1);
        user1.setPassword("test");

        // when, then
        assertThrows(IllegalStateException.class, () -> userService.deleteUser(user1));
    }

    @Test
    @DisplayName("존재하지 않는 유저 삭제")
    void testDeleteUserWithNotExist() throws NoSuchAlgorithmException, PasswordException {
        // when, then
        assertThrows(NullPointerException.class, () -> userService.deleteUser(user1));
    }

    @Test
    @DisplayName("유저 전체 조회")
    void testFindUserAll() throws PasswordException {
        // given
        userService.saveUser(user1);
        // when
        List<User> findUserAll = userService.findUserAll();
        // then
        Assertions.assertThat(findUserAll.size()).isGreaterThan(1);
    }

    @Test
    @DisplayName("유저 로그인")
    void testLoginUser() throws PasswordException, UserNotExistException, PasswordNotCorrectException {
        // given
        String password = user1.getPassword();
        userService.saveUser(user1);
        user1.setPassword(password);

        // when
        User loginUser = userService.loginUser(user1);

        // then
        Assertions.assertThat(loginUser.getId()).isNotNull();
        Assertions.assertThat(loginUser.getEmail()).isNotNull();
    }

    @Test
    @DisplayName("틀린 패스워드로 로그인")
    void testLoginUserWithWrongPassword() throws PasswordException, UserNotExistException {
        // given
        userService.saveUser(user1);
        user1.setPassword("wd!2awQWDas!");

        // when, then
        assertThrows(PasswordNotCorrectException.class, () -> userService.loginUser(user1));
    }

    @Test
    @DisplayName("존재하지 않는 이메일로 로그인")
    void testLoginUserWithNotExist() throws PasswordException, UserNotExistException {
        // given
        // when, then
        assertThrows(UserNotExistException.class, () -> userService.loginUser(user1));
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

    @Test
    @DisplayName("숫자로만 이루어진 패스워드로 유저 등록")
    void testSaveUserWithOnlyNumberPassword() throws PasswordException {
        // when, then
        assertThrows(PasswordException.class, () -> userService.saveUser(user2));
    }

    @Test
    @DisplayName("영어로만 이루어진 패스워드로 유저 등록")
    void testSaveUserWithOnlyEnglish() throws PasswordException {
        // when, then
        assertThrows(PasswordException.class, () -> userService.saveUser(user3));
    }

    @Test
    @DisplayName("8자 이하로 이루어진 패스워드로 유저 등록")
    void testSaveUserWithless8letter() throws PasswordException {
        // when, then
        assertThrows(PasswordException.class, () -> userService.saveUser(user4));
    }

    @Test
    @DisplayName("8자 이상 닉네임 변경")
    void testUpdateNickNameWithMore8letter() {
        //when, then
        assertThrows(UserValidateNickName.class, () -> userService.updateNickname(user5));
    }

    @Test
    @DisplayName("특수 문자가 포함된 닉네임 변경")
    void testUpdateNickNameIncludeExclamationMark() {
        //when, then
        assertThrows(UserValidateNickName.class, () -> userService.updateNickname(user6));
    }

    @Test
    @DisplayName("중복 닉네임")
    void testUpdateNickNameDuplication() {
        //when, then
        assertThrows(IllegalStateException.class, () -> userService.updateNickname(user7));
    }
}
