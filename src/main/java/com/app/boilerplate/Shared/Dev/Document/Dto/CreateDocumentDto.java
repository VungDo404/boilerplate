package com.app.boilerplate.Shared.Dev.Document.Dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * DTO for {@link com.app.boilerplate.Domain.Dev.Document}
 */
@AllArgsConstructor
@Getter
@JsonIgnoreProperties(ignoreUnknown = true)
public class CreateDocumentDto implements Serializable {
	@NotNull
	private final String description;
	@NotNull
	private final String title;
	private final boolean isApproved;
	@NotNull
	private final LocalDateTime expiration;
}
