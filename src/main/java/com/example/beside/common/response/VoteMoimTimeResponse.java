package com.example.beside.common.response;

import com.example.beside.dto.VoteMoimTimeDto;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

@Schema(name = "voteMoimTimeList", description = "모임 투표 시간 결과")
@Getter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class VoteMoimTimeResponse {
    @Schema(description = "상태 코드", nullable = false)
    private int httpStatusCode;

    @Schema(description = "메시지", nullable = false)
    private String msg;

    @Schema(description = "데이터", nullable = true)
    private VoteMoimTimeDto data;

    @Builder
    public VoteMoimTimeResponse(int httpStatusCode, String msg, VoteMoimTimeDto data) {
        this.httpStatusCode = httpStatusCode;
        this.msg = msg;
        this.data = data;
    }

    public static VoteMoimTimeResponse success(int httpStatusCode, String msg, VoteMoimTimeDto data) {
        return new VoteMoimTimeResponse(httpStatusCode, msg, data);
    }

    public static VoteMoimTimeResponse fail(int httpStatusCode, String errorMessage) {
        return new VoteMoimTimeResponse(httpStatusCode, errorMessage, null);
    }

    public static VoteMoimTimeResponse fail(int httpStatusCode, String errorMessage, VoteMoimTimeDto data) {
        return new VoteMoimTimeResponse(httpStatusCode, errorMessage, data);
    }
}
