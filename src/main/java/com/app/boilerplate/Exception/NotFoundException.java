package com.app.boilerplate.Exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.ErrorResponseException;


public class NotFoundException extends ErrorResponseException {

	public NotFoundException(String message, String messageDetailCode, Object... args) {
		super(
			HttpStatus.NOT_FOUND,
			ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, message),
			null,
			messageDetailCode,
			args
		);

	}
}
