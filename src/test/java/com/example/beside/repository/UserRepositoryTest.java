package com.example.beside.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
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
        user.setEmail("test_user@google.com");
        user.setPassword("1234");

    }

    @Test
    @DisplayName("유저를 id 로 삭제할 수 있는가")
    void testDeleteUser() {
        // given
        userRepository.saveUser(user);

        // when
        userRepository.deleteUser(user);
        User userInfo = userRepository.findUserById(user.getId());

        // then
        assertNull(userInfo);
    }

    @Test
    @DisplayName("유저를 전체 조회할 수 있는가?")
    void testFindUserAll() {
        // given
        userRepository.saveUser(user);

        // when
        List<User> findUserAll = userRepository.findUserAll();

        // then
        assertTrue(findUserAll.size() > 0);
    }

    @Test
    void testFindUserByEmail() {
        // given
        userRepository.saveUser(user);

        // when
        User findUserByEmail = userRepository.findUserByEmail(user.getEmail());

        // then
        assertEquals(findUserByEmail, user);
    }

    @Test
    void testFindUserById() {
        // given
        userRepository.saveUser(user);

        // when
        User findUserByEmail = userRepository.findUserByEmail(user.getEmail());

        // then
        assertEquals(findUserByEmail, user);

    }

    @Test
    @DisplayName("유저를 저장할 수 있는가?")
    void testSaveUser() {
        // when
        userRepository.saveUser(user);
        User findedUser = userRepository.findUserById(user.getId());

        // then
        assertEquals(findedUser.getId(), user.getId());
        assertEquals(findedUser.getEmail(), user.getEmail());
    }
}
