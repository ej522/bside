package com.example.beside.common.response;

import java.util.List;

import com.example.beside.dto.VotingMoimDto;
import com.fasterxml.jackson.annotation.JsonInclude;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

@Schema(name = "VotingMoimList", description = "투표중인 모임 조회")
@Getter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class VotingMoimResponse {
    @Schema(description = "상태 코드", nullable = false)
    private int httpStatusCode;

    @Schema(description = "메시지", nullable = false)
    private String msg;

    @Schema(description = "데이터", nullable = true)
    private List<VotingMoimDto> data;

    @Builder
    public VotingMoimResponse(int httpStatusCode, String msg, List<VotingMoimDto> data) {
        this.httpStatusCode = httpStatusCode;
        this.msg = msg;
        this.data = data;
    }

    public static VotingMoimResponse success(int httpStatusCode, String msg, List<VotingMoimDto> data) {
        return new VotingMoimResponse(httpStatusCode, msg, data);
    }

    public static VotingMoimResponse fail(int httpStatusCode, String errorMessage) {
        return new VotingMoimResponse(httpStatusCode, errorMessage, null);
    }

    public static VotingMoimResponse fail(int httpStatusCode, String errorMessage, List<VotingMoimDto> data) {
        return new VotingMoimResponse(httpStatusCode, errorMessage, data);
    }
}
