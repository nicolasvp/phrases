package com.microservice.phrases.models.services;

import java.util.List;

import com.microservice.phrases.models.entity.Image;

public interface IImageService {
	
	public List<Image> findAll();
	
	public Image findById(Long id);
	
	public Image save(Image image);
	
	public void delete(Long id);
}
