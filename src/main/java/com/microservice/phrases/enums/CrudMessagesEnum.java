package com.microservice.phrases.enums;

public enum CrudMessagesEnum {

	CREATED("Record succesfully created", 1),
	UPDATED("Record succesfully updated", 2),
	DELETED("Record succesfully deleted", 3);

	private String message;
	
	CrudMessagesEnum(String message, int ordinal) {
		this.message = message;
	}

	public String getMessage() {
		return message;
	}
}
