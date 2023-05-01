package com.example.beside.service;

import com.example.beside.common.Exception.PasswordException;
import com.example.beside.common.Exception.PasswordNotCorrectException;
import com.example.beside.common.Exception.UserNotExistException;
import com.example.beside.common.Exception.UserValidateNickName;
import com.example.beside.domain.LoginType;
import com.example.beside.domain.User;
import com.example.beside.repository.UserRepository;
import com.example.beside.util.Common;
import com.example.beside.util.Encrypt;
import com.example.beside.util.PasswordConverter;

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
    public Long saveUser(User user) throws PasswordException, UserValidateNickName {
        Optional<User> findUser = userRepository.findUserByEmail(user.getEmail());
        if (findUser.isPresent())
            throw new IllegalStateException("이미 존재하는 회원입니다");

        // 패스워드 검증
        var password = user.getPassword();
        Common.PasswordValidate(password);
        String hashPassword = PasswordConverter.hashPassword(password);

        user.setPassword(hashPassword);
        user.setSocial_type(LoginType.MOIM.name());

        // 닉네임 검증
        Common.NicknameValidate(user.getName());
        Optional<User> findNickname = userRepository.findUserNicknameByMoim(user.getName());
        if (findNickname.isPresent()) {
            throw new IllegalStateException("중복된 닉네임입니다.");
        }

        return userRepository.saveUser(user);
    }

    public User loginUser(User user) throws PasswordException, UserNotExistException, PasswordNotCorrectException {
        String password = user.getPassword();
        // 패스워드 검증
        Common.PasswordValidate(password);

        Optional<User> OptionalUser = userRepository
                .findUserByEmailAndPassword(user.getEmail());

        OptionalUser.orElseThrow(() -> new UserNotExistException("해당 계정이 존재하지 않습니다"));
        User userInfo = OptionalUser.get();
        String hashPassword = PasswordConverter.hashPassword(password);

        if (!userInfo.getPassword().equals(hashPassword))
            throw new PasswordNotCorrectException("비밀번호가 일치하지 않습니다");

        return OptionalUser.get();
    }

    @Transactional
    public void deleteUser(User user) throws NoSuchAlgorithmException, UserNotExistException {
        Optional<User> findUserByEmail = findUserByEmail(user.getEmail());

        if (!findUserByEmail.isPresent())
            throw new IllegalStateException("이미 존재하는 회원입니다");

        if (!findUserByEmail.get().getPassword().toString().equals(Encrypt.getHashingPassword(user.getPassword())))
            throw new IllegalStateException("비밀번호가 동일하지 않습니다");

        userRepository.deleteUser(findUserByEmail.get());
    }

    public List<User> findUserAll() {
        return userRepository.findUserAll();
    }

    public User findUserById(Long userId) {
        return userRepository.findUserById(userId);
    }

    public Optional<User> findUserByEmail(String email) throws UserNotExistException {
        return userRepository.findUserByEmail(email);
    }

    @Transactional
    public String updateNickname(User user) throws Exception {
        String nickname = user.getName();
        if (user.getSocial_type().equals("MOIM")) {
            Optional<User> optionalUser = userRepository.findUserNicknameByMoim(nickname);
            if (optionalUser.isPresent()) {
                throw new IllegalStateException("중복된 닉네임입니다.");
            }
        }

        Common.NicknameValidate(nickname);

        System.out.println("user:"+user.getId());
        User updateUserInfo = userRepository.updateNickname(user);

        return updateUserInfo.getName();
    }

    @Transactional
    public User updateProfileImage(User user) throws Exception {
        User updateUserInfo = userRepository.updateProfileImage(user);

        return updateUserInfo;
    }

    @Transactional
    public String updateTemporaryPassword(String email) throws UserNotExistException, NoSuchAlgorithmException {
        Optional<User> user = userRepository.findUserByEmail(email);
        System.out.println("user_id="+user.get().getId());

        if(user.isEmpty()) {
            throw new UserNotExistException("해당 이메일이 존재하지 않습니다");
        } else {
            if(!user.get().getSocial_type().equals(LoginType.MOIM.name())) {
                throw new UserNotExistException("해당 이메일이 존재하지 않습니다");
            } else {
                String randomPsw = Common.generateRandomPassword();
                System.out.println("randomPsw="+randomPsw);
                String encryptPws = Encrypt.getHashingPassword(randomPsw);
                System.out.println("encryptPws="+encryptPws);

                User userInfo = new User();
                userInfo.setId(user.get().getId());
                userInfo.setPassword(encryptPws);
                userInfo.setEmail(user.get().getEmail());
                userInfo.setSocial_type(user.get().getSocial_type());

                System.out.println("userInfo_id="+userInfo.getId());
                System.out.println("userInfo_password="+userInfo.getPassword());
                System.out.println("yp0date="+userRepository.updatePassword(userInfo).getPassword());

                return randomPsw;
            }
        }
    }

}
