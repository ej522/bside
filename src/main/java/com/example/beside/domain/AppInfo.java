package com.example.beside.domain;

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
@Table(name = "app_info", schema = "bside")
public class AppInfo {

    @Id
    @GeneratedValue
    @Column(name = "app_info_id")
    private long id;

    private String version;

    private String detail;
}
