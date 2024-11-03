package com.app.boilerplate.Shared.Authentication;

import lombok.Getter;
import org.springframework.security.oauth2.jwt.Jwt;

@Getter
public class RefreshJwt extends BaseJwt {
	public RefreshJwt(Jwt jwt) {
		super(jwt);
	}
}
