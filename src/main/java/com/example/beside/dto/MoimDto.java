package com.example.beside.dto;

import com.example.beside.domain.Moim;
import com.example.beside.domain.User;

import com.example.beside.util.Common;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MoimDto {
    @NotEmpty
    @Schema(description = "모임 ID", example = "1")
    private Long moim_id;

    @NotEmpty
    @Schema(description = "모임 명", example = "작당모의")
    private String moim_name;

    @NotEmpty
    @Schema(description = "모임 확정 날짜", example = "2023-12-20")
    private String fixed_date;

    @NotEmpty
    @Schema(description = "모임 확정 시간", example = "14")
    private String fixed_time;

    @Schema(description = "모임 인원", example = "6")
    private Integer memeber_cnt;

    @NotEmpty
    @Schema(description = "모임 주최자 ID", example = "1")
    private Long host_id;

    @Schema(description = "주최자 닉네임", example = "닉네임")
    private String host_name;

    @NotEmpty
    @Schema(description = "모임 주최자 프로필 이미지", example = "https://moim.life/profile/green.jpg")
    private String host_profile_img;

    @Schema(description = "데드라인 시간", example = "1")
    private int dead_line_hour;

    @Schema(description = "생성일", example = "2023-12-12")
    private LocalDateTime created_time;

    @Schema(description = "마감기한", example = "2023-12-20")
    private LocalDateTime dead_line_time;

    public MoimDto(Moim moim, User user, int memeber_cnt) {
        this.moim_id = moim.getId();
        this.moim_name = moim.getMoim_name();
        this.host_profile_img = user.getProfile_image();
        this.fixed_date = moim.getFixed_date();
        this.fixed_time = moim.getFixed_time();
        this.memeber_cnt = memeber_cnt;
        this.host_id = moim.getUser().getId();
        this.host_name = user.getName();
        this.dead_line_hour = moim.getDead_line_hour();
        this.created_time = moim.getCreated_time();
        this.dead_line_time = Common.calculateDeadLineTime(created_time, dead_line_hour);
    }

}
