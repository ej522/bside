package com.example.beside.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class VoteMoimTimeCntDto {

    @NotEmpty
    @Schema(description = "선택 날짜", example = "2022-03-01")
    private LocalDateTime selected_date;

    private int am_nine_cnt;
    private int am_ten_cnt;
    private int am_eleven_cnt;
    private int noon_cnt;
    private int pm_one_cnt;
    private int pm_two_cnt;
    private int pm_three_cnt;
    private int pm_four_cnt;
    private int pm_five_cnt;
    private int pm_six_cnt;
    private int pm_seven_cnt;
    private int pm_eight_cnt;
    private int pm_nine_cnt;

}
