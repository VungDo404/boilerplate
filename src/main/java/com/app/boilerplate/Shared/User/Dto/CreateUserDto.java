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
	@NotNull( message = "{validation.password.size}")
	@Size( message = "{validation.password.size}", min = 2, max = 50)
	private final String username;
	@NotNull(message = "{validation.password.required}",groups = RegisterUser.class)
	@Size( message = "{validation.password.size}", min = 6, max = 60, groups = RegisterUser.class)
	private final String password;
	@NotNull(message = "{validation.email.required}")
	@Size(max = 50, message = "{validation.email.size}")
	@Email(message = "{validation.email.invalid}")
	private final String email;
	@NotNull(message = "{validation.displayName.required}")
	@Size(min = 2, max = 50, message = "{validation.displayName.size}")
	private final String displayName;
}
