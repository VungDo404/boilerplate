package com.app.boilerplate.Shared.Authentication.Model;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class RefreshAccessTokenModel {
	private String accessToken;
	private int expiresInSeconds;
}
