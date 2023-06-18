package com.example.beside.domain;

import java.time.LocalDateTime;
import java.util.List;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.*;

@Entity
@Getter
@Setter
@Table(name = "user", schema = "bside")
public class User {

    @Id
    @GeneratedValue
    @Column(name = "user_id")
    private Long id;

    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<Moim> moim;

    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<Friend> friend;

    @Column(length = 10)
    private String social_type;

    @Column(length = 10)
    private String name;

    @Column(length = 30)
    private String email;

    private String fcm;

    private String password;

    private String profile_image;

    private LocalDateTime created_time;

    private Boolean push_alarm;

    private Boolean marketing_alarm;
}
