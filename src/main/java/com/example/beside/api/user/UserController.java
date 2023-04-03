package com.example.beside.api.user;

import com.example.beside.domain.User;
import com.example.beside.dto.UserDto;
import com.example.beside.service.UserService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.web.bind.annotation.*;

import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

@Tag(name = "User", description = "유저 API")
@RequiredArgsConstructor
@RequestMapping("/api/users")
@RestController
public class UserController {

    private final UserService userService;

    @Operation(tags = { "User" }, summary = "사용자 목록 페이지 조회")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "사용자 목록 조회를 성공했습니다."),
            @ApiResponse(responseCode = "400", description = "존재하지 않는 사용자입니다.")
    })
    @GetMapping(value = "/v1/users")
    public List<UserDto> getAllUsers() {
        List<UserDto> UserDtoList = new ArrayList<>();

        List<User> userAll = userService.findUserAll();
        userAll.forEach(s -> {
            UserDto userDto = new UserDto(s.getId(), s.getPassword(), s.getEmail());
            UserDtoList.add(userDto);
        });
        return UserDtoList;
    }

    @Operation(tags = { "User" }, summary = "회원가입")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "회원가입이 완료되었습니다."),
            @ApiResponse(responseCode = "400", description = "올바른 형식의 이메일 주소여야 합니다")
    })
    @PostMapping(value = "/v1/signup")
    public void createUser(@RequestBody @Validated CreateUserRequest requset) {
        User user = new User();
        user.setEmail(requset.email);
        user.setPassword(requset.password);

        userService.saveUser(user);
    }

    @Operation(tags = { "User" }, summary = "회원삭제")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "유저가 삭제되었습니다."),
            @ApiResponse(responseCode = "400", description = "존재하지 않는 유저입니다")
    })
    @DeleteMapping(value = "/v1/delete")
    public void deleteUser(@RequestBody @Validated DeleteUserRequest request) throws NoSuchAlgorithmException {

        User user = new User();
        user.setEmail(request.email);
        user.setPassword(request.password);
        userService.deleteUser(user);
    }

    @Data
    static class CreateUserRequest {
        @NotNull
        @Email
        private String email;
        @NotNull
        private String password;
    }

    @Data
    static class DeleteUserRequest {
        @NotNull
        @Email
        private String email;
        @NotNull
        private String password;
    }
}
