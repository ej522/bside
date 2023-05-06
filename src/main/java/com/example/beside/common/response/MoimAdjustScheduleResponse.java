package com.example.beside.common.response;

import com.example.beside.dto.MoimAdjustScheduleDto;
import com.fasterxml.jackson.annotation.JsonInclude;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

@Schema(name = "AdjustSchedule", description = "모임 일정 조정")
@Getter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class MoimAdjustScheduleResponse {
    @Schema(description = "상태 코드", nullable = false)
    private int httpStatusCode;

    @Schema(description = "메시지", nullable = false)
    private String msg;

    @Schema(description = "데이터", nullable = true)
    private MoimAdjustScheduleDto data;

    @Builder
    public MoimAdjustScheduleResponse(int httpStatusCode, String msg, MoimAdjustScheduleDto data) {
        this.httpStatusCode = httpStatusCode;
        this.msg = msg;
        this.data = data;
    }

    public static MoimAdjustScheduleResponse success(int httpStatusCode, String msg, MoimAdjustScheduleDto data) {
        return new MoimAdjustScheduleResponse(httpStatusCode, msg, data);
    }

    public static MoimAdjustScheduleResponse fail(int httpStatusCode, String errorMessage) {
        return new MoimAdjustScheduleResponse(httpStatusCode, errorMessage, null);
    }

    public static MoimAdjustScheduleResponse fail(int httpStatusCode, String errorMessage, MoimAdjustScheduleDto data) {
        return new MoimAdjustScheduleResponse(httpStatusCode, errorMessage, data);
    }
}
