package com.example.beside.common.response;

import java.util.List;

import com.example.beside.dto.FriendDto;
import com.fasterxml.jackson.annotation.JsonInclude;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

@Schema(name = "MyfriendList", description = "친구 목록 조회")
@Getter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class MyFriendResponse {
    @Schema(description = "상태 코드", nullable = false)
    private int httpStatusCode;

    @Schema(description = "메시지", nullable = false)
    private String msg;

    @Schema(description = "데이터", nullable = true)
    private List<FriendDto> data;

    @Builder
    public MyFriendResponse(int httpStatusCode, String msg, List<FriendDto> data) {
        this.httpStatusCode = httpStatusCode;
        this.msg = msg;
        this.data = data;
    }

    public static MyFriendResponse success(int httpStatusCode, String msg, List<FriendDto> data) {
        return new MyFriendResponse(httpStatusCode, msg, data);
    }

    public static MyFriendResponse fail(int httpStatusCode, String errorMessage) {
        return new MyFriendResponse(httpStatusCode, errorMessage, null);
    }

    public static MyFriendResponse fail(int httpStatusCode, String errorMessage, List<FriendDto> data) {
        return new MyFriendResponse(httpStatusCode, errorMessage, data);
    }
}
