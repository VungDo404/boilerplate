package com.app.boilerplate.Shared.Authorization.Dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.io.Serializable;

@AllArgsConstructor
@Getter
@JsonIgnoreProperties(ignoreUnknown = true)
public class CreateAclClass implements Serializable {
	@NotNull
	private final String type;

	@NotNull
	private final Boolean allowCreate;

	@NotNull
	private final Class idType;
}
