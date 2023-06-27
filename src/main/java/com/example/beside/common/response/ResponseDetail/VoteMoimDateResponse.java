package com.example.beside.common.response.ResponseDetail;

import com.example.beside.dto.VoteMoimDateDto;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Schema(name = "voteMoimDateList", description = "모임 투표 날짜 결과")
@Getter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class VoteMoimDateResponse {
    @Schema(description = "상태 코드", nullable = false)
    private int httpStatusCode;

    @Schema(description = "메시지", nullable = false)
    private String msg;

    @Schema(description = "데이터", nullable = true)
    private List<VoteMoimDateDto> data;

    @Builder
    public VoteMoimDateResponse(int httpStatusCode, String msg, List<VoteMoimDateDto> data) {
        this.httpStatusCode = httpStatusCode;
        this.msg = msg;
        this.data = data;
    }

    public static VoteMoimDateResponse success(int httpStatusCode, String msg, List<VoteMoimDateDto> data) {
        return new VoteMoimDateResponse(httpStatusCode, msg, data);
    }

    public static VoteMoimDateResponse fail(int httpStatusCode, String errorMessage) {
        return new VoteMoimDateResponse(httpStatusCode, errorMessage, null);
    }

    public static VoteMoimDateResponse fail(int httpStatusCode, String errorMessage, List<VoteMoimDateDto> data) {
        return new VoteMoimDateResponse(httpStatusCode, errorMessage, data);
    }
}
