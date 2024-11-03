package com.app.boilerplate.Shared.User.Dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
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
public class PutUserDto implements Serializable {
	@NotNull
	private UUID id;
	@NotNull
	@Size(message = "Username must be between {min} and {max} characters long", min = 2, max = 50)
	private final String username;
	private final String image;
	private final Boolean blocked;
	private final Boolean shouldChangePasswordOnNextLogin;
	private final Boolean isTwoFactorEnabled;
	private final Boolean lockOutIsLockoutEnabled;
	private final int lockOutAccessFailedCount;
	private final LocalDateTime lockOutLockoutEndDate;
}
