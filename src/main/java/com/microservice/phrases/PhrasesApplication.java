package com.microservice.phrases;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.cloud.client.circuitbreaker.EnableCircuitBreaker;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;

@EnableEurekaClient
@EnableCircuitBreaker
@EnableFeignClients
@EntityScan({"com.microservices.commons.models.entity.phrases"})
@ComponentScan({"com.microservices.commons.models.services", "com.microservice.phrases"})
@SpringBootApplication
public class PhrasesApplication {
	
	public static void main(String[] args) {
		SpringApplication.run(PhrasesApplication.class, args);
	}
}
