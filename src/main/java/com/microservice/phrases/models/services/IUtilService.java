package com.microservice.phrases.models.services;

import org.springframework.validation.BindingResult;

import java.util.List;

public interface IUtilService {

    public List<String> listErrors(BindingResult result);
}
