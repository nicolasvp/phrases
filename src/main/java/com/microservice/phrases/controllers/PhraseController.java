package com.microservice.phrases.controllers;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.validation.Valid;
import com.microservices.commons.models.services.IUtilService;
import com.microservices.commons.utils.Messages;
import lombok.extern.slf4j.Slf4j;
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
import org.springframework.web.bind.annotation.RestController;

import com.microservices.commons.enums.CrudMessagesEnum;
import com.microservices.commons.enums.DatabaseMessagesEnum;
import com.microservices.commons.exceptions.DatabaseAccessException;
import com.microservices.commons.exceptions.NullRecordException;
import com.microservices.commons.models.entity.phrases.Phrase;
import com.microservice.phrases.models.services.IPhraseService;

@Slf4j
@CrossOrigin(origins = { "http://localhost:4200" })
@RestController
public class PhraseController {
	
	@Autowired
	private IPhraseService phraseService;

	@Autowired
	private IUtilService utilService;

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
			log.info(Messages.findObjectMessage("Phrase", id.toString()));
			phrase = phraseService.findById(id);
		} catch (DataAccessException e) {
			log.error(Messages.errorDatabaseAccessMessage(e.getMessage()));
			throw new DatabaseAccessException(DatabaseMessagesEnum.ACCESS_DATABASE.getMessage(), e);
		}

		// return error if the record non exist
		if (phrase == null) {
			log.error(Messages.nullObjectMessage("Phrase", id.toString()));
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
			log.error(Messages.errorsCreatingObjectMessage("Phrase"));
			response.put("errors", utilService.listErrors(result));
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.BAD_REQUEST);
		}
		
		try {
			log.info(Messages.creatingObjectMessage("Phrase"));
			newPhrase = phraseService.save(phrase);
		} catch (DataAccessException e) {
			log.error(Messages.errorDatabaseCreateMessage("Phrase", e.toString()));
			throw new DatabaseAccessException(DatabaseMessagesEnum.STORE_RECORD.getMessage(), e);
		}

		response.put("msg", CrudMessagesEnum.CREATED.getMessage());
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
			log.error(Messages.errorsUpdatingObjectMessage("Phrase", id.toString()));
			response.put("errors", utilService.listErrors(result));
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.BAD_REQUEST);
		}
		
		// return error if the record non exist
		if (phraseFromDB == null) {
			log.error(Messages.nullObjectMessage("Phrase", id.toString()));
			throw new NullRecordException();
		}

		try {
			log.info(Messages.updatingObjectMessage("Phrase", id.toString()));
			phraseFromDB.setBody(phrase.getBody());
			phraseFromDB.setAuthor(phrase.getAuthor());
			phraseFromDB.setType(phrase.getType());
			phraseUpdated = phraseService.save(phraseFromDB);
		} catch (DataAccessException e) {
			log.error(Messages.errorDatabaseUpdateMessage("Phrase", id.toString(), e.getMessage()));
			throw new DatabaseAccessException(DatabaseMessagesEnum.UPDATE_RECORD.getMessage(), e);
		}

		response.put("msg", CrudMessagesEnum.UPDATED.getMessage());
		response.put("phrase", phraseUpdated);

		return new ResponseEntity<Map<String, Object>>(response, HttpStatus.CREATED);
	}
	
	@DeleteMapping(path="/phrases/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> delete(@PathVariable("id") Long id) throws DatabaseAccessException {
		
		Map<String, Object> response = new HashMap<>();

		try {
			log.info(Messages.deletingObjectMessage("Phrase", id.toString()));
			phraseService.delete(id);
		} catch (DataAccessException e) {
			log.error(Messages.errorDatabaseDeleteMessage("Phrase", id.toString(), e.getMessage()));
			throw new DatabaseAccessException(DatabaseMessagesEnum.DELETE_RECORD.getMessage(), e);
		}

		response.put("msg", CrudMessagesEnum.DELETED.getMessage());

		return new ResponseEntity<Map<String, Object>>(response, HttpStatus.OK);
	}
}
