package com.example.beside.domain;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "email_validate", schema = "bside")
public class EmailValidate {

    @Id
    @GeneratedValue
    @Column(name = "email_validate_id")
    private long id;

    private String email;

    private String validate_code;

    private LocalDateTime create_time;

}
