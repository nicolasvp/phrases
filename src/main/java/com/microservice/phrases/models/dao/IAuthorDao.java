package com.microservice.phrases.models.dao;

import org.springframework.data.repository.CrudRepository;

import com.microservices.commons.models.entity.phrases.Author;

public interface IAuthorDao extends CrudRepository<Author, Long>{

}
