package com.microservice.phrases.models.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.microservice.phrases.models.dao.IImageDao;
import com.microservice.phrases.models.entity.Image;

@Service
public class ImageServiceImpl implements IImageService {

	@Autowired
	private IImageDao imageDao;
	
	@Override
	public List<Image> findAll() {
		return (List<Image>) imageDao.findAll();
	}

	@Override
	public Image findById(Long id) {
		return imageDao.findById(id).orElse(null);
	}

	@Override
	public Image save(Image image) {
		return imageDao.save(image);
	}

	@Override
	public void delete(Long id) {
		imageDao.deleteById(id);
	}

}
