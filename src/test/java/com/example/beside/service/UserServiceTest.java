package com.example.beside.service;

import com.example.beside.common.Exception.*;
import com.example.beside.domain.LoginType;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.junit.jupiter.api.DisplayName;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;

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
        user1.setName("은지");
        user1.setProfile_image("https://moim.life/profile/yellow.jpg");

        user2.setEmail("test_2345@naver.com");
        user2.setPassword("12345");
        user2.setName("은지");
        user2.setProfile_image("https://moim.life/profile/yellow.jpg");

        user3.setEmail("test_3456@naver.com");
        user3.setPassword("abcdefghi");
        user3.setName("은지");
        user3.setProfile_image("https://moim.life/profile/yellow.jpg");

        user4.setEmail("test_4567@naver.com");
        user4.setPassword("1a!vD");
        user4.setName("은지");
        user4.setProfile_image("https://moim.life/profile/yellow.jpg");

        user5.setEmail("test_5678@naver.com");
        user5.setName("은지");
        user5.setPassword("wd!2awQWDas!@");
        user5.setProfile_image("https://moim.life/profile/yellow.jpg");

        user6.setEmail("test_6789@naver.com");
        user6.setName("은지");
        user6.setPassword("wd!2awQWDas!@");
        user6.setProfile_image("https://moim.life/profile/yellow.jpg");

        user7.setEmail("test_678@naver.com");
        user7.setName("은지");
        user7.setSocial_type(LoginType.MOIM.name());
        user7.setPassword("wd!2awQWDas!@");
        user7.setProfile_image("https://moim.life/profile/yellow.jpg");
    }

    @Test
    @DisplayName("유저 삭제")
    void testDeleteUser()
            throws NoSuchAlgorithmException, PasswordException, UserNotExistException, UserValidateNickName {
        // given
        String password = user1.getPassword();
        userService.saveUser(user1);
        user1.setPassword(password);

        // when
        userService.deleteUser(user1);
        Optional<User> findUserByEmail = userService.findUserByEmail(user1.getEmail());

        // then
        Assertions.assertThat(findUserByEmail).isEmpty();
    }

    @Test
    @DisplayName("존재하지 않는 유저 삭제")
    void testDeleteUserWithNotExist() throws NoSuchAlgorithmException, PasswordException {
        // when, then
        assertThrows(IllegalStateException.class, () -> userService.deleteUser(user1));
    }

    @Test
    @DisplayName("유저 전체 조회")
    void testFindUserAll() throws PasswordException, UserValidateNickName {
        // given
        userService.saveUser(user1);
        // when
        List<User> findUserAll = userService.findUserAll();
        // then
        Assertions.assertThat(findUserAll.size()).isGreaterThan(0);
    }

    @Test
    @DisplayName("유저 로그인")
    void testLoginUser()
            throws PasswordException, UserNotExistException, PasswordNotCorrectException, UserValidateNickName {
        // given
        String email = user1.getEmail();
        String password = user1.getPassword();
        userService.saveUser(user1);

        // when
        User testUser = new User();
        testUser.setEmail(email);
        testUser.setPassword(password);
        User loginUser = userService.loginUser(testUser);

        // then
        Assertions.assertThat(loginUser.getId()).isNotNull();
        Assertions.assertThat(loginUser.getEmail()).isNotNull();
    }

    @Test
    @DisplayName("틀린 패스워드로 로그인")
    void testLoginUserWithWrongPassword() throws PasswordException, UserNotExistException, UserValidateNickName {
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
    void testSaveUser() throws PasswordException, UserNotExistException, UserValidateNickName {
        // when
        userService.saveUser(user1);
        em.flush();

        // then
        Optional<User> findUserById = userService.findUserByEmail(user1.getEmail());

        Assertions.assertThat(user1.getEmail()).isEqualTo(findUserById.get().getEmail());
    }

    @Test
    @DisplayName("이미 등록된 유저 등록")
    void testSaveUserWithAlreadyRegistered() throws PasswordException, UserValidateNickName {
        // given
        userService.saveUser(user1);

        // when , then
        assertThrows(IllegalStateException.class, () -> userService.saveUser(user1));
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
    @DisplayName("닉네임 변경")
    void testUpdateNickname() throws Exception {
        // given
        String nickname = user5.getName();
        User saveUser = userService.saveUser(user5);
        saveUser.setName("변경닉네임");

        // when
        User updateUser = userService.updateNickname(saveUser);

        // then
        Assertions.assertThat(updateUser.getName()).isNotEqualTo(nickname);
        assertTrue(!nickname.equals(updateUser.getName()));
    }

    @Test
    @DisplayName("8자 이상 닉네임 변경")
    void testUpdateNickNameWithMore8letter() throws PasswordException, UserValidateNickName {
        // given
        userService.saveUser(user5);
        user5.setName("은지은지은지은지123");

        // when, then
        assertThrows(UserValidateNickName.class, () -> userService.updateNickname(user5));
    }

    @Test
    @DisplayName("임시 비밀번호 발급")
    void testUpdateTemporaryPassword() throws Exception {
        // given
        String beforePsw = user7.getPassword();
        User saveUser = userService.saveUser(user7);

        // when
        Map<String, Object> userInfo = userService.updateTemporaryPassword(saveUser.getEmail());

        // then
        String afterPsw = userInfo.get("password").toString();
        assertTrue(!beforePsw.equals(afterPsw));

    }

    @Test
    @DisplayName("비밀 번호 수정")
    void testUpdatePassword() throws Exception {
        // given
        User saveUser = userService.saveUser(user7);
        String beforePsw = saveUser.getPassword();

        // when
        userService.updatePassword(saveUser, "newPsw123!");

        // then
        userService.findUserById(saveUser.getId());
        Assertions.assertThat(beforePsw).isNotEqualTo(saveUser.getPassword());
    }

    @Test
    @DisplayName("현재 비밀번호가 틀렸을 경우")
    void testUpdatePasswordByWrongPassword() throws PasswordException, UserValidateNickName {
        // given
        User saveUser = userService.saveUser(user7);

        // when, then
        Assertions.assertThat(
                userService.validateCurrentPassword(saveUser.getId(), "wrongPsw12!")).isFalse();
    }

    @Test
    @DisplayName("숫자로만 이루어진 패스워드로 유저 등록")
    void testUpdatePasswordWithOnlyNumber() throws PasswordException, UserValidateNickName {
        // given
        User saveUser = userService.saveUser(user7);

        // when, then
        assertThrows(PasswordException.class, () -> userService.updatePassword(saveUser, "1234567890"));
    }

    @Test
    @DisplayName("영어로만 이루어진 패스워드로 유저 등록")
    void testUpdatePasswordWithOnlyEnglish() throws PasswordException, UserValidateNickName {
        // given
        User saveUser = userService.saveUser(user7);

        // when, then
        assertThrows(PasswordException.class, () -> userService.updatePassword(saveUser, "abcdefghijk"));
    }

    @Test
    @DisplayName("8자 이하로 이루어진 패스워드로 유저 등록")
    void testUpdatePasswordByWithless8letter() throws PasswordException, UserValidateNickName {
        // given
        User saveUser = userService.saveUser(user7);

        // when, then
        assertThrows(PasswordException.class, () -> userService.updatePassword(saveUser, "abc12!"));
    }

    @Test
    @DisplayName("현재 비밀번호와 새 비밀번호가 일치할 경우")
    void testUpdatePasswordWithEqualNewPassword() throws PasswordException, UserValidateNickName {
        // given
        User saveUser = userService.saveUser(user7);

        // when, then
        assertThrows(CurrentPasswordEqualNewPassword.class,
                () -> userService.updatePassword(saveUser, "wd!2awQWDas!@"));
    }

    @Test
    @DisplayName("유저 프로필 수정")
    void testUpdateProfileImg() throws Exception {
        // given
        String nickName = user7.getName();
        String profile_image = user7.getProfile_image();

        // 유저 등록
        User saveUser = userService.saveUser(user7);
        saveUser.setName("부엉이");
        saveUser.setProfile_image("https://moim.life/profile/skyblue.jpg");

        // when
        User user = userService.updateProfileImage(saveUser);

        // then
        Assertions.assertThat(nickName).isNotEqualTo(user.getName());
        Assertions.assertThat(profile_image).isNotEqualTo(user.getProfile_image());
    }

}
