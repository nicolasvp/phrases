package com.microservice.phrases.models.services.remote;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@FeignClient(name="USERS-SERVICE") // Service name registered on Eureka Server
public interface IUserRemoteCallService {
	
	@RequestMapping(method=RequestMethod.GET, value="/api/service-route")
	public String getServiceRoute();
	
}
