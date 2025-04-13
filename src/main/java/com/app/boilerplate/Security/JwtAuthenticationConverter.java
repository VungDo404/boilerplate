package com.app.boilerplate.Security;

import com.app.boilerplate.Service.Authorization.AuthorizeService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Component;

import java.util.Collection;

@RequiredArgsConstructor
@Component
public class JwtAuthenticationConverter implements Converter<Jwt, AbstractAuthenticationToken> {
	private final AuthorizeService authorizeService;

	@Override
	public AbstractAuthenticationToken convert(Jwt jwt) {
		Collection<GrantedAuthority> authorities = extractAuthorities(jwt.getSubject());
		return new JwtAuthenticationToken(jwt, authorities);
	}

	private Collection<GrantedAuthority> extractAuthorities(String sid) {
		return authorizeService.getSidGrantedAuthorities(sid);
	}
}
