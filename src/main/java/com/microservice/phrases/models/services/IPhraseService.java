package com.microservice.phrases.models.services;

import java.util.List;

import com.microservices.commons.models.entity.phrases.Phrase;

public interface IPhraseService {

	public List<Phrase> findAll();
	
	public Phrase findById(Long id);
	
	public Phrase save(Phrase phrase);
	
	public void delete(Long id);
	
	public String callUserService();
	
	public String unavailableMessage();
}
