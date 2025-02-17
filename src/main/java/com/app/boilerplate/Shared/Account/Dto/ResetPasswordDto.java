package com.app.boilerplate.Shared.Account.Dto;

import com.app.boilerplate.Shared.Account.Group.RegisterUser;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ResetPasswordDto {
    @NotNull(message = "{validation.password.required}",groups = RegisterUser.class)
    @Size( message = "{validation.password.size}", min = 6, max = 60, groups = RegisterUser.class)
    @Pattern(regexp = "^[\\w!@#$%^&*()\\-+=<>?,.;:'\"{}\\[\\]\\\\/|`~]+$",
            message = "{validation.username.pattern}")
    private String password;

}
