package com.example.beside.dto;

import java.time.LocalDateTime;

import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MoimOveralDateDto {
    @NotEmpty
    private Long id;
    @NotEmpty
    private Long user_id;
    @NotEmpty
    private String user_name;
    @NotEmpty
    private String moim_name;
    @NotEmpty
    private int dead_line_hour;
    @NotEmpty
    private Boolean morning;
    @NotEmpty
    private Boolean afternoon;
    @NotEmpty
    private Boolean evening;
    private LocalDateTime selected_date;

    public MoimOveralDateDto(Long id, Long user_id, String user_name, String moim_name, Integer dead_line_hour,
            Boolean morning,
            Boolean afternoon,
            Boolean evening, LocalDateTime selected_date) {
        this.id = id;
        this.user_id = user_id;
        this.user_name = user_name;
        this.moim_name = moim_name;
        this.dead_line_hour = dead_line_hour;
        this.morning = morning;
        this.afternoon = afternoon;
        this.evening = evening;
        this.selected_date = selected_date;
    }
}
