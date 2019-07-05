package com.microservice.phrases.controllers;

import com.microservice.phrases.models.entity.Image;
import com.microservice.phrases.models.services.IImageService;
import com.microservice.phrases.models.services.IUtilService;
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
@RequestMapping("/api")
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
    public ResponseEntity<?> show(@PathVariable Long id) {

        Image image = null;
        Map<String, Object> response = new HashMap<>();

        try {
            image = imageService.findById(id);
        } catch (DataAccessException e) {
            LOGGER.error("Error al realizar la consulta en la base de datos: " + e.getMessage().concat(": ").concat(e.getMostSpecificCause().getMessage()));
            response.put("msg", "Error al realizar la consulta en la base de datos");
            return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }

        // return error if the record non exist
        if (image == null) {
            LOGGER.warn("El registro con ID: ".concat(id.toString().concat(" no existe en la base de datos")));
            response.put("msg", "El registro con ID: ".concat(id.toString().concat(" no existe en la base de datos")));
            return new ResponseEntity<Map<String, Object>>(response, HttpStatus.NOT_FOUND);
        }

        return new ResponseEntity<Image>(image, HttpStatus.OK);
    }

    @PostMapping(path="/images", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> create(@Valid @RequestBody Image image, BindingResult result) {

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
            LOGGER.error("Error al intentar guardar el registro: " + e.getMessage().concat(": ").concat(e.getMostSpecificCause().getMessage()));
            response.put("msg", "Error al intentar guardar el registro");
            return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }

        response.put("msg", "Registro creado con éxito");
        response.put("image", newImage);

        return new ResponseEntity<Map<String, Object>>(response, HttpStatus.CREATED);
    }

    @PutMapping(path="/images/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> update(@Valid @RequestBody Image image, BindingResult result, @PathVariable("id") Long id) {

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
            LOGGER.warn("El registro con ID: ".concat(id.toString().concat(" no existe en la base de datos")));
            response.put("msg", "El registro no existe en la base de datos");
            return new ResponseEntity<Map<String, Object>>(response, HttpStatus.NOT_FOUND);
        }

        try {
            imageFromDB.setName(image.getName());
            imageUpdated = imageService.save(imageFromDB);
        } catch (DataAccessException e) {
            LOGGER.error("Error al intentar actualizar el registro en la base de datos: " + e.getMessage().concat(": ").concat(e.getMostSpecificCause().getMessage()));
            response.put("msg", "Error al intentar actualizar el registro en la base de datos");
            return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }

        response.put("msg", "Registro actualizado con éxito");
        response.put("image", imageUpdated);

        return new ResponseEntity<Map<String, Object>>(response, HttpStatus.CREATED);
    }

    @DeleteMapping(path="/images/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> delete(@PathVariable("id") Long id) {

        Map<String, Object> response = new HashMap<>();

        try {
            imageService.delete(id);
        } catch (DataAccessException e) {
            LOGGER.error("Error al intentar eliminar el registro de la base de datos: " + e.getMessage().concat(": ").concat(e.getMostSpecificCause().getMessage()));
            response.put("msg", "Error al intentar eliminar el registro de la base de datos");
            return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }

        response.put("msg", "Registro eliminado con éxito");

        return new ResponseEntity<Map<String, Object>>(response, HttpStatus.OK);
    }
}
