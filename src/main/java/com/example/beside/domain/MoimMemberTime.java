package com.example.beside.domain;

import java.time.LocalDateTime;

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

}
