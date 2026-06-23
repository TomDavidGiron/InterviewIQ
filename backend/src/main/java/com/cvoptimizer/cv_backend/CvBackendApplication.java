package com.cvoptimizer.cv_backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class CvBackendApplication {

	public static void main(String[] args) {
		SpringApplication.run(CvBackendApplication.class, args);
	}
}
