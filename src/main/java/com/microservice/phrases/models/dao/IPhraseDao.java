package com.microservice.phrases.models.dao;

import org.springframework.data.repository.CrudRepository;

import com.microservices.commons.models.entity.phrases.Phrase;

public interface IPhraseDao extends CrudRepository<Phrase, Long>{

}
