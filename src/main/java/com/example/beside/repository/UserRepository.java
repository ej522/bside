package com.example.beside.repository;

import java.util.List;
import java.util.Optional;

import com.example.beside.domain.User;
import com.example.beside.dto.FriendDto;

public interface UserRepository {
    // CREATE
    User saveUser(User user);

    // READ
    User findUserById(Long id);

    Optional<User> findUserByEmail(String email);

    Optional<User> findUserByEmailAndSocialType(String email, String social_type);

    Optional<User> findUserNickname(String nickName);

    List<FriendDto.FriendInfo> findFriendByUserId(Long user_id);

    List<User> findUserAll();

    // UPDATE
    User updateFcmToken(User user);

    User updateNickname(User user);

    User updatePassword(User user);

    User updateProfileImage(User user);

    User UpdateAlarmState(User user);

    // DELETE
    void deleteUser(User user);

    //List<FriendDto.FriendInfo> test(Long user_id);
}
