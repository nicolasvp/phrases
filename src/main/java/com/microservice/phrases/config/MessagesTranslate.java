package com.microservice.phrases.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration
@EnableConfigurationProperties
@ConfigurationProperties
@PropertySource("classpath:translate.properties")
public class MessagesTranslate {

	@Value("${messages.crud.created}")
	private String created;

	@Value("${messages.crud.updated}")
	private String updated;

	@Value("${messages.crud.deleted}")
	private String deleted;

	public String getCreated() {
		return created;
	}

	public String getUpdated() {
		return updated;
	}

	public String getDeleted() {
		return deleted;
	}
}
