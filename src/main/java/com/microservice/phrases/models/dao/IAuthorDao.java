package com.microservice.phrases.models.dao;

import org.springframework.data.repository.CrudRepository;

import com.microservice.phrases.models.entity.Author;

public interface IAuthorDao extends CrudRepository<Author, Long>{

}
