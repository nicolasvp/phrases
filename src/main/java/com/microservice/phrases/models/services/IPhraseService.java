package com.microservice.phrases.models.services;

import java.util.List;

import com.microservice.phrases.models.entity.Phrase;

public interface IPhraseService {

	public List<Phrase> findAll();
	
	public Phrase findById(Long id);
	
	public Phrase save(Phrase phrase);
	
	public void delete(Long id);
}
