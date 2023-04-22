package com.example.beside;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
@EnableJpaAuditing
public class BesideApplication {
	// http://localhost:8080/swagger-ui/index.html

	public static void main(String[] args) {
		SpringApplication.run(BesideApplication.class, args);
	}

}
