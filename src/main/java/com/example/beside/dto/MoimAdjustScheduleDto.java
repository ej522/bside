package com.example.beside.dto;

import java.util.List;
import java.util.stream.Collectors;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;

public class MoimAdjustScheduleDto {
    @NotEmpty
    @Schema(description = "모임 주최자", example = "부엉이")
    private String moim_leader;

    @NotEmpty
    @Schema(description = "모임명", example = "작당모의")
    private String moim_name;

    @NotEmpty
    @Schema(description = "데드라인 시간", example = "5")
    private int dead_line_hour;

    @NotEmpty
    @Schema(description = "모임 가능 시간")
    private List<MoimScheduleDto> dateList;

    public MoimAdjustScheduleDto(List<MoimOveralScheduleDto> moimScheduleInfo) {
        this.moim_leader = moimScheduleInfo.get(0).getUser_name();
        this.moim_name = moimScheduleInfo.get(0).getMoim_name();
        this.dead_line_hour = moimScheduleInfo.get(0).getDead_line_hour();
        this.dateList = moimScheduleInfo.stream().map(MoimScheduleDto::new)
                .collect(Collectors.toList());
    }
}
