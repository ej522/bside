package com.example.beside.domain;

import java.time.LocalDateTime;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "moim", schema = "bside")
public class Moim {

    @Id
    @GeneratedValue
    @Column(name = "moim_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @Column(length = 20)
    private String moim_name;

    private String[] selected_date;

    @Column(length = 15)
    private String fixed_date;

    @Column(length = 15)
    private String fixed_time;

    private int dead_line_hour;

    private LocalDateTime created_time;

}
