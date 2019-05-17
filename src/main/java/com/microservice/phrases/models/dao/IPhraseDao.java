package com.microservice.phrases.models.dao;

import org.springframework.data.repository.CrudRepository;

import com.microservice.phrases.models.entity.Phrase;

public interface IPhraseDao extends CrudRepository<Phrase, Long>{

}
