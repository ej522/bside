package com.example.beside.dto;

import java.time.LocalDateTime;

import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MoimOveralScheduleDto {
    @NotEmpty
    private Long moim_id;
    @NotEmpty
    private String moim_name;
    @NotEmpty
    private Long user_id;
    @NotEmpty
    private String user_name;
    @NotEmpty
    private int dead_line_hour;
    @NotEmpty
    private LocalDateTime created_time;

    @NotEmpty
    private String member_name;
    @NotEmpty
    private String profile_image;
    @NotEmpty
    private long moim_member_id;
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
    private Boolean pm_one;
    @NotEmpty
    private Boolean pm_two;
    @NotEmpty
    private Boolean pm_three;
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

    public MoimOveralScheduleDto(Long moim_id, int dead_line_hour, LocalDateTime created_time, Long user_id, String user_name,
            String moim_name,
            String member_name, String profile_image, LocalDateTime selected_date, Boolean am_nine, Boolean am_ten, Boolean am_eleven,
            Boolean noon, Boolean pm_one, Boolean pm_two, Boolean pm_three, Boolean pm_four, Boolean pm_five,
            Boolean pm_six, Boolean pm_seven, Boolean pm_eight,
            Boolean pm_nine) {
        this.moim_id = moim_id;
        this.dead_line_hour = dead_line_hour;
        this.created_time = created_time;
        this.user_id = user_id;
        this.user_name = user_name;
        this.moim_name = moim_name;
        this.member_name = member_name;
        this.profile_image = profile_image;
        this.selected_date = selected_date;
        this.am_nine = am_nine;
        this.am_ten = am_ten;
        this.am_eleven = am_eleven;
        this.noon = noon;
        this.pm_one = pm_one;
        this.pm_two = pm_two;
        this.pm_three = pm_three;
        this.pm_four = pm_four;
        this.pm_five = pm_five;
        this.pm_six = pm_six;
        this.pm_seven = pm_seven;
        this.pm_eight = pm_eight;
        this.pm_nine = pm_nine;
    }
}
