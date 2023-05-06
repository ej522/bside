package com.example.beside.common.response;

import com.example.beside.dto.MoimParticipateInfoDto;
import com.fasterxml.jackson.annotation.JsonInclude;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

@Schema(name = "ParticipateMoim", description = "모임 참여하기")
@Getter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class MoimParticipateResponse {
    @Schema(description = "상태 코드", nullable = false)
    private int httpStatusCode;

    @Schema(description = "메시지", nullable = false)
    private String msg;

    @Schema(description = "데이터", nullable = true)
    private MoimParticipateInfoDto data;

    @Builder
    public MoimParticipateResponse(int httpStatusCode, String msg, MoimParticipateInfoDto data) {
        this.httpStatusCode = httpStatusCode;
        this.msg = msg;
        this.data = data;
    }

    public static MoimParticipateResponse success(int httpStatusCode, String msg, MoimParticipateInfoDto data) {
        return new MoimParticipateResponse(httpStatusCode, msg, data);
    }

    public static MoimParticipateResponse fail(int httpStatusCode, String errorMessage) {
        return new MoimParticipateResponse(httpStatusCode, errorMessage, null);
    }

    public static MoimParticipateResponse fail(int httpStatusCode, String errorMessage,
            MoimParticipateInfoDto data) {
        return new MoimParticipateResponse(httpStatusCode, errorMessage, data);
    }
}
