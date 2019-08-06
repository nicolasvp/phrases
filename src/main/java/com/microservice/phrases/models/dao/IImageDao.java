package com.microservice.phrases.models.dao;

import org.springframework.data.repository.CrudRepository;

import com.microservices.commons.models.entity.phrases.Image;

public interface IImageDao extends CrudRepository<Image, Long>{

}
