package com.example.beside.common.response;

import java.util.List;

import com.example.beside.dto.MyMoimDto;
import com.fasterxml.jackson.annotation.JsonInclude;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

@Schema(name = "MyMoimList", description = "확정 모임 조회")
@Getter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class MyMoimListResponse {
    @Schema(description = "상태 코드", nullable = false)
    private int httpStatusCode;

    @Schema(description = "메시지", nullable = false)
    private String msg;

    @Schema(description = "데이터", nullable = true)
    private List<MyMoimDto> data;

    @Builder
    public MyMoimListResponse(int httpStatusCode, String msg, List<MyMoimDto> data) {
        this.httpStatusCode = httpStatusCode;
        this.msg = msg;
        this.data = data;
    }

    public static MyMoimListResponse success(int httpStatusCode, String msg, List<MyMoimDto> data) {
        return new MyMoimListResponse(httpStatusCode, msg, data);
    }

    public static MyMoimListResponse fail(int httpStatusCode, String errorMessage) {
        return new MyMoimListResponse(httpStatusCode, errorMessage, null);
    }

    public static MyMoimListResponse fail(int httpStatusCode, String errorMessage, List<MyMoimDto> data) {
        return new MyMoimListResponse(httpStatusCode, errorMessage, data);
    }
}
