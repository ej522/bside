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
    @Schema(description = "모임 아이디", example = "1")
    private Long moim_id;

    @NotEmpty
    @Schema(description = "모임 날짜", example = "2023-03-03")
    private LocalDateTime selected_date;

    @NotEmpty
    @Schema(description = "투표 시간 정보")
    private List<VoteMoimTimeDetailDto> time_info;

}
