package com.app.boilerplate.Shared.Authentication.Dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
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
	@NotNull(message = "{validation.username.required}")
	@Size(min = 2, max = 50, message = "{validation.username.size}")
	@Pattern(regexp = "^[\\w!@#$%^&*()\\-+=<>?,.;:'\"{}\\[\\]\\\\/|`~]+$",
			message = "{validation.username.pattern}")
	private final String username;
	@NotNull(message = "{validation.password.required}")
	@Size(min = 6, max = 60, message = "{validation.password.size}")
	@Pattern(regexp = "^[\\w!@#$%^&*()\\-+=<>?,.;:'\"{}\\[\\]\\\\/|`~]+$",
			message = "{validation.password.pattern}")
	private final String password;
	@Size(max = 6, message = "{validation.twoFactorCode.size}")
	@Pattern(regexp = "\\d*", message = "{validation.twoFactorCode.pattern}")
	private final String twoFactorCode;
}
