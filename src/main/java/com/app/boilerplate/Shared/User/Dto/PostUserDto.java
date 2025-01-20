package com.app.boilerplate.Shared.User.Dto;

import lombok.Getter;

import java.time.LocalDateTime;

/**
 * DTO for {@link com.app.boilerplate.Domain.User.User}
 */
@Getter
public class PostUserDto extends CreateUserDto {
	private final String image;
	private final Boolean enabled;
	private final Boolean blocked;
	private final Boolean shouldChangePasswordOnNextLogin;
	private final Boolean isTwoFactorEnabled;
	private final Boolean lockOutIsLockoutEnabled;
	private final int lockOutAccessFailedCount;
	private final LocalDateTime lockOutLockoutEndDate;
	private final Boolean shouldSendConfirmationEmail;


	public PostUserDto(
		String username,
		String password,
		String email,
		String displayName,
		String image,
		Boolean enabled,
		Boolean blocked,
		Boolean shouldChangePasswordOnNextLogin,
		Boolean isTwoFactorEnabled,
		Boolean lockOutIsLockoutEnabled,
		int lockOutAccessFailedCount,
		LocalDateTime lockOutLockoutEndDate,
		Boolean shouldSendConfirmationEmail
	) {
		super(username, password, email, displayName);
		this.image = image;
		this.enabled = enabled;
		this.blocked = blocked;
		this.shouldChangePasswordOnNextLogin = shouldChangePasswordOnNextLogin;
		this.isTwoFactorEnabled = isTwoFactorEnabled;
		this.lockOutIsLockoutEnabled = lockOutIsLockoutEnabled;
		this.lockOutAccessFailedCount = lockOutAccessFailedCount;
		this.lockOutLockoutEndDate = lockOutLockoutEndDate;
		this.shouldSendConfirmationEmail = shouldSendConfirmationEmail;
	}
}
