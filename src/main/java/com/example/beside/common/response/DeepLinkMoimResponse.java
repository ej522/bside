package com.example.beside.common.response;

import com.example.beside.dto.MoimDto;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

@Schema(name = "MoinInfo", description = "모임 조회")
@Getter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class DeepLinkMoimResponse {
    @Schema(description = "상태 코드", nullable = false)
    private int httpStatusCode;

    @Schema(description = "메시지", nullable = false)
    private String msg;

    @Schema(description = "데이터", nullable = true)
    private MoimDto data;

    @Builder
    public DeepLinkMoimResponse(int httpStatusCode, String msg, MoimDto data) {
        this.httpStatusCode = httpStatusCode;
        this.msg = msg;
        this.data = data;
    }

    public static DeepLinkMoimResponse success(int httpStatusCode, String msg, MoimDto data) {
        return new DeepLinkMoimResponse(httpStatusCode, msg, data);
    }

    public static DeepLinkMoimResponse fail(int httpStatusCode, String errorMessage) {
        return new DeepLinkMoimResponse(httpStatusCode, errorMessage, null);
    }

    public static DeepLinkMoimResponse fail(int httpStatusCode, String errorMessage, MoimDto data) {
        return new DeepLinkMoimResponse(httpStatusCode, errorMessage, data);
    }
}
