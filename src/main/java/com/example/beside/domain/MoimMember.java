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
@Table(name = "moim_member", schema = "bside")
public class MoimMember {

    @Id
    @GeneratedValue
    @Column(name = "moim_member_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "moim_id")
    private Moim moim;

    @OneToMany(mappedBy = "moim_member", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<MoimMemberTime> moim_member_time = new ArrayList<>();

    private Long user_id;

    private String user_name;

    private LocalDateTime join_time;

    @ColumnDefault("true")
    private Boolean history_view_yn;

    @ColumnDefault("false")
    private Boolean is_accept;

}
