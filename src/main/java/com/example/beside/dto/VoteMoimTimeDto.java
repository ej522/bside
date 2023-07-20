package com.example.beside.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class VoteMoimTimeDto {
    @NotEmpty
    @Schema(description = "모임 ID", example = "1")
    private Long moim_id;

    @NotEmpty
    @Schema(description = "선택된 날짜", example = "2022-03-01")
    private LocalDateTime selected_date;

    @NotEmpty
    @Schema(description = "총 투표수", example = "1")
    private Integer total;

    @Schema(description = "오전 투표 정보")
    private List<TimeVoteInfo> morning;


    @Schema(description = "오후 투표 정보")
    private List<TimeVoteInfo> afternoon;

    @Schema(description = "저녁 투표 정보")
    private List<TimeVoteInfo> evening;

    @Data
    public static class TimeVoteInfo {
        @NotEmpty
        @Schema(description = "선택된 시간", example = "13")
        private Integer selected_time;

        @Schema(description = "시간 투표 수", example = "1")
        private Integer vote_cnt;

        @Schema(description = "유저정보")
        private List<SimpleUserDto> userInfo;
    }

}
