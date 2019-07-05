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
	public ResponseEntity<?> show(@PathVariable Long id) {
		
		Type type = null;
		Map<String, Object> response = new HashMap<>();

		try {
			type = typeService.findById(id);
		} catch (DataAccessException e) {
			LOGGER.error("Error al realizar la consulta en la base de datos: " + e.getMessage().concat(": ").concat(e.getMostSpecificCause().getMessage()));
			response.put("msg", "Error al realizar la consulta en la base de datos");
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}

		// return error if the record non exist
		if (type == null) {
			LOGGER.warn("El registro con ID: ".concat(id.toString().concat(" no existe en la base de datos")));
			response.put("msg", "El registro con ID: ".concat(id.toString().concat(" no existe en la base de datos")));
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.NOT_FOUND);
		}

		return new ResponseEntity<Type>(type, HttpStatus.OK);
	}
	
	@PostMapping(path="/types", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> create(@Valid @RequestBody Type type, BindingResult result) {
		
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
			LOGGER.error("Error al intentar guardar el registro: " + e.getMessage().concat(": ").concat(e.getMostSpecificCause().getMessage()));
			response.put("msg", "Error al intentar guardar el registro");
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}

		response.put("msg", "Registro creado con éxito");
		response.put("type", newType);

		return new ResponseEntity<Map<String, Object>>(response, HttpStatus.CREATED);
	}
	
	@PutMapping(path="/types/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> update(@Valid @RequestBody Type type, BindingResult result, @PathVariable("id") Long id) {
		
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
			LOGGER.warn("El registro con ID: ".concat(id.toString().concat(" no existe en la base de datos")));
			response.put("msg", "El registro no existe en la base de datos");
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.NOT_FOUND);
		}

		try {
			typeFromDB.setName(type.getName());
			typeUpdated = typeService.save(typeFromDB);
		} catch (DataAccessException e) {
			LOGGER.error("Error al intentar actualizar el registro en la base de datos: " + e.getMessage().concat(": ").concat(e.getMostSpecificCause().getMessage()));
			response.put("msg", "Error al intentar actualizar el registro en la base de datos");
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}

		response.put("msg", "Registro actualizado con éxito");
		response.put("type", typeUpdated);

		return new ResponseEntity<Map<String, Object>>(response, HttpStatus.CREATED);
	}
	
	@DeleteMapping(path="/types/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> delete(@PathVariable("id") Long id) {
		
		Map<String, Object> response = new HashMap<>();

		try {
			typeService.delete(id);
		} catch (DataAccessException e) {
			LOGGER.error("Error al intentar eliminar el registro de la base de datos: " + e.getMessage().concat(": ").concat(e.getMostSpecificCause().getMessage()));
			response.put("msg", "Error al intentar eliminar el registro de la base de datos");
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}

		response.put("msg", "Registro eliminado con éxito");

		return new ResponseEntity<Map<String, Object>>(response, HttpStatus.OK);
	}
}
