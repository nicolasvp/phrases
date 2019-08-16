package com.microservice.phrases.controllers;

import com.microservices.commons.exceptions.DatabaseAccessException;
import com.microservices.commons.exceptions.NullRecordException;
import com.microservices.commons.enums.CrudMessagesEnum;
import com.microservices.commons.enums.DatabaseMessagesEnum;
import com.microservices.commons.models.entity.phrases.Image;
import com.microservice.phrases.models.services.IImageService;
import com.microservices.commons.models.services.IUtilService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@CrossOrigin(origins = { "http://localhost:4200" })
@RestController
public class ImageController {

    protected Logger LOGGER = LoggerFactory.getLogger(ImageController.class);

    @Autowired
    private IImageService imageService;

    @Autowired
    private IUtilService utilService;

    @GetMapping(path="/images", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<Image> index(){
        return imageService.findAll();
    }

    @GetMapping(path="/images/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> show(@PathVariable Long id) throws NullRecordException, DatabaseAccessException {

        Image image = null;

        try {
            image = imageService.findById(id);
        } catch (DataAccessException e) {
        	throw new DatabaseAccessException(DatabaseMessagesEnum.ACCESS_DATABASE.getMessage(), e);
        }

        // return error if the record non exist
        if (image == null) {
        	throw new NullRecordException();
        }

        return new ResponseEntity<Image>(image, HttpStatus.OK);
    }

    @PostMapping(path="/images", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> create(@Valid @RequestBody Image image, BindingResult result) throws DatabaseAccessException {

        Image newImage = null;
        Map<String, Object> response = new HashMap<>();

        // if validation fails, list all errors and return them
        if(result.hasErrors()) {
            response.put("errors", utilService.listErrors(result));
            return new ResponseEntity<Map<String, Object>>(response, HttpStatus.BAD_REQUEST);
        }

        try {
            newImage = imageService.save(image);
        } catch (DataAccessException e) {
        	throw new DatabaseAccessException(DatabaseMessagesEnum.STORE_RECORD.getMessage(), e);
        }

        response.put("msg", CrudMessagesEnum.CREATED.getMessage());
        response.put("image", newImage);

        return new ResponseEntity<Map<String, Object>>(response, HttpStatus.CREATED);
    }

    @PutMapping(path="/images/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> update(@Valid @RequestBody Image image, BindingResult result, @PathVariable("id") Long id) throws NullRecordException, DatabaseAccessException {

        Image imageFromDB = imageService.findById(id);
        Image imageUpdated = null;
        Map<String, Object> response = new HashMap<>();

        // if validation fails, list all errors and return them
        if(result.hasErrors()) {
            response.put("errors", utilService.listErrors(result));
            return new ResponseEntity<Map<String, Object>>(response, HttpStatus.BAD_REQUEST);
        }

        // return error if the record non exist
        if (imageFromDB == null) {
        	throw new NullRecordException();
        }

        try {
            imageFromDB.setName(image.getName());
            imageUpdated = imageService.save(imageFromDB);
        } catch (DataAccessException e) {
        	throw new DatabaseAccessException(DatabaseMessagesEnum.UPDATE_RECORD.getMessage(), e);
        }

        response.put("msg", CrudMessagesEnum.UPDATED.getMessage());
        response.put("image", imageUpdated);

        return new ResponseEntity<Map<String, Object>>(response, HttpStatus.CREATED);
    }

    @DeleteMapping(path="/images/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> delete(@PathVariable("id") Long id) throws DatabaseAccessException {

        Map<String, Object> response = new HashMap<>();

        try {
            imageService.delete(id);
        } catch (DataAccessException e) {
        	throw new DatabaseAccessException(DatabaseMessagesEnum.DELETE_RECORD.getMessage(), e);
        }

        response.put("msg", CrudMessagesEnum.DELETED.getMessage());

        return new ResponseEntity<Map<String, Object>>(response, HttpStatus.OK);
    }
}
