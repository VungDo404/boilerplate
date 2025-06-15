package com.app.boilerplate.Shared.User.Dto;

import com.app.boilerplate.Decorator.EnumValidator.ValidEnum;
import com.app.boilerplate.Shared.Authentication.Gender;
import com.app.boilerplate.Shared.User.IUpdateUserDto;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.io.Serializable;
import java.time.LocalDate;

@AllArgsConstructor
@Getter
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UpdateUserDto implements Serializable, IUpdateUserDto {
	@Size(min = 2, max = 50, message = "{validation.displayName.size}")
	private final String displayName;
	@Size(max = 20, message = "{validation.phone-number.size}")
	private final String phoneNumber;
	@ValidEnum(enumClass = Gender.class, message = "{validation.gender.invalid}")
	private final Gender gender;
	private final LocalDate dateOfBirth;
}
