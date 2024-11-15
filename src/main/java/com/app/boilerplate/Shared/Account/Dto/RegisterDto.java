package com.app.boilerplate.Shared.Account.Dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.io.Serializable;
import java.util.Locale;

/**
 * DTO for {@link com.app.boilerplate.Domain.User.User}
 */
@AllArgsConstructor
@Getter
@JsonIgnoreProperties(ignoreUnknown = true)
public class RegisterDto implements Serializable {
	@NotNull
	@Size(message = "Email must be at most {max} characters long", max = 50)
	@Email
	private final String login;
	@NotNull
	@Size(message = "Username must be between {min} and {max} characters long", min = 2, max = 50)
	private final String username;
	@NotNull
	@Size(message = "Password must be at least {min} characters long", min = 6)
	private final String password;
	@NotNull
	@Size(message = "Email must be at most {max} characters long", max = 50)
	@Email
	private final String email;
	private final Locale locale;
    @Size(min = 2, max = 10)
    private final String displayName;
}
