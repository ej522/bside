package com.example.beside.common.response.ResponseDetail;

import com.example.beside.dto.MoimDetailDto;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

@Schema(name = "MyMoimList", description = "모임 상세조회")
@Getter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class MoimDetailListResponse {
    @Schema(description = "상태 코드", nullable = false)
    private int httpStatusCode;

    @Schema(description = "메시지", nullable = false)
    private String msg;

    @Schema(description = "데이터", nullable = true)
    private MoimDetailDto data;

    @Builder
    public MoimDetailListResponse(int httpStatusCode, String msg, MoimDetailDto data) {
        this.httpStatusCode = httpStatusCode;
        this.msg = msg;
        this.data = data;
    }

    public static MoimDetailListResponse success(int httpStatusCode, String msg, MoimDetailDto data) {
        return new MoimDetailListResponse(httpStatusCode, msg, data);
    }

    public static MoimDetailListResponse fail(int httpStatusCode, String errorMessage) {
        return new MoimDetailListResponse(httpStatusCode, errorMessage, null);
    }

    public static MoimDetailListResponse fail(int httpStatusCode, String errorMessage, MoimDetailDto data) {
        return new MoimDetailListResponse(httpStatusCode, errorMessage, data);
    }
}
