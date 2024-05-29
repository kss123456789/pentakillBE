package com.example.java21_test;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@EnableJpaAuditing
@SpringBootApplication
public class Java21TestApplication {

	public static void main(String[] args) {
		SpringApplication.run(Java21TestApplication.class, args);
	}

}
