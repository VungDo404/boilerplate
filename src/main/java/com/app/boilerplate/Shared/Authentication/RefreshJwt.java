package com.app.boilerplate.Shared.Authentication;

import com.app.boilerplate.Util.AppConsts;
import lombok.Getter;
import org.springframework.security.oauth2.jwt.Jwt;

import java.util.Optional;

@Getter
public class RefreshJwt extends BaseJwt {
	private final String accessJti;
	public RefreshJwt(Jwt jwt) {
		super(jwt);
		accessJti = Optional.of(jwt.getClaim(AppConsts.ACCESS_TOKEN_ID))
			.map(Object::toString)
			.orElseThrow(() -> new IllegalArgumentException("Access Token Id is missing"));
	}
}
