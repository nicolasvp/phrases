package com.microservice.phrases.controllers;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.validation.Valid;

import com.microservice.phrases.models.services.IUtilService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.microservice.phrases.config.MessagesTranslate;
import com.microservice.phrases.enums.DatabaseMessagesEnum;
import com.microservice.phrases.exceptions.DatabaseAccessException;
import com.microservice.phrases.exceptions.NullRecordException;
import com.microservice.phrases.models.entity.Phrase;
import com.microservice.phrases.models.services.IPhraseService;

@CrossOrigin(origins = { "http://localhost:4200" })
@RestController
@RequestMapping("/api")
public class PhraseController {

	protected Logger LOGGER = LoggerFactory.getLogger(PhraseController.class);
	
	@Autowired
	private IPhraseService phraseService;

	@Autowired
	private IUtilService utilService;

	@Autowired
	private MessagesTranslate messages;
	
	@GetMapping(path="/phrases", produces = MediaType.APPLICATION_JSON_VALUE)
	public List<Phrase> index(){
		return phraseService.findAll();
	}

	@GetMapping("/phrases/users")
	public String users(){
		return phraseService.callUserService();
	}
	
	@GetMapping(path="/phrases/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> show(@PathVariable Long id) throws NullRecordException, DatabaseAccessException {
		
		Phrase phrase = null;

		try {
			phrase = phraseService.findById(id);
		} catch (DataAccessException e) {
			throw new DatabaseAccessException(DatabaseMessagesEnum.ACCESS_DATABASE.getMessage(), e);
		}

		// return error if the record non exist
		if (phrase == null) {
			throw new NullRecordException();
		}

		return new ResponseEntity<Phrase>(phrase, HttpStatus.OK);
	}
	
	@PostMapping(path="/phrases", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> create(@Valid @RequestBody Phrase phrase, BindingResult result) throws DatabaseAccessException {
		
		Phrase newPhrase = null;
		Map<String, Object> response = new HashMap<>();

		// if validation fails, list all errors and return them
		if(result.hasErrors()) {
			response.put("errors", utilService.listErrors(result));
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.BAD_REQUEST);
		}
		
		try {
			newPhrase = phraseService.save(phrase);
		} catch (DataAccessException e) {
			throw new DatabaseAccessException(DatabaseMessagesEnum.STORE_RECORD.getMessage(), e);
		}

		response.put("msg", messages.getCreated());
		response.put("phrase", newPhrase);

		return new ResponseEntity<Map<String, Object>>(response, HttpStatus.CREATED);
	}
	
	@PutMapping(path="/phrases/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> update(@Valid @RequestBody Phrase phrase, BindingResult result, @PathVariable("id") Long id) throws NullRecordException, DatabaseAccessException {
		
		Phrase phraseFromDB = phraseService.findById(id);
		Phrase phraseUpdated = null;
		Map<String, Object> response = new HashMap<>();

		// if validation fails, list all errors and return them
		if(result.hasErrors()) {
			response.put("errors", utilService.listErrors(result));
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.BAD_REQUEST);
		}
		
		// return error if the record non exist
		if (phraseFromDB == null) {
			throw new NullRecordException();
		}

		try {
			phraseFromDB.setBody(phrase.getBody());
			phraseFromDB.setAuthor(phrase.getAuthor());
			phraseFromDB.setType(phrase.getType());
			phraseUpdated = phraseService.save(phraseFromDB);
		} catch (DataAccessException e) {
			throw new DatabaseAccessException(DatabaseMessagesEnum.UPDATE_RECORD.getMessage(), e);
		}

		response.put("msg", messages.getUpdated());
		response.put("phrase", phraseUpdated);

		return new ResponseEntity<Map<String, Object>>(response, HttpStatus.CREATED);
	}
	
	@DeleteMapping(path="/phrases/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> delete(@PathVariable("id") Long id) throws DatabaseAccessException {
		
		Map<String, Object> response = new HashMap<>();

		try {
			phraseService.delete(id);
		} catch (DataAccessException e) {
			throw new DatabaseAccessException(DatabaseMessagesEnum.DELETE_RECORD.getMessage(), e);
		}

		response.put("msg", messages.getDeleted());

		return new ResponseEntity<Map<String, Object>>(response, HttpStatus.OK);
	}
}
