package com.example.beside.domain;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;

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

    @OneToMany(mappedBy = "moim", cascade = CascadeType.ALL)
    private List<MoimMember> moim_member = new ArrayList<>();

    @Column(length = 20)
    private String moim_name;

    @Column(length = 15)
    private String fixed_date;

    @Column(length = 15)
    private String fixed_time;

    private int dead_line_hour;

    private LocalDateTime created_time;

    private String encrypted_id;

    private Boolean nobody_schedule_selected;

    @ColumnDefault("true")
    private Boolean history_view_yn;

}
