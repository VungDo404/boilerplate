package com.app.boilerplate.Shared.Common.Dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EntityChangeDto {
	private String propertyName;
	private Object previousValue;
	private Object newValue;
}
