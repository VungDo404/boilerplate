package com.app.boilerplate.Shared.Authorization.Dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.security.acls.model.Permission;

import java.io.Serializable;

@AllArgsConstructor
@Getter
@JsonIgnoreProperties(ignoreUnknown = true)
public class AclDto implements Serializable {
	@NotNull
	private final Class<?> type;
	@NotNull
	private final Long objectId;
	@NotNull
	private final Permission permission;
	@NotNull
	private final String sidName;
	@NotNull
	private final Boolean principal;
}
