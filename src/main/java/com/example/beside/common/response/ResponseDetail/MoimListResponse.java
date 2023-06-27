package com.example.beside.common.response.ResponseDetail;

import java.util.List;

import com.example.beside.dto.MoimDto;
import com.fasterxml.jackson.annotation.JsonInclude;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

@Schema(name = "MoimList", description = "모임 여러개 조회")
@Getter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class MoimListResponse {
    @Schema(description = "상태 코드", nullable = false)
    private int httpStatusCode;

    @Schema(description = "메시지", nullable = false)
    private String msg;

    @Schema(description = "데이터", nullable = true)
    private List<MoimDto> data;

    @Builder
    public MoimListResponse(int httpStatusCode, String msg, List<MoimDto> data) {
        this.httpStatusCode = httpStatusCode;
        this.msg = msg;
        this.data = data;
    }

    public static MoimListResponse success(int httpStatusCode, String msg, List<MoimDto> data) {
        return new MoimListResponse(httpStatusCode, msg, data);
    }

    public static MoimListResponse fail(int httpStatusCode, String errorMessage) {
        return new MoimListResponse(httpStatusCode, errorMessage, null);
    }

    public static MoimListResponse fail(int httpStatusCode, String errorMessage, List<MoimDto> data) {
        return new MoimListResponse(httpStatusCode, errorMessage, data);
    }
}
