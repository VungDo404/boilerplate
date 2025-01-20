package com.app.boilerplate.Shared.User.Dto;

import com.app.boilerplate.Shared.Account.Group.RegisterUser;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.Email;
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
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CreateUserDto implements Serializable {
	@NotNull
	@Size(message = "Username must be between {min} and {max} characters long", min = 2, max = 50)
	private final String username;
	@NotNull(groups = RegisterUser.class)
	@Size(message = "Password must be at least {min} characters long", min = 6, groups = RegisterUser.class)
	private final String password;
	@NotNull
	@Size(message = "Email must be at most {max} characters long", max = 50)
	@Email
	private final String email;
	@NotNull
	@Size(min = 2, max = 10)
	private final String displayName;
}
