package com.app.boilerplate.Shared.Authentication;

import com.app.boilerplate.Util.AppConsts;
import lombok.Getter;
import org.springframework.security.oauth2.jwt.Jwt;

import java.util.Optional;

@Getter
public class AccessJwt extends BaseJwt {
	private final String securityStamp;
	private final LoginProvider provider;
	private final String username;

	public AccessJwt(Jwt jwt) {
		super(jwt);
		securityStamp = Optional.ofNullable(jwt.getClaim(AppConsts.SECURITY_STAMP))
			.map(Object::toString)
			.orElseThrow(() -> new IllegalArgumentException("Security stamp is missing"));
		provider = Optional.of(jwt.getClaim(AppConsts.ACCESS_TOKEN_PROVIDER))
			.map(Object::toString)
			.map(LoginProvider::valueOf)
			.orElseThrow(() -> new IllegalArgumentException("Invalid provider"));
		username = Optional.of(jwt.getClaim(AppConsts.ACCESS_TOKEN_USERNAME))
			.map(Object::toString)
			.orElseThrow(() -> new IllegalArgumentException("Username is missing"));

	}
}
