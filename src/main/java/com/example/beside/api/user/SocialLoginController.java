package com.example.beside.api.user;

import com.example.beside.common.Exception.ExceptionDetail.SocialLoginException;
import com.example.beside.common.Exception.ExceptionDetail.UserNotExistException;
import com.example.beside.common.config.Loggable;
import com.example.beside.common.response.Response;
import com.example.beside.common.response.ResponseDetail.LoginResponse;
import com.example.beside.domain.User;
import com.example.beside.dto.UserDto;
import com.example.beside.dto.UserTokenDto;
import com.example.beside.service.SocialLoginService;
import com.example.beside.util.JwtProvider;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import io.swagger.v3.oas.annotations.media.Content;

import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.web.bind.annotation.*;

@Loggable
@RequiredArgsConstructor
@RequestMapping("/api/social")
@RestController
public class SocialLoginController {

    private final SocialLoginService socialLoginService;
    private final JwtProvider jwtProvider;

    private final RedisTemplate<String, String> redisTemplate;

    @Value("${jwt.expTime}")
    private Long tokenValidTime;

    @Operation(tags = { "Social" }, summary = "카카오 로그인")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "정상 로그인 되었습니다.", content = @Content(schema = @Schema(implementation = LoginResponse.class))),
    })
    @PostMapping(value = "/v1/login/Kakao")
    public Response<UserTokenDto> kakaoLogin(@RequestBody @Valid SocialUserRequest request,
            HttpServletResponse response)
            throws UserNotExistException, SocialLoginException {
        User user = socialLoginService.getKaKaoUserInfo(request.access_token, request.fcm);

        // jwt 토큰발급
        String userToken = jwtProvider.createToken(user);
        ValueOperations<String, String> valueOperations = redisTemplate.opsForValue();
        valueOperations.set("jwt:" + user.getId(), userToken, tokenValidTime, TimeUnit.MILLISECONDS);

        UserTokenDto result = new UserTokenDto(userToken, new UserDto(user));
        response.addHeader("Authorization", "Bearer " + userToken);

        return LoginResponse.success(200, "정상 로그인 되었습니다.", result);
    }

    @Operation(tags = { "Social" }, summary = "카카오 로그아웃")
    @PostMapping("/v1/logout/kakao")
    public Response<Void> kakaoLogout(HttpServletRequest token) throws SocialLoginException {
        User user = (User) token.getAttribute("user");
        socialLoginService.logoutKakao(user);
        redisTemplate.delete("jwt:" + user.getId());

        return Response.success(200, "정상 로그아웃 되었습니다.", null);
    }

    @Operation(tags = { "Social" }, summary = "카카오 계정 제거")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "정상적으로 회원 탈퇴가 되었습니다.", content = @Content(schema = @Schema(implementation = LoginResponse.class))),
    })
    @DeleteMapping(value = "/v1/unlink/Kakao")
    public Response<Void> kakaoUnLink(HttpServletRequest token)
            throws SocialLoginException {
        User user = (User) token.getAttribute("user");
        socialLoginService.unLinkKakao(user);
        redisTemplate.delete("jwt:" + user.getId());

        return Response.success(201, "회원 탈퇴가 완료되었습니다.", null);
    }

    @Operation(tags = { "Social" }, summary = "애플 로그인")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "정상 로그인 되었습니다.", content = @Content(schema = @Schema(implementation = LoginResponse.class))),
    })
    @PostMapping(value = "/v1/login/Apple")
    public Response<UserTokenDto> appleLogin(@RequestBody @Valid SocialUserRequest request,
            HttpServletResponse response)
            throws Exception {
        User user = socialLoginService.appleLogin(request.access_token, request.fcm);

        // jwt 토큰발급
        String userToken = jwtProvider.createToken(user);
        ValueOperations<String, String> valueOperations = redisTemplate.opsForValue();
        valueOperations.set("jwt:" + user.getId(), userToken, tokenValidTime, TimeUnit.MILLISECONDS);

        UserTokenDto result = new UserTokenDto(userToken, new UserDto(user));
        response.addHeader("Authorization", "Bearer " + userToken);

        return LoginResponse.success(200, "정상 로그인 되었습니다.", result);
    }

    @Data
    static class SocialUserRequest {
        @NotNull
        @Schema(description = "access_token", example = "", type = "String")
        private String access_token;

        @Schema(description = "fcm", example = "esZVkMAAYUrzi9-40UVPDL:APA91bHQPgHFfbo-dZojoJk2-RT4_EVVfP0njfGhFHAZmYIXWbbjBE", type = "String")
        private String fcm;
    }

}
