package com.app.boilerplate.Shared.Authorization.Dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.io.Serializable;
import java.util.UUID;

@AllArgsConstructor
@Getter
@JsonIgnoreProperties(ignoreUnknown = true)
public class CreateAuthorityDto implements Serializable {
	private final UUID userId;
	private final Long sid;
	private final Integer priority;
}
