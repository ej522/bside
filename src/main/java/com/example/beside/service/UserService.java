package com.example.beside.service;

import com.example.beside.common.Exception.*;
import com.example.beside.domain.LoginType;
import com.example.beside.domain.User;
import com.example.beside.dto.FriendDto;
import com.example.beside.repository.UserRepository;
import com.example.beside.util.Common;
import com.example.beside.util.Encrypt;
import com.example.beside.util.PasswordConverter;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;

    @Transactional
    public User saveUser(User user) throws PasswordException, UserValidateNickName {
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
            throw new IllegalStateException("존재하지 않는 회원입니다");

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
    public User updateNickname(User user) throws Exception {
        String nickname = user.getName();
        Common.NicknameValidate(nickname);

        User updateUserInfo = userRepository.updateNickname(user);

        return updateUserInfo;
    }

    @Transactional
    public User updateAlarmState(User user) {
        User updateAlarmState = userRepository.UpdateAlarmState(user);

        return updateAlarmState;
    }

    @Transactional
    public User updateProfileImage(User user) throws Exception {
        User updateUserInfo = userRepository.updateProfileImage(user);

        return updateUserInfo;
    }

    @Transactional
    public Map<String, Object> updateTemporaryPassword(String email)
            throws UserNotExistException, NoSuchAlgorithmException {
        Optional<User> user = userRepository.findUserByEmail(email);
        Map<String, Object> chgInfo = new HashMap<>();

        if (user.isEmpty())
            throw new UserNotExistException("해당 이메일이 존재하지 않습니다");

        if (!user.get().getSocial_type().equals(LoginType.MOIM.name()))
            throw new UserNotExistException("해당 이메일이 존재하지 않습니다.");

        String randomPsw = Common.generateRandomPassword();
        String encryptPws = Encrypt.getHashingPassword(randomPsw);

        User userInfo = new User();
        userInfo.setId(user.get().getId());
        userInfo.setEmail(user.get().getEmail());
        userInfo.setSocial_type(user.get().getSocial_type());
        userInfo.setPassword(encryptPws);

        User chgUser = userRepository.updatePassword(userInfo);

        chgInfo.put("password", randomPsw);
        chgInfo.put("userInfo", chgUser);

        return chgInfo;

    }

    @Transactional
    public void updatePassword(User user, String new_password)
            throws PasswordException, PasswordNotCorrectException, CurrentPasswordEqualNewPassword {
        // 패스워드 검증
        Common.PasswordValidate(new_password);

        // 현재 비밀번호, 새 비밀번호 일치 여부
        var findUser = userRepository.findUserById(user.getId());
        var encrypedPassword = PasswordConverter.hashPassword(new_password);

        if (findUser.getPassword().equals(encrypedPassword))
            throw new CurrentPasswordEqualNewPassword("현재 비밀번호와 새 비밀번호가 일치합니다.");

        user.setPassword(PasswordConverter.hashPassword(new_password));
        userRepository.updatePassword(user);
    }

    public Boolean validateCurrentPassword(Long user_id, String validatedPsw) {
        User userInfo = userRepository.findUserById(user_id);

        // 비밀번호 일치 여부
        String hashPassword = PasswordConverter.hashPassword(validatedPsw);

        if (!userInfo.getPassword().equals(hashPassword))
            return false;

        return true;
    }

    public List<FriendDto> findFriendByUserId(User user) {
        return userRepository.findFriendByUserId(user.getId());
    }

}
