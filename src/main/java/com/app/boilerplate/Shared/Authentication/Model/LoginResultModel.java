package com.app.boilerplate.Shared.Authentication.Model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
@Builder
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class LoginResultModel {
	private String accessToken;
	private String encryptedAccessToken;
	private int expiresInSeconds;
	private boolean shouldChangePasswordOnNextLogin;
	private String passwordResetCode;
	private boolean isTwoFactorEnabled;
	private boolean requiresTwoFactorVerification;
}
