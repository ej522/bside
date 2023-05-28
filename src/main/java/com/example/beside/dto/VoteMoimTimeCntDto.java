package com.example.beside.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.ColumnDefault;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class VoteMoimTimeCntDto {

    @NotEmpty
    @Schema(description = "선택 날짜", example = "2022-03-01")
    private LocalDateTime selected_date;

    private Long am_nine_cnt;
    private Long am_ten_cnt;
    private Long am_eleven_cnt;
    private Long noon_cnt;
    private Long pm_one_cnt;
    private Long pm_two_cnt;
    private Long pm_three_cnt;
    private Long pm_four_cnt;
    private Long pm_five_cnt;
    private Long pm_six_cnt;
    private Long pm_seven_cnt;
    private Long pm_eight_cnt;
    private Long pm_nine_cnt;


}
