package com.example.beside.common.response.ResponseDetail;

import com.example.beside.dto.UserTokenDto;
import com.fasterxml.jackson.annotation.JsonInclude;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

@Schema(name = "Login", description = "자체 계정 로그인")
@Getter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class LoginResponse {
    @Schema(description = "상태 코드", nullable = false)
    private int httpStatusCode;

    @Schema(description = "메시지", nullable = false)
    private String msg;

    @Schema(description = "데이터", nullable = true)
    private UserTokenDto data;

    @Builder
    public LoginResponse(int httpStatusCode, String msg, UserTokenDto data) {
        this.httpStatusCode = httpStatusCode;
        this.msg = msg;
        this.data = data;
    }

    public static LoginResponse success(int httpStatusCode, String msg, UserTokenDto data) {
        return new LoginResponse(httpStatusCode, msg, data);
    }

    public static LoginResponse fail(int httpStatusCode, String errorMessage) {
        return new LoginResponse(httpStatusCode, errorMessage, null);
    }

    public static LoginResponse fail(int httpStatusCode, String errorMessage, UserTokenDto data) {
        return new LoginResponse(httpStatusCode, errorMessage, data);
    }
}
