package com.example.beside.service;

import com.example.beside.domain.User;
import com.example.beside.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;

    @Transactional
    public Long saveUser(User user){
        validateDuplicateUser(user);

        userRepository.saveUser(user);
        return user.getId();
    }

    public List<User> findUserAll(){
        return userRepository.findUserAll();
    }

    public User findUser(Long userId){
        return userRepository.findUserById(userId);
    }

    private void validateDuplicateUser(User user){
        List<User> userAll = userRepository.findUserAll();
//        if(userAll.isEmpty() == false){
//            throw new IllegalStateException("이미 존재하는 회원입니다");
//        }
    }
}
