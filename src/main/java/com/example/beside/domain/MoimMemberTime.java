package com.example.beside.domain;

import java.time.LocalDateTime;

import com.example.beside.dto.MoimTImeInfoDto;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class MoimMemberTime {
    @Id
    @GeneratedValue
    @Column(name = "moim_member_time_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "moim_member_id")
    private MoimMember moim_member;

    private Long moim_id;

    private LocalDateTime selected_date;

    private Boolean am_nine;
    private Boolean am_ten;
    private Boolean am_eleven;
    private Boolean noon;
    private Boolean pm_one;
    private Boolean pm_two;
    private Boolean pm_three;
    private Boolean pm_four;
    private Boolean pm_five;
    private Boolean pm_six;
    private Boolean pm_seven;
    private Boolean pm_eigth;
    private Boolean pm_nine;

    // 비즈니스 로직
    public void setSchedule(LocalDateTime selectedDate, MoimTImeInfoDto moimTimeInfo) {
        this.selected_date = selectedDate;
        this.am_nine = moimTimeInfo.isAmNine();
        this.am_ten = moimTimeInfo.isAmTen();
        this.am_eleven = moimTimeInfo.isAmEleven();
        this.noon = moimTimeInfo.isNoon();
        this.pm_one = moimTimeInfo.isPmOne();
        this.pm_two = moimTimeInfo.isPmTwo();
        this.pm_three = moimTimeInfo.isPmThree();
        this.pm_four = moimTimeInfo.isPmFour();
        this.pm_five = moimTimeInfo.isPmFive();
        this.pm_six = moimTimeInfo.isPmSix();
        this.pm_seven = moimTimeInfo.isPmSeven();
        this.pm_eigth = moimTimeInfo.isPmEight();
        this.pm_nine = moimTimeInfo.isPmNine();
    }

}
