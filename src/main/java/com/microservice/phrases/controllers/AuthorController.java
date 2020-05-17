package com.microservice.phrases.controllers;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.validation.Valid;
import com.microservices.commons.models.services.IUtilService;
import com.microservices.commons.enums.CrudMessagesEnum;
import com.microservices.commons.enums.DatabaseMessagesEnum;
import com.microservices.commons.exceptions.DatabaseAccessException;
import com.microservices.commons.exceptions.NullRecordException;
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

import com.microservices.commons.models.entity.phrases.Author;
import com.microservice.phrases.models.services.IAuthorService;

@Slf4j
@CrossOrigin(origins = { "http://localhost:4200" })
@RestController
public class AuthorController {
	
	@Autowired
	private IAuthorService authorService;

	@Autowired
	private IUtilService utilService;

	@GetMapping(path="/authors", produces = MediaType.APPLICATION_JSON_VALUE)
	public List<Author> index(){
		return authorService.findAll();
	}
	
	@GetMapping(path="/authors/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> show(@PathVariable Long id) throws NullRecordException, DatabaseAccessException {

		Author author = null;

		try {
			log.info(Messages.findObjectMessage("Author", id.toString()));
			author = authorService.findById(id);
		} catch (DataAccessException e) {
			log.error(Messages.errorDatabaseAccessMessage(e.getMessage()));
			throw new DatabaseAccessException(DatabaseMessagesEnum.ACCESS_DATABASE.getMessage(), e);
		}

		// return error if the record non exist
		if (author == null) {
			log.error(Messages.nullObjectMessage("Author", id.toString()));
			throw new NullRecordException();
		}

		return new ResponseEntity<Author>(author, HttpStatus.OK);
	}
	
	@PostMapping(path="/authors", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> create(@Valid @RequestBody Author author, BindingResult result) throws DatabaseAccessException {
		
		Author newAuthor = null;
		Map<String, Object> response = new HashMap<>();

		// if validation fails, list all errors and return them
		if(result.hasErrors()) {
			log.error(Messages.errorsCreatingObjectMessage("Author"));
			response.put("errors", utilService.listErrors(result));
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.BAD_REQUEST);
		}
		
		try {
			log.info(Messages.creatingObjectMessage("Author"));
			newAuthor = authorService.save(author);
		} catch (DataAccessException e) {
			log.error(Messages.errorDatabaseCreateMessage("Author", e.toString()));
			throw new DatabaseAccessException(DatabaseMessagesEnum.STORE_RECORD.getMessage(), e);
		}

		response.put("msg", CrudMessagesEnum.CREATED.getMessage());
		response.put("author", newAuthor);

		return new ResponseEntity<Map<String, Object>>(response, HttpStatus.CREATED);
	}
	
	@PutMapping(path="/authors/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> update(@Valid @RequestBody Author author, BindingResult result, @PathVariable("id") Long id) throws NullRecordException, DatabaseAccessException {
		
		Author authorFromDB = authorService.findById(id);
		Author authorUpdated = null;
		Map<String, Object> response = new HashMap<>();

		// if validation fails, list all errors and return them
		if(result.hasErrors()) {
			log.error(Messages.errorsUpdatingObjectMessage("Author", id.toString()));
			response.put("errors", utilService.listErrors(result));
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.BAD_REQUEST);
		}
		
		// return error if the record non exist
		if (authorFromDB == null) {
			log.error(Messages.nullObjectMessage("Author", id.toString()));
			throw new NullRecordException();
		}

		try {
			log.info(Messages.updatingObjectMessage("Author", id.toString()));
			authorFromDB.setName(author.getName());
			authorUpdated = authorService.save(authorFromDB);
		} catch (DataAccessException e) {
			log.error(Messages.errorDatabaseUpdateMessage("Author", id.toString(), e.getMessage()));
			throw new DatabaseAccessException(DatabaseMessagesEnum.UPDATE_RECORD.getMessage(), e);
		}

		response.put("msg", CrudMessagesEnum.UPDATED.getMessage());
		response.put("author", authorUpdated);

		return new ResponseEntity<Map<String, Object>>(response, HttpStatus.CREATED);
	}
	
	@DeleteMapping(path="/authors/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> delete(@PathVariable("id") Long id) throws DatabaseAccessException {
		
		Map<String, Object> response = new HashMap<>();

		try {
			log.info(Messages.deletingObjectMessage("Author", id.toString()));
			authorService.delete(id);
		} catch (DataAccessException e) {
			log.error(Messages.errorDatabaseDeleteMessage("Author", id.toString(), e.getMessage()));
			throw new DatabaseAccessException(DatabaseMessagesEnum.DELETE_RECORD.getMessage(), e);
		}

		response.put("msg", CrudMessagesEnum.DELETED.getMessage());

		return new ResponseEntity<Map<String, Object>>(response, HttpStatus.OK);
	}
}
