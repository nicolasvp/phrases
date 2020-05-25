package com.microservice.phrases.models.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.microservice.phrases.models.dao.IImageDao;
import com.microservices.commons.models.entity.phrases.Image;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ImageServiceImpl implements IImageService {

	@Autowired
	private IImageDao imageDao;
	
	@Override
	@Transactional(readOnly = true)
	public List<Image> findAll() {
		return (List<Image>) imageDao.findAll();
	}

	@Override
	@Transactional(readOnly = true)
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
