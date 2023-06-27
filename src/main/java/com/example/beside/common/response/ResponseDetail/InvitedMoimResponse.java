package com.example.beside.common.response.ResponseDetail;

import java.util.List;

import com.example.beside.dto.InvitedMoimListDto;
import com.fasterxml.jackson.annotation.JsonInclude;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

@Schema(name = "InvitedMoimResponse", description = "초대받은 모임 목록 조회")
@Getter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class InvitedMoimResponse {
    @Schema(description = "상태 코드", nullable = false)
    private int httpStatusCode;

    @Schema(description = "메시지", nullable = false)
    private String msg;

    @Schema(description = "데이터", nullable = true)
    private List<InvitedMoimListDto> data;

    @Builder
    public InvitedMoimResponse(int httpStatusCode, String msg, List<InvitedMoimListDto> data) {
        this.httpStatusCode = httpStatusCode;
        this.msg = msg;
        this.data = data;
    }

    public static InvitedMoimResponse success(int httpStatusCode, String msg, List<InvitedMoimListDto> data) {
        return new InvitedMoimResponse(httpStatusCode, msg, data);
    }

    public static InvitedMoimResponse fail(int httpStatusCode, String errorMessage) {
        return new InvitedMoimResponse(httpStatusCode, errorMessage, null);
    }

    public static InvitedMoimResponse fail(int httpStatusCode, String errorMessage, List<InvitedMoimListDto> data) {
        return new InvitedMoimResponse(httpStatusCode, errorMessage, data);
    }
}
