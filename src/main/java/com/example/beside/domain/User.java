package com.example.beside.domain;

import java.util.ArrayList;
import java.util.List;

import com.example.beside.util.PasswordConverter;
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

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<Moim> moim = new ArrayList();

    @OneToOne(mappedBy = "user", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private Friend friend;

    @Column(length = 10)
    private String social_type;

    @Column(length = 10)
    private String name;

    @Column(length = 30)
    private String email;

    @Convert(converter = PasswordConverter.class)
    private String password;

    private String profile_image;
}
