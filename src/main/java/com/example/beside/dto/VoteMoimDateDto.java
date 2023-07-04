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
public class VoteMoimDateDto {

    @NotEmpty
    @Schema(description = "모임 ID", example = "1")
    private Long moim_id;

    @NotEmpty
    @Schema(description = "총 투표수", example = "5")
    private Integer total;

    @NotEmpty
    @Schema(description = "나의 투표여부", example = "true")
    private Boolean myVote_yn;

    @Schema
    private List<DateVoteInfo> voteList;

    @Data
    public static class DateVoteInfo {
        @NotEmpty
        @Schema(description = "선택한 날짜", example = "2023-01-01")
        private LocalDateTime selected_date;

        @NotEmpty
        @Schema(description = "투표 수", example = "1")
        private Integer vote_cnt;

        @Schema(description = "해당 날짜에 투표한 유저 정보")
        private List<DateUserInfo> userInfoList;

    }

    @Data
    public static class DateUserInfo {
        @NotEmpty
        @Schema(description = "유저ID", example = "1")
        private Long user_id;

        @NotEmpty
        @Schema(description = "유저 닉네임", example = "닉네임")
        private String nickname;

        @Schema(description = "유저 프로필", example = "https://moim.life/profile/green.jpg")
        private String profile;
    }

}
