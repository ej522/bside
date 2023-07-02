package com.example.beside.dto;

import java.time.LocalDateTime;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.PostLoad;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class VotingMoimDto {
    @NotEmpty
    @Schema(description = "모임장 ID", example = "123")
    private Long user_id;

    @NotEmpty
    @Schema(description = "모임장 이름", example = "부엉이")
    private String user_name;

    @NotEmpty
    @Schema(description = "모임 ID", example = "1")
    private Long moim_id;

    @NotEmpty
    @Schema(description = "모임 명", example = "작당모의")
    private String moim_name;

    @NotEmpty
    @Schema(description = "생성일", example = "2023-12-12")
    private LocalDateTime created_time;

    @NotEmpty
    @Schema(description = "데드 라인 시간", example = "2023-12-20")
    private int dead_line_hour;

    @Schema(description = "모임 확정 시간", example = "2023-12-20")
    private LocalDateTime dead_line_time;

    public VotingMoimDto(VotingMoimDto newDto) {
        this.user_id = newDto.user_id;
        this.user_name = newDto.user_name;
        this.moim_id = newDto.moim_id;
        this.moim_name = newDto.moim_name;
        this.created_time = newDto.created_time;
        this.dead_line_hour = newDto.dead_line_hour;
        this.dead_line_time = calculateDeadLineTime(newDto.created_time, newDto.dead_line_hour);
    }

    @PostLoad
    private LocalDateTime calculateDeadLineTime(LocalDateTime createdTime, int deadLineHour) {
        LocalDateTime plusedTime = createdTime.plusHours(deadLineHour);
        return plusedTime;
    }
}
