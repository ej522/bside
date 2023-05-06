package com.example.beside.dto;

import java.time.LocalDateTime;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class MoimScheduleDto {
    @NotEmpty
    @Schema(description = "선택 일", example = "2023-12-24")
    private LocalDateTime selected_date;

    @NotEmpty
    @Schema(description = "모임 멤버", example = "거북이")
    private String member_name;

    @NotEmpty
    @Schema(description = "오전 9시", example = "true")
    private Boolean am_nine;

    @NotEmpty
    @Schema(description = "오전 10시", example = "true")
    private Boolean am_ten;

    @NotEmpty
    @Schema(description = "오전 11시", example = "true")
    private Boolean am_eleven;

    @NotEmpty
    @Schema(description = "정오", example = "true")
    private Boolean noon;

    @NotEmpty
    @Schema(description = "오후 1시", example = "true")
    private Boolean pm_one;

    @NotEmpty
    @Schema(description = "오후 2시", example = "true")
    private Boolean pm_two;

    @NotEmpty
    @Schema(description = "오후 3시", example = "true")
    private Boolean pm_three;

    @NotEmpty
    @Schema(description = "오후 4시", example = "true")
    private Boolean pm_four;

    @NotEmpty
    @Schema(description = "오후 5시", example = "true")
    private Boolean pm_five;

    @NotEmpty
    @Schema(description = "오후 6시", example = "true")
    private Boolean pm_six;

    @NotEmpty
    @Schema(description = "오후 7시", example = "true")
    private Boolean pm_seven;

    @NotEmpty
    @Schema(description = "오후 8시", example = "true")
    private Boolean pm_eight;

    @NotEmpty
    @Schema(description = "오후 9시", example = "true")
    private Boolean pm_nine;

    public MoimScheduleDto(MoimOveralScheduleDto moimOveralScheduleDto) {
        this.selected_date = moimOveralScheduleDto.getSelected_date();
        this.member_name = moimOveralScheduleDto.getMember_name();
        this.am_nine = moimOveralScheduleDto.getAm_nine();
        this.am_ten = moimOveralScheduleDto.getAm_ten();
        this.am_eleven = moimOveralScheduleDto.getAm_eleven();
        this.noon = moimOveralScheduleDto.getNoon();

        this.pm_one = moimOveralScheduleDto.getPm_one();
        this.pm_two = moimOveralScheduleDto.getPm_two();
        this.pm_three = moimOveralScheduleDto.getPm_three();
        this.pm_four = moimOveralScheduleDto.getPm_four();
        this.pm_five = moimOveralScheduleDto.getPm_five();
        this.pm_six = moimOveralScheduleDto.getPm_six();
        this.pm_seven = moimOveralScheduleDto.getPm_seven();
        this.pm_eight = moimOveralScheduleDto.getPm_eight();
        this.pm_nine = moimOveralScheduleDto.getPm_nine();
    }
}
