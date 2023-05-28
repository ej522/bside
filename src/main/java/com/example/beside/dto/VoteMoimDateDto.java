package com.example.beside.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class VoteMoimDateDto {
    @NotEmpty
    @Schema(description = "모임 아이디", example = "1")
    private Long moim_id;

    @NotEmpty
    @Schema(description = "선택된 날짜", example = "2023-05-26")
    private LocalDateTime selected_date;

    @NotEmpty
    @Schema(description = "오전", example = "true")
    private Boolean morning;

    @NotEmpty
    @Schema(description = "오후", example = "true")
    private Boolean afternoon;

    @NotEmpty
    @Schema(description = "저녁", example = "true")
    private Boolean evening;

    @NotEmpty
    @Schema(description = "투표한 인원", example = "1")
    private Long vote_cnt;

    @Schema(description = "유저정보")
    private List<UserDto> user_info;
}
