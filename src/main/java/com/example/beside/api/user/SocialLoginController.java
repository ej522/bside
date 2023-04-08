package com.example.beside.api.user;

import com.example.beside.common.Exception.UserAlreadyExistException;
import com.example.beside.common.Exception.UserNotExistException;
import com.example.beside.common.response.Response;
import com.example.beside.domain.User;
import com.example.beside.dto.UserDto;
import com.example.beside.dto.UserTokenDto;
import com.example.beside.service.SocialLoginService;
import com.example.beside.service.UserService;
import com.example.beside.util.JwtProvider;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/social")
public class SocialLoginController {

    private final SocialLoginService socialLoginService;

    private final UserService userService;

    private final JwtProvider jwtProvider;

    @Operation(tags = { "Social" }, summary = "카카오 로그인")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "정상 로그인 되었습니다."),
            @ApiResponse(responseCode = "400", description = "해당 계정이 존재하지 않습니다.")
    })
    @PostMapping(value = "/v1/login/Kakao")
    public Response<UserTokenDto> kakaoLogin(@RequestBody @Valid SocialUserRequest request) throws UserNotExistException {
        User userInfo = socialLoginService.getKaKaoUserInfo(request.access_token);

        User user = socialLoginService.loginKakao(userInfo);

        // jwt 토큰발급
        String userToken = jwtProvider.createToken(user);
        UserTokenDto result = new UserTokenDto(userToken, new UserDto(user));

        return Response.success(200, "정상 로그인 되었습니다.", result);
    }

    @Operation(tags = { "social" }, summary = "카카오 회원가입")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "회원가입이 완료 되었습니다."),
            @ApiResponse(responseCode = "400", description = "이미 존재하는 회원입니다.")
    })
    @PostMapping(value = "v1/signup/Kakao")
    public Response<Void> kakaoSinup(@RequestBody @Valid SocialUserRequest request) throws UserAlreadyExistException {
        User user = socialLoginService.getKaKaoUserInfo(request.access_token);
        socialLoginService.signupKakao(user);

        return Response.success(201, "회원가입이 완료 되었습니다.", null);
    }

    @Data
    static class SocialUserRequest {
        @NotNull
        @Schema(description = "access_token", example = "", type = "String")
        private String access_token;
    }

}
