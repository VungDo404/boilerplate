package com.app.boilerplate.Shared.User.Dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * DTO for {@link com.app.boilerplate.Domain.User.User}
 */
@Getter
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PutUserDto extends UpdateUserDto implements Serializable {
	private final Boolean isTwoFactorEnabled;
	private final Boolean accountNonLocked;
	private final Boolean credentialsNonExpired;
	private final Boolean accountNonExpired;
	private final Boolean lockOutIsLockoutEnabled;
	private final int lockOutAccessFailedCount;
	private final LocalDateTime lockOutLockoutEndDate;


	public PutUserDto(
		@NotNull final UUID id,
		final String displayName,
		final String image,
		final Boolean isTwoFactorEnabled,
		final Boolean accountNonLocked,
		final Boolean credentialsNonExpired,
		final Boolean accountNonExpired,
		final Boolean lockOutIsLockoutEnabled,
		final int lockOutAccessFailedCount,
		final LocalDateTime lockOutLockoutEndDate
	) {
		super(id, displayName, image);
		this.isTwoFactorEnabled = isTwoFactorEnabled;
		this.accountNonLocked = accountNonLocked;
		this.credentialsNonExpired = credentialsNonExpired;
		this.accountNonExpired = accountNonExpired;
		this.lockOutIsLockoutEnabled = lockOutIsLockoutEnabled;
		this.lockOutAccessFailedCount = lockOutAccessFailedCount;
		this.lockOutLockoutEndDate = lockOutLockoutEndDate;
	}
}
