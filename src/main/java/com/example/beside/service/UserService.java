package com.example.beside.service;

import com.example.beside.domain.User;
import com.example.beside.repository.UserRepository;
import com.example.beside.util.Encrypt;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.NoSuchAlgorithmException;
import java.util.Optional;
import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;

    @Transactional
    public Long saveUser(User user) {
        userRepository.saveUser(user);
        return user.getId();
    }

    @Transactional
    public void deleteUser(User user) throws NoSuchAlgorithmException {
        User findUserByEmail = findUserByEmail(user.getEmail());
        Optional<User> optionalUser = Optional.of(findUserByEmail);

        if (!optionalUser.isPresent())
            throw new IllegalStateException("이미 존재하는 회원입니다");

        if (!optionalUser.get().getPassword().toString().equals(Encrypt.getHashingPassword(user.getPassword())))
            throw new IllegalStateException("비밀번호가 동일하지 않습니다");

        userRepository.deleteUser(findUserByEmail);
    }

    public List<User> findUserAll() {
        return userRepository.findUserAll();
    }

    public User findUserById(Long userId) {
        return userRepository.findUserById(userId);
    }

    public User findUserByEmail(String email) {
        return userRepository.findUserByEmail(email);
    }

}
