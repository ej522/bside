package com.example.beside.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Table(name = "alarm", schema = "bside")
public class Alarm {

    @Id
    @GeneratedValue
    @Column(name = "alarm_id")
    private long id;

    private long moim_id;

    private String moim_name;

    private long receive_id;

    private String receive_name;

    private long send_id;

    private String send_name;

    private String type;

    private LocalDateTime alarm_time;

    private String status;

    private String error_msg;

}
