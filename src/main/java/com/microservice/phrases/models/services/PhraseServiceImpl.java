package com.microservice.phrases.models.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.microservice.phrases.models.dao.IPhraseDao;
import com.microservice.phrases.models.entity.Phrase;

@Service
public class PhraseServiceImpl implements IPhraseService{

	@Autowired
	private IPhraseDao phraseDao;
	
	@Override
	@Transactional(readOnly = true)
	public List<Phrase> findAll() {
		return (List<Phrase>) phraseDao.findAll();
	}

	@Override
	@Transactional(readOnly = true)
	public Phrase findById(Long id) {
		return phraseDao.findById(id).orElse(null);
	}

	@Override
	public Phrase save(Phrase phrase) {
		return phraseDao.save(phrase);
	}

	@Override
	public void delete(Long id) {
		phraseDao.deleteById(id);
	}

}
