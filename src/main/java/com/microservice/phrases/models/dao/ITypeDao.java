package com.microservice.phrases.models.dao;

import org.springframework.data.repository.CrudRepository;

import com.microservice.phrases.models.entity.Type;

public interface ITypeDao extends CrudRepository<Type, Long>{

}
