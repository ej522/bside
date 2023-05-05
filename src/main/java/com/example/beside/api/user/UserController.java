package com.example.beside.api.user;

import com.example.beside.common.Exception.*;
import com.example.beside.common.response.Response;
import com.example.beside.domain.User;
import com.example.beside.dto.FriendDto;
import com.example.beside.dto.UserDto;
import com.example.beside.dto.UserTokenDto;
import com.example.beside.service.EmailService;
import com.example.beside.service.UserService;
import com.example.beside.util.JwtProvider;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import org.springframework.validation.annotation.Validated;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.web.bind.annotation.*;

import jakarta.mail.MessagingException;
import java.security.NoSuchAlgorithmException;
import java.util.*;

@Tag(name = "User", description = "유저 API")
@RequiredArgsConstructor
@RequestMapping("/api/users")
@RestController
public class UserController {

    private final EmailService emailService;

    private final UserService userService;

    @Operation(tags = { "User" }, summary = "사용자 목록 페이지 조회")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "사용자 목록 조회를 성공했습니다."),
            @ApiResponse(responseCode = "400", description = "존재하지 않는 사용자입니다.")
    })
    @GetMapping(value = "/v1/users")
    public Response<List<UserDto>> getAllUsers(HttpServletRequest request) {
        List<UserDto> UserDtoList = new ArrayList<>();
        User user_ = (User) request.getAttribute("user");

        List<User> userAll = userService.findUserAll();
        userAll.forEach(s -> {
            UserDto userDto = new UserDto(s.getId(), s.getPassword(), s.getEmail(), s.getName(), s.getProfile_image(),
                    s.getSocial_type());
            UserDtoList.add(userDto);
        });

        return Response.success(200, "유저 목록 조회를 완료했습니다", UserDtoList);
    }

    @Operation(tags = { "User" }, summary = "로그인")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "정상 로그인 되었습니다."),
            @ApiResponse(responseCode = "400", description = "해당 계정이 존재하지 않습니다."),
            @ApiResponse(responseCode = "401", description = "비밀번호가 일치하지 않습니다.")
    })
    @PostMapping(value = "/v1/login")
    public Response<UserTokenDto> login(@RequestBody @Validated LoginUserRequest requset, HttpServletResponse response)
            throws PasswordException, UserNotExistException, PasswordNotCorrectException {
        User user = new User();
        user.setEmail(requset.email);
        user.setPassword(requset.password);

        User userInfo = userService.loginUser(user);
        String userToken = JwtProvider.createToken(userInfo);
        response.addHeader("Authorization", "Bearer " + userToken);

        UserTokenDto result = new UserTokenDto(userToken, new UserDto(userInfo));
        return Response.success(200, "정상 로그인 되었습니다.", result);
    }

    @Operation(tags = { "User" }, summary = "회원가입")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "회원가입이 완료되었습니다."),
            @ApiResponse(responseCode = "400", description = "올바른 형식의 이메일, 패스워드야 합니다")
    })
    @PostMapping(value = "/v1/signup")
    public Response<Void> createUser(@RequestBody @Validated CreateUserRequest requset, HttpServletResponse response)
            throws PasswordException, UserNotExistException, UserValidateNickName, PasswordNotCorrectException {
        User user = new User();
        user.setEmail(requset.email);
        user.setPassword(requset.password);
        user.setName(requset.name);
        user.setProfile_image(requset.imgUrl);

        User saveUser = userService.saveUser(user);
        String userToken = JwtProvider.createToken(saveUser);
        response.addHeader("Authorization", "Bearer " + userToken);

        return Response.success(201, "회원가입이 완료되었습니다.", null);
    }

    @Operation(tags = { "User" }, summary = "이메일 인증번호 전송")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "이메일 인증 코드가 발송되었습니다."),
            @ApiResponse(responseCode = "500", description = "이메일 전송이 실패했습니다.")
    })
    @PostMapping(value = "/v1/signup/email-validate")
    public Response<String> sendVerificationEmail(@RequestBody @Validated EmailRequest request) {
        try {
            String verificationCode = generateVerificationCode();

            emailService.sendVerificationEmail(request.getEmail(), verificationCode);
            return Response.success(200, "이메일 인증 번호를 발송했습니다.", null);

        } catch (MessagingException ex) {
            return Response.fail(500, ex.getMessage());
        }
    }

    @Operation(tags = { "User" }, summary = "이메일 인증 코드 확인")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "이메일 인증 코드가 발송되었습니다."),
            @ApiResponse(responseCode = "500", description = "이메일 전송이 실패했습니다.")
    })
    @PostMapping(value = "/v1/signup/verify-code")
    public Response<Boolean> checkVerificationCode(@RequestBody @Validated EmailValidateRequest request)
            throws EmailValidateException {

        Boolean isValid = emailService.checkEmailValidate(request.getEmail(), request.getValidateCode());
        if (isValid)
            return Response.success(200, "이메일 인증이 완료 되었습니다.", isValid);
        else
            return Response.fail(400, "이메일 인증이 실패 했습니다", isValid);
    }

    @Operation(tags = { "User" }, summary = "회원삭제")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "유저가 삭제되었습니다."),
            @ApiResponse(responseCode = "400", description = "존재하지 않는 유저입니다")
    })
    @DeleteMapping(value = "/v1/delete")
    public Response<Void> deleteUser(@RequestBody @Validated DeleteUserRequest request)
            throws NoSuchAlgorithmException, UserNotExistException {

        User user = new User();
        user.setEmail(request.email);
        user.setPassword(request.password);
        userService.deleteUser(user);

        return Response.success(200, "유저가 삭제되었습니다.", null);
    }

    @Operation(tags = { "User" }, summary = "닉네임변경")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "닉네임이 변경 되었습니다."),
            @ApiResponse(responseCode = "400", description = "닉네임은 8자 이내여야 합니다.") })
    @PutMapping(value = "/v1/update/nickname")
    public Response<UserDto> updateNickname(HttpServletRequest token,
            @RequestBody @Validated UpdateUserNicknameRequest request)
            throws Exception {
        User user = (User) token.getAttribute("user");

        User updateUser = new User();
        updateUser.setId(user.getId());
        updateUser.setName(request.name);
        updateUser.setSocial_type(user.getSocial_type());

        User changUser = userService.updateNickname(updateUser);

        UserDto userDto = new UserDto(changUser);

        return Response.success(200, "닉네임이 변경 되었습니다.", userDto);
    }

    @Operation(tags = { "User" }, summary = "이메일 계정 확인")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "해당 이메일이 존재합니다"),
            @ApiResponse(responseCode = "400", description = "해당 이메일이 존재하지 않습니다") })
    @PostMapping(value = "/v1/check-email")
    public Response<String> checkEmailAccount(@RequestBody @Validated EmailRequest request)
            throws UserNotExistException {
        var email = request.getEmail();
        var user = userService.findUserByEmail(email);

        if (user.isEmpty())
            return Response.success(200, "중복된 이메일이 없습니다", "Success");
        else
            return Response.success(400, "해당 이메일이 존재합니다", "Error");
    }

    @Operation(tags = { "User" }, summary = "프로필 이미지 전체 조회")
    @ApiResponses(value = { @ApiResponse(responseCode = "200", description = "프로필 이미지가 조회 되었습니다.") })
    @GetMapping(value = "/v1/all-Profile-img")
    public Response<List<String>> getAllProfileImage() {
        List<String> profileList = new ArrayList<String>();

        String img = "";
        img = "https://moim.life/profile/green.jpg";
        profileList.add(img);
        img = "https://moim.life/profile/heart.jpg";
        profileList.add(img);
        img = "https://moim.life/profile/lightgreen.jpg";
        profileList.add(img);
        img = "https://moim.life/profile/lightpurple.jpg";
        profileList.add(img);
        img = "https://moim.life/profile/purple_bubble.jpg";
        profileList.add(img);
        img = "https://moim.life/profile/purple_diamond.jpg";
        profileList.add(img);
        img = "https://moim.life/profile/purple_flower.jpg";
        profileList.add(img);
        img = "https://moim.life/profile/skyblue.jpg";
        profileList.add(img);
        img = "https://moim.life/profile/yellow.jpg";
        profileList.add(img);

        return Response.success(200, "프로필 이미지가 조회 되었습니다.", profileList);
    }

    @Operation(tags = { "User" }, summary = "유저프로필 수정")
    @ApiResponses(value = { @ApiResponse(responseCode = "200", description = "프로필이 수정되었습니다.") })
    @PutMapping("/v1/update/profile-image")
    public Response<UserDto> updateProfileImage(HttpServletRequest token,
            @RequestBody @Validated UpdateUserProfileImage updateUserProfileImage) throws Exception {
        User user = (User) token.getAttribute("user");

        User updateUser = new User();
        updateUser.setId(user.getId());
        updateUser.setProfile_image(updateUserProfileImage.profile_image);

        updateUser = userService.updateProfileImage(updateUser);

        UserDto userDto = new UserDto(updateUser);

        return Response.success(200, "프로필 이미지가 수정 되었습니다.", userDto);
    }

    @Operation(tags = { "User" }, summary = "임시 비밀번호 발급")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "이메일 인증 번호를 발송했습니다."),
            @ApiResponse(responseCode = "500", description = "이메일 전송 실패") })
    @PostMapping("/v1/send-password")
    public Response<String> sendPassword(@RequestBody @Validated EmailRequest request) throws Exception {
        Map<String, Object> userInfo = userService.updateTemporaryPassword(request.email);
        User user = (User) userInfo.get("userInfo");

        try {
            emailService.sendTemporaryPasswordEmail(request.email, user.getName(), userInfo.get("password").toString());
        } catch (MessagingException ex) {
            return Response.fail(500, ex.getMessage());
        }

        return Response.success(200, "이메일 인증 번호를 발송했습니다.", "Success");
    }

    @Operation(tags = { "User" }, summary = "현재 비밀번호 확인")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "비밀번호가 변경되었습니다."),
            @ApiResponse(responseCode = "400", description = "비밀번호가 일치하지 않습니다.") })
    @PostMapping("/v1/check/current-password")
    public Response validateCurrentPasswordMatch(HttpServletRequest token,
            @RequestBody @Validated PasswordRequest passwordRequest)
            throws PasswordException, PasswordNotCorrectException, CurrentPasswordEqualNewPassword {
        User user = (User) token.getAttribute("user");

        if (!userService.validateCurrentPassword(user.getId(), passwordRequest.current_password)) {
            throw new PasswordNotCorrectException("비밀번호가 일치하지 않습니다.");
        }

        return Response.success(200, "비밀 번호가 일치합니다.", "");
    }

    @Operation(tags = { "User" }, summary = "비밀번호 수정")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "비밀번호가 변경되었습니다."),
            @ApiResponse(responseCode = "400", description = "현재 비밀번호와 새 비밀번호가 일치합니다.") })
    @PutMapping("/v1/update/password")
    public Response updatePassword(HttpServletRequest token, @RequestBody @Validated PasswordRequest passwordRequest)
            throws PasswordException, PasswordNotCorrectException, CurrentPasswordEqualNewPassword {
        User user = (User) token.getAttribute("user");
        user.setPassword(passwordRequest.current_password);

        userService.updatePassword(user, passwordRequest.new_password);

        return Response.success(200, "비밀번호가 변경되었습니다.", "");
    }

    @Operation(tags = { "User" }, summary = "친구 목록 조회")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "친구 목록이 조회되었습니다.") })
    @GetMapping("/v1/my-friend")
    public Response<List<FriendDto>> getMyfriendList(HttpServletRequest token) {
        User user = (User) token.getAttribute("user");

        List<FriendDto> friendDtoList = userService.findFriendByUserId(user);

        return Response.success(200, "친구 목록이 조회되었습니다.", friendDtoList);
    }

    private String generateVerificationCode() {
        String numbers = "";
        Random random = new Random();
        int bound = 9; // 1부터 9까지의 숫자 중에서 랜덤으로 추출

        while (numbers.length() < 6) {
            String randomNumber = String.valueOf(random.nextInt(bound));
            numbers += randomNumber;
        }

        return numbers;
    }

    @Data
    static class EmailRequest {
        @NotNull
        @Email
        @Schema(description = "email", example = "test@email.com", type = "String")
        private String email;
    }

    @Data
    static class EmailValidateRequest {
        @NotNull
        @Email
        @Schema(description = "email", example = "test@email.com", type = "String")
        private String email;

        @NotNull
        @Schema(description = "인증번호", example = "321219", type = "String")
        private String validateCode;
    }

    @Data
    static class LoginUserRequest {
        @NotNull
        @Email
        @Schema(description = "email", example = "test@naver.com", type = "String")
        private String email;

        @NotNull
        @Schema(description = "password", example = "password", type = "String")
        private String password;
    }

    @Data
    static class CreateUserRequest {
        @NotNull
        @Email
        @Schema(description = "email", example = "test@naver.com", type = "String")
        private String email;

        @NotNull
        @Schema(description = "password", example = "password", type = "String")
        private String password;

        @Schema(description = "image_url", example = "https://www.moim.life", type = "String")
        private String imgUrl;

        @Schema(description = "nickname", example = "닉네임", type = "String")
        private String name;
    }

    @Data
    static class DeleteUserRequest {
        @NotNull
        @Email
        @Schema(description = "email", example = "test@naver.com", type = "String")
        private String email;

        @NotNull
        @Schema(description = "password", example = "password", type = "String")
        private String password;
    }

    @Data
    static class UpdateUserNicknameRequest {
        @NotNull
        @Schema(description = "nickname", example = "닉네임", type = "String")
        private String name;
    }

    @Data
    static class UpdateUserProfileImage {
        @NotNull
        @Schema(description = "profile_image", example = "https://moim.life/profile/yellow.jpg", type = "String")
        private String profile_image;
    }

    @Data
    static class PasswordRequest {
        @NotNull
        @Schema(description = "현재 비밀번호", example = "password123!", type = "String")
        private String current_password;

        @NotNull
        @Schema(description = "새 비밀번호", example = "newPassword123!", type = "String")
        private String new_password;

    }
}
