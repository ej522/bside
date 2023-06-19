package com.example.beside.dto;

import java.time.LocalDateTime;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class InvitedMoimListDto {
    @NotEmpty
    @Schema(description = "모임 id", example = "wqddwdkozxkcoxz=")
    private String encryptedInfo;

    @NotEmpty
    @Schema(description = "모임 명", example = "아니이게 모임")
    private String moim_name;

    @NotEmpty
    @Schema(description = "모임 주최자", example = "부엉이")
    private String moim_leader;

    @NotEmpty
    @Schema(description = "생성일", example = "YYYY-MM-DD HH24:mi:ss")
    private LocalDateTime createdTime;

    @NotEmpty
    @Schema(description = "데드라인 시간", example = "5")
    private int dead_line_hour;

    @Schema(description = "마감기한", example = "YYYY-MM-DD HH24:mi:ss")
    private LocalDateTime deadLineTime;

    public InvitedMoimListDto(String encryptedInfo, String moim_name, String moim_leader, LocalDateTime createdTime,
            int dead_line_hour) {
        this.encryptedInfo = encryptedInfo;
        this.moim_name = moim_name;
        this.moim_leader = moim_leader;
        this.createdTime = createdTime;
        this.dead_line_hour = dead_line_hour;
        this.deadLineTime = createdTime.plusHours(dead_line_hour);
    }
}
