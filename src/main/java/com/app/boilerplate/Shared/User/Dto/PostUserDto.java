package com.app.boilerplate.Shared.User.Dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Locale;

/**
 * DTO for {@link com.app.boilerplate.Domain.User.User}
 */
@AllArgsConstructor
@Getter
@JsonIgnoreProperties(ignoreUnknown = true)
public class PostUserDto implements Serializable {
	@NotNull
	@Size(max = 50)
	private final String login;
	@NotNull
	@Size(message = "Username must be between {min} and {max} characters long", min = 2, max = 50)
	private final String username;
	@NotNull
	@Size(message = "Email must be at most {max} characters long", max = 50)
	@Email
	private final String email;
	@NotNull
	private final Locale locale;
	@NotNull
	@Size(min = 2, max = 50)
	@NotEmpty
	@NotBlank
	private final String displayName;
	private final String password;
	private final String image;
	private final Boolean enabled;
	private final Boolean blocked;
	private final Boolean shouldChangePasswordOnNextLogin;
	private final Boolean isTwoFactorEnabled;
	private final Boolean lockOutIsLockoutEnabled;
	private final int lockOutAccessFailedCount;
	private final LocalDateTime lockOutLockoutEndDate;
	private final Boolean shouldSendConfirmationEmail;


}
