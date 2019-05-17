package com.microservice.phrases.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.microservice.phrases.models.entity.Phrase;
import com.microservice.phrases.models.services.IPhraseService;

@RestController
@RequestMapping("/api")
public class PhraseController {

	@Autowired
	private IPhraseService phraseService;
	
	@GetMapping("/phrases")
	public List<Phrase> index(){
		return phraseService.findAll();
	}
}
