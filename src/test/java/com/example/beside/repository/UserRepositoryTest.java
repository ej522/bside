package com.example.beside.repository;

import java.util.List;
import java.util.Optional;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.example.beside.domain.User;

import jakarta.transaction.Transactional;

@SpringBootTest
@Transactional
public class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;
    private User user;

    @BeforeEach
    @DisplayName("각각의 Test 함수 실행 전 실행되는 함수")
    void settingEntity() {
        // 유저1 세팅
        user = new User();
        user.setName("부엉이");
        user.setEmail("test-user@google.com");
        user.setPassword("myMoim@0313");

    }

    @Test
    @DisplayName("유저를 삭제할 수 있는가")
    void testDeleteUser() {
        // given
        User saveUser = userRepository.saveUser(user);

        // when
        userRepository.deleteUser(saveUser);

        // then
        Optional<User> findUserByEmail = userRepository.findUserByEmail(user.getEmail());
        Assertions.assertThat(findUserByEmail).isEmpty();
    }

    @Test
    @DisplayName("유저를 전체 조회할 수 있는가?")
    void testFindUserAll() {
        // given
        userRepository.saveUser(user);

        // when
        List<User> findUserAll = userRepository.findUserAll();

        // then
        Assertions.assertThat(findUserAll).size().isGreaterThan(0);
    }

    @Test
    @DisplayName("이메일로 유저를 찾을 수 있는가?")
    void testFindUserByEmail() {
        // given
        userRepository.saveUser(user);

        // when
        Optional<User> findUserByEmail = userRepository.findUserByEmail(user.getEmail());

        // then
        Assertions.assertThat(findUserByEmail.get().getEmail()).isEqualTo(user.getEmail());
        Assertions.assertThat(findUserByEmail.get().getName()).isEqualTo(user.getName());
    }

    @Test
    @DisplayName("유저를 저장할 수 있는가?")
    void testSaveUser() {
        // when
        User saveUser = userRepository.saveUser(user);

        // then
        Assertions.assertThat(saveUser.getEmail()).isEqualTo("test-user@google.com");
        Assertions.assertThat(saveUser.getName()).isEqualTo("부엉이");
    }
}
