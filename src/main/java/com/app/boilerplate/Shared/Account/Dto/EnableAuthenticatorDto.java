package com.app.boilerplate.Shared.Account.Dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@JsonIgnoreProperties(ignoreUnknown = true)
public class EnableAuthenticatorDto implements Serializable {
    @NotNull(message = "{validation.twoFactorCode.provider.required}")
    @Size(max = 6, message = "{validation.twoFactorCode.size}")
    @Pattern(regexp = "\\d*", message = "{validation.twoFactorCode.pattern}")
    private String twoFactorCode;
}
