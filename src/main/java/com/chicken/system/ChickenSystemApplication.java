package com.chicken.system;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class ChickenSystemApplication {

	public static void main(String[] args) {
		SpringApplication.run(ChickenSystemApplication.class, args);
	}

}
