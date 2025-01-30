package com.app.boilerplate.Shared.Authorization.Dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.io.Serializable;

@AllArgsConstructor
@Getter
@JsonIgnoreProperties(ignoreUnknown = true)
public class CreateObjectIdentityDto implements Serializable {
	@NotNull
	private Long objectIdClass;
	@NotNull
	private String objectIdIdentity;
	private Long parentObject;
	private Long ownerSid;
	@NotNull
	private boolean entriesInheriting;
}
