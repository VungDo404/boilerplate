package com.app.boilerplate.Shared.Dev.Document.Dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.validation.constraints.Positive;
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
public class PutDocumentDto implements Serializable {
	@Positive
	private final int id;
	private final String description;
	private final String title;
	private final boolean isApproved;
	private final LocalDateTime expiration;
}
