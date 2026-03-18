package com.example.BidSmart;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class BidSmartApplication {

	public static void main(String[] args) {
		SpringApplication.run(BidSmartApplication.class, args);
	}

}
