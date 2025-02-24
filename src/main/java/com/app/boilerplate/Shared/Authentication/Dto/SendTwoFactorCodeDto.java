package com.app.boilerplate.Shared.Authentication.Dto;

import com.app.boilerplate.Decorator.EnumValidator.ValidEnum;
import com.app.boilerplate.Shared.Authentication.TwoFactorProvider;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.UUID;

@AllArgsConstructor
@Getter
@JsonIgnoreProperties(ignoreUnknown = true)
public class SendTwoFactorCodeDto {
    @NotNull(message = "{validation.id.required}")
    private final UUID userId;
    @NotNull(message = "{validation.twoFactorCode.provider.required}")
    @ValidEnum(enumClass = TwoFactorProvider.class, message = "{validation.twoFactorCode.provider.invalid}")
    private final TwoFactorProvider provider;
}
