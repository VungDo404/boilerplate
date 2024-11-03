package com.app.boilerplate.Shared.Authentication;

import com.app.boilerplate.Util.AppConsts;
import lombok.Getter;
import org.springframework.security.oauth2.jwt.Jwt;

import java.util.Optional;
import java.util.UUID;

@Getter
public class BaseJwt extends Jwt {
	private final TokenType type;
	private final UUID jti;
	private final UUID sub;
	public BaseJwt(Jwt jwt) {
		super(jwt.getTokenValue(), jwt.getIssuedAt(), jwt.getExpiresAt(), jwt.getHeaders(), jwt.getClaims());
		type = Optional.ofNullable(jwt.getClaim(AppConsts.TOKEN_TYPE))
			.filter(value -> value instanceof Number)
			.map(value -> ((Number) value).intValue())
			.map(value -> TokenType.values()[value] )
			.orElseThrow(() -> new IllegalArgumentException("Invalid TokenType claim."));
		jti = UUID.fromString(jwt.getId());
		sub = UUID.fromString(jwt.getSubject());
	}
}
