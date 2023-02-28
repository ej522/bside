package com.example.beside.domain;


import com.example.beside.util.PasswordConverter;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter @Setter
@Table(name="user", schema = "bside" )
public class User {

    @Id
    @GeneratedValue
    @Column(name = "user_id")
    private Long id;

    @Column(nullable = false)
    //@Convert(converter= PasswordConverter.class)
    private String password;

    private String email;
}
