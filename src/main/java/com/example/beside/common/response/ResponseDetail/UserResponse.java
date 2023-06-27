package com.example.beside.common.response.ResponseDetail;

import com.example.beside.dto.UserDto;
import com.fasterxml.jackson.annotation.JsonInclude;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

@Schema(name = "UpdateNickname", description = "닉네임 변경")
@Getter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserResponse {
    @Schema(description = "상태 코드", nullable = false)
    private int httpStatusCode;

    @Schema(description = "메시지", nullable = false)
    private String msg;

    @Schema(description = "데이터", nullable = true)
    private UserDto data;

    @Builder
    public UserResponse(int httpStatusCode, String msg, UserDto data) {
        this.httpStatusCode = httpStatusCode;
        this.msg = msg;
        this.data = data;
    }

    public static UserResponse success(int httpStatusCode, String msg, UserDto data) {
        return new UserResponse(httpStatusCode, msg, data);
    }

    public static UserResponse fail(int httpStatusCode, String errorMessage) {
        return new UserResponse(httpStatusCode, errorMessage, null);
    }

    public static UserResponse fail(int httpStatusCode, String errorMessage, UserDto data) {
        return new UserResponse(httpStatusCode, errorMessage, data);
    }
}
