package com.app.boilerplate.Shared.Authentication.Dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.io.Serializable;

/**
 * DTO for {@link com.app.boilerplate.Domain.User.User}
 */
@AllArgsConstructor
@Getter
@JsonIgnoreProperties(ignoreUnknown = true)
public class LoginDto implements Serializable {
	@NotNull
	@Size(max = 50)
	private final String username;
	@NotNull
	@Size(message = "Password must be between {min} and {max} characters long", min = 6)
	private final String password;
}
