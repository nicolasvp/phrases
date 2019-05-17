package com.microservice.phrases.models.services;

import java.util.List;

import com.microservice.phrases.models.entity.Type;

public interface ITypeService {

	public List<Type> findAll();
	
	public Type findById(Long id);

	public Type save(Type type);
	
	public void delete(Long id);
}
