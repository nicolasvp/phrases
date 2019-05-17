package com.microservice.phrases;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@EnableAutoConfiguration //defines this as a Spring Boot application
@EnableDiscoveryClient 
@SpringBootApplication
public class PhrasesApplication {

	public static void main(String[] args) {
		SpringApplication.run(PhrasesApplication.class, args);
	}

}
