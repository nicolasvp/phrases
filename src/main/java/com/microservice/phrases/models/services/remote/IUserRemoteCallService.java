package com.microservice.phrases.models.services.remote;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

@FeignClient(name="USERS-SERVICE") // Service name registered on Eureka Server
public interface IUserRemoteCallService {
	
	@GetMapping("/api/service-route")
	public String getServiceRoute();
	
}
