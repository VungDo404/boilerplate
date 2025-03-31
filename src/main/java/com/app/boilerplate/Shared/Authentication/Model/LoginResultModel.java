package com.app.boilerplate.Shared.Authentication.Model;

import com.app.boilerplate.Shared.Authentication.TwoFactorProvider;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.UUID;

@Getter
@Setter
@Builder
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class LoginResultModel {
    private String accessToken;
    private Integer expiresInSeconds;
    private Boolean shouldChangePasswordOnNextLogin;
    private String passwordResetCode;
    private Boolean isTwoFactorEnabled;
    private Boolean requiresEmailVerification;
    private List<TwoFactorProvider> twoFactorProviders;
    private String email;
    private UUID userId;
}
