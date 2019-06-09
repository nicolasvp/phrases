package com.microservice.phrases.models.dao;

import org.springframework.data.repository.CrudRepository;

import com.microservice.phrases.models.entity.Image;

public interface IImageDao extends CrudRepository<Image, Long>{

}
