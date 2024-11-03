package com.app.boilerplate.Shared.Authentication;

import com.app.boilerplate.Util.AppConsts;
import lombok.Getter;
import org.springframework.security.oauth2.jwt.Jwt;

import java.util.Optional;

@Getter
public class AccessJwt extends BaseJwt{
	private final String securityStamp;
	public AccessJwt(Jwt jwt) {
		super(jwt);
		securityStamp = Optional.ofNullable(jwt.getClaim(AppConsts.SECURITY_STAMP))
			.map(Object::toString)
			.orElseThrow(() -> new IllegalArgumentException("Security stamp is missing"));

	}
}
