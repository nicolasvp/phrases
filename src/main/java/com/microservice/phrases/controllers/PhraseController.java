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
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.microservice.phrases.models.entity.Phrase;
import com.microservice.phrases.models.services.IPhraseService;

@RestController
@RequestMapping("/api")
public class PhraseController {

	protected Logger LOGGER = LoggerFactory.getLogger(PhraseController.class);
	
	@Autowired
	private IPhraseService phraseService;

	@Autowired
	private IUtilService utilService;

	@GetMapping(path="/phrases", produces = MediaType.APPLICATION_JSON_VALUE)
	public List<Phrase> index(){
		List<Phrase> phrases = phraseService.findAll();
		return phraseService.findAll();
	}

	@GetMapping("/phrases/users")
	public String users(){
		return phraseService.callUserService();
	}
	
	@GetMapping(path="/phrases/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> show(@PathVariable Long id) {
		
		Phrase phrase = null;
		Map<String, Object> response = new HashMap<>();

		try {
			phrase = phraseService.findById(id);
		} catch (DataAccessException e) {
			LOGGER.error("Error al realizar la consulta en la base de datos: " + e.getMessage().concat(": ").concat(e.getMostSpecificCause().getMessage()));
			response.put("msg", "Error al realizar la consulta en la base de datos");
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}

		// return error if the record non exist
		if (phrase == null) {
			LOGGER.warn("El registro con ID: ".concat(id.toString().concat(" no existe en la base de datos")));
			response.put("msg", "El registro con ID: ".concat(id.toString().concat(" no existe en la base de datos")));
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.NOT_FOUND);
		}

		return new ResponseEntity<Phrase>(phrase, HttpStatus.OK);
	}
	
	@PostMapping(path="/phrases", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> create(@Valid @RequestBody Phrase phrase, BindingResult result) {
		
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
			LOGGER.error("Error al intentar guardar el registro: " + e.getMessage().concat(": ").concat(e.getMostSpecificCause().getMessage()));
			response.put("msg", "Error al intentar guardar el registro");
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}

		response.put("msg", "Registro creado con éxito");
		response.put("phrase", newPhrase);

		return new ResponseEntity<Map<String, Object>>(response, HttpStatus.CREATED);
	}
	
	@PutMapping(path="/phrases/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> update(@Valid @RequestBody Phrase phrase, BindingResult result, @PathVariable("id") Long id) {
		
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
			LOGGER.warn("El registro con ID: ".concat(id.toString().concat(" no existe en la base de datos")));
			response.put("msg", "El registro no existe en la base de datos");
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.NOT_FOUND);
		}

		try {
			phraseFromDB.setBody(phrase.getBody());
			phraseFromDB.setAuthor(phrase.getAuthor());
			phraseFromDB.setType(phrase.getType());
			phraseUpdated = phraseService.save(phraseFromDB);
		} catch (DataAccessException e) {
			LOGGER.error("Error al intentar actualizar el registro en la base de datos: " + e.getMessage().concat(": ").concat(e.getMostSpecificCause().getMessage()));
			response.put("msg", "Error al intentar actualizar el registro en la base de datos");
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}

		response.put("msg", "Registro actualizado con éxito");
		response.put("phrase", phraseUpdated);

		return new ResponseEntity<Map<String, Object>>(response, HttpStatus.CREATED);
	}
	
	@DeleteMapping(path="/phrases/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> delete(@PathVariable("id") Long id) {
		
		Map<String, Object> response = new HashMap<>();

		try {
			phraseService.delete(id);
		} catch (DataAccessException e) {
			LOGGER.error("Error al intentar eliminar el registro de la base de datos: " + e.getMessage().concat(": ").concat(e.getMostSpecificCause().getMessage()));
			response.put("msg", "Error al intentar eliminar el registro de la base de datos");
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}

		response.put("msg", "Registro eliminado con éxito");

		return new ResponseEntity<Map<String, Object>>(response, HttpStatus.OK);
	}
}
