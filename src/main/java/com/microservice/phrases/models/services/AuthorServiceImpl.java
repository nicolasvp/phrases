package com.microservice.phrases.models.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.microservice.phrases.models.dao.IAuthorDao;
import com.microservices.commons.models.entity.phrases.Author;

@Service
public class AuthorServiceImpl implements IAuthorService {

	@Autowired
	private IAuthorDao authorDao;
	
	@Override
	@Transactional(readOnly = true)
	public List<Author> findAll() {
		return (List<Author>) authorDao.findAll();
	}

	@Override
	@Transactional(readOnly = true)
	public Author findById(Long id) {
		return authorDao.findById(id).orElse(null);
	}

	@Override
	public Author save(Author author) {
		return authorDao.save(author);
	}

	@Override
	public void delete(Long id) {
		authorDao.deleteById(id);
	}

}
