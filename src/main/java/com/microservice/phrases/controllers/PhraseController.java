package com.microservice.phrases.controllers;

import java.util.List;
import java.util.logging.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import com.microservice.phrases.models.entity.Phrase;
import com.microservice.phrases.models.services.IPhraseService;
import com.microservice.phrases.models.services.remote.IUserRemoteCallService;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;

@RestController
@RequestMapping("/api")
public class PhraseController {

	protected Logger LOGGER = Logger.getLogger(PhraseController.class.getName());
	
	@Autowired
	private IPhraseService phraseService;
		
	@Autowired
	private IUserRemoteCallService loadBalancer;
	
	@GetMapping("/phrases")
	public List<Phrase> index(){
		return phraseService.findAll();
	}
	
	@GetMapping("/service-route")
	public String serviceRoute() {
		return "Hi from phrases service";
	}
	
	@HystrixCommand(fallbackMethod = "unavailableMessage")
	@GetMapping("/phrases/users")
	public String users(){
		LOGGER.info("Invoking users service from phrase service");
		String response = loadBalancer.getServiceRoute();
		return response;
	}
		
	public String unavailableMessage() {
		return "Users service is not available";
	}
}
