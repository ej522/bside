package com.example.beside.domain;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.*;

@Entity
@Getter
@Setter
// @NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "moim_member", schema = "bside")
public class MoimMember {

    @Id
    @GeneratedValue
    @Column(name = "mm_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "moim_id")
    private Moim moims;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @Column(length = 20)
    private String user_name;

    private LocalDateTime selected_date;

}
