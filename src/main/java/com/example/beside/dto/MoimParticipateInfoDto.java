package com.example.beside.dto;

import java.util.List;
import java.util.stream.Collectors;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class MoimParticipateInfoDto {
    @NotEmpty
    @Schema(description = "모임 주최자", example = "부엉이")
    private String moim_leader;

    @NotEmpty
    @Schema(description = "모임 주최자 id", example = "123")
    private Long moim_leader_id;

    @NotEmpty
    @Schema(description = "모임명", example = "작당모의")
    private String moim_name;

    @NotEmpty
    @Schema(description = "데드라인 시간", example = "5")
    private int dead_line_hour;

    @NotEmpty
    @Schema(description = "모임 가능 날짜")
    private List<MoimDateDto> dateList;

    public MoimParticipateInfoDto(List<MoimOveralDateDto> moimOveralInfo) {
        this.moim_leader = moimOveralInfo.get(0).getUser_name();
        this.moim_leader_id = moimOveralInfo.get(0).getUser_id();
        this.moim_name = moimOveralInfo.get(0).getMoim_name();
        this.dead_line_hour = moimOveralInfo.get(0).getDead_line_hour();
        this.dateList = moimOveralInfo.stream().map(MoimDateDto::new).collect(Collectors.toList());
    }
}
