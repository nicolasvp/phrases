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
import com.microservices.commons.models.entity.phrases.Type;
import com.microservice.phrases.models.services.ITypeService;

@Slf4j
@CrossOrigin(origins = { "http://localhost:4200" })
@RestController
public class TypeController {
	
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

		try {
			log.info(Messages.findObjectMessage("Type", id.toString()));
			type = typeService.findById(id);
		} catch (DataAccessException e) {
			log.error(Messages.errorDatabaseAccessMessage(e.getMessage()));
			throw new DatabaseAccessException(DatabaseMessagesEnum.ACCESS_DATABASE.getMessage(), e);
		}

		// return error if the record non exist
		if (type == null) {
			log.error(Messages.nullObjectMessage("Type", id.toString()));
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
			log.error(Messages.errorsCreatingObjectMessage("Type"));
			response.put("errors", utilService.listErrors(result));
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.BAD_REQUEST);
		}
		
		try {
			log.info(Messages.creatingObjectMessage("Type"));
			newType = typeService.save(type);
		} catch (DataAccessException e) {
			log.error(Messages.errorDatabaseCreateMessage("Type", e.toString()));
			throw new DatabaseAccessException(DatabaseMessagesEnum.STORE_RECORD.getMessage(), e);
		}

		response.put("msg", CrudMessagesEnum.CREATED.getMessage());
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
			log.error(Messages.errorsUpdatingObjectMessage("Type", id.toString()));
			response.put("errors", utilService.listErrors(result));
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.BAD_REQUEST);
		}
		
		// return error if the record non exist
		if (typeFromDB == null) {
			log.error(Messages.nullObjectMessage("Type", id.toString()));
			throw new NullRecordException();
		}

		try {
			log.info(Messages.updatingObjectMessage("Type", id.toString()));
			typeFromDB.setName(type.getName());
			typeUpdated = typeService.save(typeFromDB);
		} catch (DataAccessException e) {
			log.error(Messages.errorDatabaseUpdateMessage("Type", id.toString(), e.getMessage()));
			throw new DatabaseAccessException(DatabaseMessagesEnum.UPDATE_RECORD.getMessage(), e);
		}

		response.put("msg", CrudMessagesEnum.UPDATED.getMessage());
		response.put("type", typeUpdated);

		return new ResponseEntity<Map<String, Object>>(response, HttpStatus.CREATED);
	}
	
	@DeleteMapping(path="/types/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> delete(@PathVariable("id") Long id) throws DatabaseAccessException {
		
		Map<String, Object> response = new HashMap<>();

		try {
			log.info(Messages.deletingObjectMessage("Type", id.toString()));
			typeService.delete(id);
		} catch (DataAccessException e) {
			log.error(Messages.errorDatabaseDeleteMessage("Type", id.toString(), e.getMessage()));
			throw new DatabaseAccessException(DatabaseMessagesEnum.DELETE_RECORD.getMessage(), e);
		}

		response.put("msg", CrudMessagesEnum.DELETED.getMessage());

		return new ResponseEntity<Map<String, Object>>(response, HttpStatus.OK);
	}
}
