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

import com.microservice.phrases.enums.DatabaseMessagesEnum;
import com.microservice.phrases.exceptions.DatabaseAccessException;
import com.microservice.phrases.exceptions.NullRecordException;
import com.microservice.phrases.models.entity.Type;
import com.microservice.phrases.models.services.ITypeService;

@CrossOrigin(origins = { "http://localhost:4200" })
@RestController
@RequestMapping("/api")
public class TypeController {

	protected Logger LOGGER = LoggerFactory.getLogger(TypeController.class);
	
	@Autowired
	private ITypeService typeService;

	@Autowired
	private IUtilService utilService;

	@GetMapping(path="/types", produces = MediaType.APPLICATION_JSON_VALUE)
	public List<Type> index(){
		return typeService.findAll();
	}
	
	
	@GetMapping(path="/types/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> show(@PathVariable Long id) throws NullRecordException, DatabaseAccessException {
		
		Type type = null;
		Map<String, Object> response = new HashMap<>();

		try {
			type = typeService.findById(id);
		} catch (DataAccessException e) {
			throw new DatabaseAccessException(DatabaseMessagesEnum.ACCESS_DATABASE.getMessage(), e);
		}

		// return error if the record non exist
		if (type == null) {
			throw new NullRecordException();
		}

		return new ResponseEntity<Type>(type, HttpStatus.OK);
	}
	
	@PostMapping(path="/types", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> create(@Valid @RequestBody Type type, BindingResult result) throws DatabaseAccessException {
		
		Type newType = null;
		Map<String, Object> response = new HashMap<>();

		// if validation fails, list all errors and return them
		if(result.hasErrors()) {
			response.put("errors", utilService.listErrors(result));
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.BAD_REQUEST);
		}
		
		try {
			newType = typeService.save(type);
		} catch (DataAccessException e) {
			throw new DatabaseAccessException(DatabaseMessagesEnum.STORE_RECORD.getMessage(), e);
		}

		response.put("msg", "Registro creado con éxito");
		response.put("type", newType);

		return new ResponseEntity<Map<String, Object>>(response, HttpStatus.CREATED);
	}
	
	@PutMapping(path="/types/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> update(@Valid @RequestBody Type type, BindingResult result, @PathVariable("id") Long id) throws NullRecordException, DatabaseAccessException {
		
		Type typeFromDB = typeService.findById(id);
		Type typeUpdated = null;
		Map<String, Object> response = new HashMap<>();

		// if validation fails, list all errors and return them
		if(result.hasErrors()) {
			response.put("errors", utilService.listErrors(result));
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.BAD_REQUEST);
		}
		
		// return error if the record non exist
		if (typeFromDB == null) {
			throw new NullRecordException();
		}

		try {
			typeFromDB.setName(type.getName());
			typeUpdated = typeService.save(typeFromDB);
		} catch (DataAccessException e) {
			throw new DatabaseAccessException(DatabaseMessagesEnum.UPDATE_RECORD.getMessage(), e);
		}

		response.put("msg", "Registro actualizado con éxito");
		response.put("type", typeUpdated);

		return new ResponseEntity<Map<String, Object>>(response, HttpStatus.CREATED);
	}
	
	@DeleteMapping(path="/types/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> delete(@PathVariable("id") Long id) throws DatabaseAccessException {
		
		Map<String, Object> response = new HashMap<>();

		try {
			typeService.delete(id);
		} catch (DataAccessException e) {
			throw new DatabaseAccessException(DatabaseMessagesEnum.DELETE_RECORD.getMessage(), e);
		}

		response.put("msg", "Registro eliminado con éxito");

		return new ResponseEntity<Map<String, Object>>(response, HttpStatus.OK);
	}
}
