package com.example.beside.dto;

import java.time.LocalDateTime;

import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MoimDateDto {
    @NotEmpty
    private boolean morning;

    @NotEmpty
    private boolean afternoon;

    @NotEmpty
    private boolean evening;

    @NotEmpty
    private LocalDateTime selected_date;

    public MoimDateDto(MoimOveralDateDto moimOveralDto) {
        this.selected_date = moimOveralDto.getSelected_date();
        this.morning = moimOveralDto.getMorning();
        this.afternoon = moimOveralDto.getAfternoon();
        this.evening = moimOveralDto.getEvening();
    }
}
