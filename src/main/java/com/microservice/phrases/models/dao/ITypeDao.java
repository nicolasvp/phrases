package com.microservice.phrases.models.dao;

import org.springframework.data.repository.CrudRepository;

import com.microservices.commons.models.entity.phrases.Type;

public interface ITypeDao extends CrudRepository<Type, Long>{

}
