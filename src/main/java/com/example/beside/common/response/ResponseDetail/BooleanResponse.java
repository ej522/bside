package com.example.beside.common.response.ResponseDetail;

import com.fasterxml.jackson.annotation.JsonInclude;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

@Schema(name = "CheckVerificationCode", description = "이메일 인증 코드 확인")
@Getter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class BooleanResponse {
    @Schema(description = "상태 코드", nullable = false)
    private int httpStatusCode;

    @Schema(description = "메시지", nullable = false)
    private String msg;

    @Schema(description = "데이터", nullable = true)
    private Boolean data;

    @Builder
    public BooleanResponse(int httpStatusCode, String msg, Boolean data) {
        this.httpStatusCode = httpStatusCode;
        this.msg = msg;
        this.data = data;
    }

    public static BooleanResponse success(int httpStatusCode, String msg, Boolean data) {
        return new BooleanResponse(httpStatusCode, msg, data);
    }

    public static BooleanResponse fail(int httpStatusCode, String errorMessage) {
        return new BooleanResponse(httpStatusCode, errorMessage, null);
    }

    public static BooleanResponse fail(int httpStatusCode, String errorMessage, Boolean data) {
        return new BooleanResponse(httpStatusCode, errorMessage, data);
    }
}
