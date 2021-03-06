package com.microservice.phrases.models.services;

import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.microservice.phrases.models.dao.IPhraseDao;
import com.microservices.commons.models.entity.phrases.Phrase;
import com.microservice.phrases.models.services.remote.IUserRemoteCallService;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;

@Slf4j
@Service
public class PhraseServiceImpl implements IPhraseService{
	
	@Autowired
	private IPhraseDao phraseDao;
	
	@Autowired
	private IUserRemoteCallService loadBalancer;
	
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

	@Override
	@HystrixCommand(fallbackMethod = "unavailableMessage")
	public String callUserService() {
		log.info("Invoking users service from phrase service");
		return loadBalancer.getServiceRoute();
	}

	@Override
	public String unavailableMessage() {
		return "Users service is not available";
	}

}
