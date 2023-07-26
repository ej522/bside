package com.example.beside.dto;

import java.time.LocalDateTime;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MoimDateDto {
    @NotEmpty
    @Schema(description = "오전", example = "true")
    private boolean morning;

    @NotEmpty
    @Schema(description = "오후", example = "true")
    private boolean afternoon;

    @NotEmpty
    @Schema(description = "저녁", example = "false")
    private boolean evening;

    @NotEmpty
    @Schema(description = "선택 일", example = "2023-12-20")
    private LocalDateTime selected_date;

    public MoimDateDto(MoimOveralDateDto moimOveralDto) {
        this.selected_date = moimOveralDto.getSelected_date();
        this.morning = moimOveralDto.getMorning();
        this.afternoon = moimOveralDto.getAfternoon();
        this.evening = moimOveralDto.getEvening();
    }
}
