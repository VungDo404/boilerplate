package com.app.boilerplate.Shared.Account.Dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.io.Serializable;
import java.util.UUID;

/**
 * DTO for {@link com.app.boilerplate.Domain.User.User}
 */
@AllArgsConstructor
@Getter
@JsonIgnoreProperties(ignoreUnknown = true)
public class ChangePasswordDto implements Serializable {
	@NotNull(message = "{validation.id.required}")
	private UUID id;
	@NotNull(message = "{validation.password.required}")
	@Size(min = 6, max = 60, message = "{validation.password.size}")
	@Pattern(regexp = "^[\\w!@#$%^&*()\\-+=<>?,.;:'\"{}\\[\\]\\\\/|`~]+$",
			message = "{validation.password.pattern}")
	private String currentPassword;
	@NotNull(message = "{validation.newPassword.required}")
	@Size(min = 6, max = 60, message = "{validation.newPassword.size}")
	@Pattern(regexp = "^[\\w!@#$%^&*()\\-+=<>?,.;:'\"{}\\[\\]\\\\/|`~]+$",
			message = "{validation.newPassword.pattern}")
	private String newPassword;

}
