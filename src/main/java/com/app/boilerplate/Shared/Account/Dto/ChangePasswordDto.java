package com.app.boilerplate.Shared.Account.Dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.hibernate.validator.constraints.UUID;

import java.io.Serializable;

/**
 * DTO for {@link com.app.boilerplate.Domain.User.User}
 */
@AllArgsConstructor
@Getter
@JsonIgnoreProperties(ignoreUnknown = true)
public class ChangePasswordDto implements Serializable {
	@NotNull
	@UUID
	private java.util.UUID id;
	@NotNull
	@Size(message = "Password must be at least {min} characters long", min = 6)
	private String currentPassword;
	@NotNull
	@Size(message = "Password must be at least {min} characters long", min = 6)
	private String newPassword;

}
