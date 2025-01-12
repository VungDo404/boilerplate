package com.app.boilerplate.Shared.Dev.Organization.Dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.io.Serializable;

/**
 * DTO for {@link com.app.boilerplate.Domain.Dev.Organization}
 */
@AllArgsConstructor
@Getter
@JsonIgnoreProperties(ignoreUnknown = true)
public class CreateOrganizationDto implements Serializable {
	@NotNull
	private final String displayName;
}
