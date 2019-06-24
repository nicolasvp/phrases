package com.microservice.phrases.controllers;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
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

	protected Logger LOGGER = Logger.getLogger(PhraseController.class.getName());
	private static final String ERROR = "ERROR";
	
	@Autowired
	private IPhraseService phraseService;
		
	@GetMapping("/phrases")
	public List<Phrase> index(){
		List<Phrase> phrases = phraseService.findAll();
		return phraseService.findAll();
	}
	
	@GetMapping("/service-route")
	public String serviceRoute() {
		return "Hi from phrases service";
	}
	
	@GetMapping("/phrases/users")
	public String users(){
		return phraseService.callUserService();
	}
	
	@GetMapping("/phrases/{id}")
	public ResponseEntity<?> show(@PathVariable Long id) {
		
		Phrase phrase = null;
		Map<String, Object> response = new HashMap<>();

		try {
			phrase = phraseService.findById(id);
		} catch (DataAccessException e) {
			response.put("msg", "Error al realizar la consulta en la base de datos");
			response.put(ERROR, e.getMessage().concat(": ").concat(e.getMostSpecificCause().getMessage()));

			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.OK);
		}

		// return error if the record non exist
		if (phrase == null) {
			response.put("msg", "El registro con ID: ".concat(id.toString().concat(" no existe en la base de datos")));
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.NOT_FOUND);
		}

		return new ResponseEntity<Phrase>(phrase, HttpStatus.OK);
	}
	
	@PostMapping("/phrases")
	public ResponseEntity<?> create(@Valid @RequestBody Phrase phrase, BindingResult result) {
		
		Phrase newPhrase = null;
		Map<String, Object> response = new HashMap<>();

		// if validation fails, list all errors and return them
		if(result.hasErrors()) {
			List<String> errors = result.getFieldErrors()
					.stream()
					.map(err -> "El campo " + err.getField() + " " + err.getDefaultMessage())
					.collect(Collectors.toList());
			
			response.put("errors", errors);
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.BAD_REQUEST);
		}
		
		try {
			newPhrase = phraseService.save(phrase);
		} catch (DataAccessException e) {
			response.put("msg", "Error al intentar guardar el registro");
			response.put(ERROR, e.getMessage().concat(": ").concat(e.getMostSpecificCause().getMessage()));

			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}

		response.put("msg", "Registro creado con éxito");
		response.put("phrase", newPhrase);

		return new ResponseEntity<Map<String, Object>>(response, HttpStatus.CREATED);
	}
	
	@PutMapping("/phrases/{id}")
	public ResponseEntity<?> update(@Valid @RequestBody Phrase phrase, BindingResult result, @PathVariable("id") Long id) {
		
		Phrase phraseFromDB = phraseService.findById(id);
		Phrase phraseUpdated = null;
		Map<String, Object> response = new HashMap<>();

		// if validation fails, list all errors and return them
		if(result.hasErrors()) {
			List<String> errors = result.getFieldErrors()
					.stream()
					.map(err -> "El campo " + err.getField() + " " + err.getDefaultMessage())
					.collect(Collectors.toList());
			
			response.put("errors", errors);
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.BAD_REQUEST);
		}
		
		// return error if the record non exist
		if (phraseFromDB == null) {
			response.put("msg", "El registro no existe en la base de datos");
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.NOT_FOUND);
		}

		try {
			phraseFromDB.setBody(phrase.getBody());
			phraseFromDB.setAuthor(phrase.getAuthor());
			phraseFromDB.setType(phrase.getType());
			phraseUpdated = phraseService.save(phraseFromDB);
		} catch (DataAccessException e) {
			response.put("msg", "Error al intentar actualizar el registro en la base de datos");
			response.put(ERROR, e.getMessage().concat(": ").concat(e.getMostSpecificCause().getMessage()));
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}

		response.put("msg", "Registro actualizado con éxito");
		response.put("phrase", phraseUpdated);

		return new ResponseEntity<Map<String, Object>>(response, HttpStatus.CREATED);
	}
	
	@DeleteMapping("/phrases/{id}")
	public ResponseEntity<?> delete(@PathVariable("id") Long id) {
		
		Map<String, Object> response = new HashMap<>();

		try {
			phraseService.delete(id);
		} catch (DataAccessException e) {
			response.put("msg", "Error al intentar eliminar el registro en la base de datos, el registro no existe");
			response.put(ERROR, e.getMessage().concat(": ").concat(e.getMostSpecificCause().getMessage()));
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}

		response.put("msg", "Registro eliminado con éxito");

		return new ResponseEntity<Map<String, Object>>(response, HttpStatus.OK);
	}
}
