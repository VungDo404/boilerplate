package com.app.boilerplate.Shared.Authorization.Dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.io.Serializable;

@AllArgsConstructor
@Getter
@JsonIgnoreProperties(ignoreUnknown = true)
public class CreateSidDto implements Serializable {
	@NotNull
	private final String sidName;
	@NotNull
	private final Boolean sidIsPrincipal;
	@NotNull
	private final Boolean allowCreate;
}
