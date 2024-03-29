package com.example.beside.common.response;

import com.fasterxml.jackson.annotation.JsonInclude;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

@Schema(name = "Response", description = "공통 Response")
@Getter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Response<T> {

    @Schema(description = "상태 코드", nullable = false)
    private int httpStatusCode;

    @Schema(description = "메시지", nullable = false)
    private String msg;

    @Schema(description = "데이터", nullable = true)
    private T data;

    @Builder
    public Response(int httpStatusCode, String msg, T data) {
        this.httpStatusCode = httpStatusCode;
        this.msg = msg;
        this.data = data;
    }

    public static <T> Response<T> success(int httpStatusCode, String msg, T data) {
        return new Response<T>(httpStatusCode, msg, data);
    }

    public static <T> Response<T> fail(int httpStatusCode, String errorMessage) {
        return new Response<T>(httpStatusCode, errorMessage, null);
    }

    public static <T> Response<T> fail(int httpStatusCode, String errorMessage, T data) {
        return new Response<>(httpStatusCode, errorMessage, data);
    }
}
