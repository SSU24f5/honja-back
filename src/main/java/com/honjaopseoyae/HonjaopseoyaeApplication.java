package com.honjaopseoyae;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@EnableJpaAuditing
@SpringBootApplication
public class HonjaopseoyaeApplication {

	public static void main(String[] args) {
		SpringApplication.run(HonjaopseoyaeApplication.class, args);
	}

}
