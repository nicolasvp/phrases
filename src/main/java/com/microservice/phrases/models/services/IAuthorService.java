package com.microservice.phrases.models.services;

import java.util.List;

import com.microservices.commons.models.entity.phrases.Author;

public interface IAuthorService {

	public List<Author> findAll();
	
	public Author findById(Long id);
	
	public Author save(Author author);
	
	public void delete(Long id);
}
