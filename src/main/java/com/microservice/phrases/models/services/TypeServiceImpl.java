package com.microservice.phrases.models.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.microservice.phrases.models.dao.ITypeDao;
import com.microservices.commons.models.entity.phrases.Type;

@Service
public class TypeServiceImpl implements ITypeService {

	@Autowired
	private ITypeDao typeDao;
	
	@Override
	@Transactional(readOnly=true)
	public List<Type> findAll() {
		return (List<Type>) typeDao.findAll();
	}

	@Override
	@Transactional(readOnly=true)
	public Type findById(Long id) {
		return typeDao.findById(id).orElse(null);
	}

	@Override
	public Type save(Type type) {
		return typeDao.save(type);
	}

	@Override
	public void delete(Long id) {
		typeDao.deleteById(id);
	}

}
