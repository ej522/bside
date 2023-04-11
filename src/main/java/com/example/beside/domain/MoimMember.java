package com.example.beside.domain;

import java.time.LocalDateTime;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
// @NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "moim_member", schema = "bside")
public class MoimMember {

    @Id
    @GeneratedValue
    @Column(name = "moim_member_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "moim_id")
    private Moim moim;

    private String member_id;

    @Column(length = 20)
    private String member_name;

    private LocalDateTime selected_date;

}
