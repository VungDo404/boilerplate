package com.app.boilerplate.Exception;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class FieldErrorVM {
	private final String objectName;
	private final String field;
	private final String message;
}
