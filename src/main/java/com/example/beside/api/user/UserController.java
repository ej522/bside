package com.example.beside.api.user;

import com.example.beside.common.Exception.*;
import com.example.beside.common.response.Response;
import com.example.beside.domain.User;
import com.example.beside.dto.UserDto;
import com.example.beside.dto.UserTokenDto;
import com.example.beside.service.EmailService;
import com.example.beside.service.UserService;
import com.example.beside.util.JwtProvider;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
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
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

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
    public Response<UserTokenDto> login(@RequestBody @Validated CreateUserRequest requset)
            throws PasswordException, UserNotExistException, PasswordNotCorrectException {
        User user = new User();
        user.setEmail(requset.email);
        user.setPassword(requset.password);

        User userInfo = userService.loginUser(user);
        String userToken = JwtProvider.createToken(userInfo);

        UserTokenDto result = new UserTokenDto(userToken, new UserDto(userInfo));
        return Response.success(200, "정상 로그인 되었습니다.", result);
    }

    @Operation(tags = { "User" }, summary = "회원가입")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "회원가입이 완료되었습니다."),
            @ApiResponse(responseCode = "400", description = "올바른 형식의 이메일, 패스워드야 합니다")
    })
    @PostMapping(value = "/v1/signup")
    public Response<Void> createUser(@RequestBody @Validated CreateUserRequest requset) throws PasswordException {
        User user = new User();
        user.setEmail(requset.email);
        user.setPassword(requset.password);

        userService.saveUser(user);

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
            return Response.success(202, "이메일 인증이 실패 했습니다", isValid);
    }

    @Operation(tags = { "User" }, summary = "회원삭제")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "유저가 삭제되었습니다."),
            @ApiResponse(responseCode = "400", description = "존재하지 않는 유저입니다")
    })
    @DeleteMapping(value = "/v1/delete")
    public Response<Void> deleteUser(@RequestBody @Validated DeleteUserRequest request)
            throws NoSuchAlgorithmException {

        User user = new User();
        user.setEmail(request.email);
        user.setPassword(request.password);
        userService.deleteUser(user);

        return Response.success(200, "유저가 삭제되었습니다.", null);
    }

    @Operation(tags = { "User" }, summary = "닉네임변경")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "닉네임이 변경 되었습니다."),
            @ApiResponse(responseCode = "400", description = "한글, 영문, 숫자 조합 8자 이내"),
            @ApiResponse(responseCode = "500", description = "중복된 닉네임 입니다.")})
    @PutMapping(value = "/v1/update/nickname")
    public Response<User> updateNickname(@RequestBody @Validated UpdateUserNicknameRequest request) throws UserValidateNickName {
        User user = new User();
        user.setId(request.id);
        user.setName(request.name);

        user = userService.updateNickname(user);

        return Response.success(200, "닉네임이 변경 되었습니다.", user);
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
    static class CreateUserRequest {
        @NotNull
        @Email
        @Schema(description = "email", example = "test@email.com", type = "String")
        private String email;

        @NotNull
        @Schema(description = "password", example = "password", type = "String")
        private String password;
    }

    @Data
    static class DeleteUserRequest {
        @NotNull
        @Email
        @Schema(description = "email", example = "test@email.com", type = "String")
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

        @NotNull
        @Schema(description = "회원번호", example = "1", type = "Long")
        private Long id;
    }
}
