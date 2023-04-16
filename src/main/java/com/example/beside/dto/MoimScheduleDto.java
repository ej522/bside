package com.example.beside.dto;

import java.time.LocalDateTime;

import jakarta.validation.constraints.NotEmpty;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class MoimScheduleDto {
    @NotEmpty
    private LocalDateTime selected_date;
    @NotEmpty
    private Boolean am_nine;
    @NotEmpty
    private Boolean am_ten;
    @NotEmpty
    private Boolean am_eleven;
    @NotEmpty
    private Boolean noon;

    @NotEmpty
    private Boolean pm_four;
    @NotEmpty
    private Boolean pm_five;
    @NotEmpty
    private Boolean pm_six;
    @NotEmpty
    private Boolean pm_seven;
    @NotEmpty
    private Boolean pm_eight;
    @NotEmpty
    private Boolean pm_nine;

    public MoimScheduleDto(MoimOveralScheduleDto moimOveralScheduleDto) {
        this.selected_date = moimOveralScheduleDto.getSelected_date();
        this.am_nine = moimOveralScheduleDto.getAm_nine();
        this.am_ten = moimOveralScheduleDto.getAm_ten();
        this.am_eleven = moimOveralScheduleDto.getAm_eleven();
        this.noon = moimOveralScheduleDto.getNoon();

        this.pm_four = moimOveralScheduleDto.getPm_four();
        this.pm_five = moimOveralScheduleDto.getPm_five();
        this.pm_six = moimOveralScheduleDto.getPm_six();
        this.pm_seven = moimOveralScheduleDto.getPm_seven();
        this.pm_eight = moimOveralScheduleDto.getPm_eight();
        this.pm_nine = moimOveralScheduleDto.getPm_nine();
    }
}
