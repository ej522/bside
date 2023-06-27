package com.example.beside.common.response.ResponseDetail;

import java.util.List;

import com.example.beside.dto.UserDto;
import com.fasterxml.jackson.annotation.JsonInclude;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

@Schema(name = "AllUsers", description = "유저 전체 조회")
@Getter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AllUsersResponse {
    @Schema(description = "상태 코드", nullable = false)
    private int httpStatusCode;

    @Schema(description = "메시지", nullable = false)
    private String msg;

    @Schema(description = "데이터", nullable = true)
    private List<UserDto> data;

    @Builder
    public AllUsersResponse(int httpStatusCode, String msg, List<UserDto> data) {
        this.httpStatusCode = httpStatusCode;
        this.msg = msg;
        this.data = data;
    }

    public static AllUsersResponse success(int httpStatusCode, String msg, List<UserDto> data) {
        return new AllUsersResponse(httpStatusCode, msg, data);
    }

    public static AllUsersResponse fail(int httpStatusCode, String errorMessage) {
        return new AllUsersResponse(httpStatusCode, errorMessage, null);
    }

    public static AllUsersResponse fail(int httpStatusCode, String errorMessage, List<UserDto> data) {
        return new AllUsersResponse(httpStatusCode, errorMessage, data);
    }
}
