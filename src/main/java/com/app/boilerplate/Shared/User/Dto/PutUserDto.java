package com.app.boilerplate.Shared.User.Dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * DTO for {@link com.app.boilerplate.Domain.User.User}
 */
@AllArgsConstructor
@Getter
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PutUserDto implements Serializable {
	@NotNull
	private final UUID id;
	private final String displayName;
	private final String image;
	private final Boolean isTwoFactorEnabled;
	private final Boolean accountNonLocked;
	private final Boolean credentialsNonExpired;
	private final Boolean accountNonExpired;
	private final Boolean lockOutIsLockoutEnabled;
	private final int lockOutAccessFailedCount;
	private final LocalDateTime lockOutLockoutEndDate;
}
