package com.example.beside;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@EnableJpaAuditing
@SpringBootApplication
public class BesideApplication {

	public static void main(String[] args) {
		SpringApplication.run(BesideApplication.class, args);
	}

}
