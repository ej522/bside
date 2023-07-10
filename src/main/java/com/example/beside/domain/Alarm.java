package com.example.beside.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
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

    private long user_id;

    private String user_name;

    private long friend_id;

    private String friend_name;

    private String type;

    private LocalDateTime alarm_time;

    private String status;

    private String error_msg;

}
